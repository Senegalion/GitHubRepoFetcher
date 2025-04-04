package org.example.githubrepofetcher.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.githubrepofetcher.model.dto.BranchDto;
import org.example.githubrepofetcher.model.dto.GitHubBranchResponseDto;
import org.example.githubrepofetcher.model.dto.GitHubRepositoryResponseDto;
import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.example.githubrepofetcher.service.exception.GitHubUserNotFoundException;
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

    @Override
    public List<GithubRepositoryDto> fetchGitHubRepositories(String username) {
        log.info("Fetching GitHub repositories for user: {}", username);
        HttpHeaders headers = new HttpHeaders();
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        try {
            List<GitHubRepositoryResponseDto> repos = getUserRepositories(username, requestEntity);
            return repos.stream()
                    .filter(repo -> !repo.fork())
                    .map(this::mapToGithubRepositoryDto)
                    .collect(Collectors.toList());
        } catch (ResourceAccessException e) {
            log.error("Error while fetching GitHub repositories: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GitHub API not available");
        }
    }

    private List<GitHubRepositoryResponseDto> getUserRepositories(String username, HttpEntity<HttpHeaders> requestEntity) {
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
                throw new GitHubUserNotFoundException("User found, but no repositories available");
            }

            return repos;
        } catch (ResponseStatusException e) {
            log.warn("User {} not found on GitHub", username);
            throw new GitHubUserNotFoundException("User not found on GitHub");
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
        ResponseEntity<List<GitHubBranchResponseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );

        List<GitHubBranchResponseDto> branches = response.getBody();
        if (branches.isEmpty()) {
            log.warn("No branches found for repo: {}/{}", owner, repositoryName);
            return List.of();
        }

        return branches.stream()
                .map(this::mapToBranchDto)
                .collect(Collectors.toList());
    }

    private BranchDto mapToBranchDto(GitHubBranchResponseDto branch) {
        return BranchDto.builder()
                .branchName(branch.name())
                .lastCommitSha(branch.commit().sha())
                .build();
    }
}
