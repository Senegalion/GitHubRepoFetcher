package org.example.githubrepofetcher.infrastructure.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubrepofetcher.domain.model.dto.api.BranchDto;
import org.example.githubrepofetcher.domain.model.dto.github.GitHubBranchResponseDto;
import org.example.githubrepofetcher.domain.model.dto.github.GitHubRepositoryResponseDto;
import org.example.githubrepofetcher.domain.model.dto.api.GithubRepositoryDto;
import org.example.githubrepofetcher.domain.service.GitHubFetcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class GitHubFetcherRestTemplate implements GitHubFetcher {
    private final RestTemplate restTemplate;
    private final String uri;
    private final String githubToken;

    @Override
    public List<GithubRepositoryDto> fetchGitHubRepositories(String username) {
        log.info("Fetching GitHub repositories for user: {}", username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        try {
            List<GitHubRepositoryResponseDto> repos = getUserRepositories(username, requestEntity);
            return repos.stream()
                    .filter(repo -> !repo.fork())
                    .map(this::mapToGithubRepositoryDto)
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            log.error("Error while fetching GitHub repositories: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Network error while fetching repositories");
        }
    }

    private List<GitHubRepositoryResponseDto> getUserRepositories(String username, HttpEntity<HttpHeaders> requestEntity) {
        checkIfUserExists(username, requestEntity);

        final String url = UriComponentsBuilder.fromUriString(uri + "/users/" + username + "/repos")
                .toUriString();
        try {
            ResponseEntity<List<GitHubRepositoryResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<GitHubRepositoryResponseDto> repos = response.getBody();
            if (repos.isEmpty()) {
                log.warn("No repositories found for user: {}", username);
            }

            return repos;
        } catch (ResponseStatusException e) {
            log.error("Error occurred while fetching repositories: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GitHub API is unavailable");
        }
    }

    private void checkIfUserExists(String username, HttpEntity<HttpHeaders> requestEntity) {
        final String url = UriComponentsBuilder.fromUriString(uri + "/users/" + username)
                .toUriString();

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Void.class
            );
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("User {} not found on GitHub", username);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GitHub user '" + username + "' not found");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.warn("Unauthorized when accessing user {}.", username);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("Too many requests for user {}", username);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests to GitHub API");
            } else {
                log.error("Error while checking if user exists: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "GitHub API is unavailable");
            }
        } catch (ResourceAccessException e) {
            log.error("Connection error while checking user existence: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "GitHub API is unavailable");
        } catch (Exception e) {
            log.error("Unexpected error while checking user existence: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        }
    }

    private GithubRepositoryDto mapToGithubRepositoryDto(GitHubRepositoryResponseDto repo) {
        return GithubRepositoryDto.builder()
                .repositoryName(repo.name())
                .ownerLogin(repo.owner().login())
                .branches(fetchBranches(repo.owner().login(), repo.name()))
                .build();
    }

    private List<BranchDto> fetchBranches(String owner, String repositoryName) {
        final String url = UriComponentsBuilder
                .fromUriString(uri + "/repos/" + owner + "/" + repositoryName + "/branches")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);

        ResponseEntity<String> rawResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (rawResponse.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<GitHubBranchResponseDto> branches = mapper.readValue(
                        rawResponse.getBody(),
                        new TypeReference<>() {
                        }
                );

                return branches.stream()
                        .map(this::mapToBranchDto)
                        .collect(Collectors.toList());

            } catch (Exception e) {
                log.error("Failed to deserialize branches: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.error("GitHub returned non-2xx: {} Body: {}", rawResponse.getStatusCode(), rawResponse.getBody());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BranchDto mapToBranchDto(GitHubBranchResponseDto branch) {
        return BranchDto.builder()
                .branchName(branch.name())
                .lastCommitSha(branch.commit().sha())
                .build();
    }
}
