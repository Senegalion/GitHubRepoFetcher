package org.example.githubrepofetcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.githubrepofetcher.domain.model.dto.api.GithubRepositoryDto;
import org.example.githubrepofetcher.domain.model.dto.github.GitHubRepositoryResponseDto;
import org.example.githubrepofetcher.domain.model.dto.github.OwnerDto;
import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherRestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubFetcherRestTemplateTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GitHubFetcherRestTemplate gitHubFetcherRestTemplate;

    @BeforeEach
    void setUp() {
        String uri = "https://api.github.com";
        String token = "ghp_YourGeneratedTokenHere1234567890";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        gitHubFetcherRestTemplate = new GitHubFetcherRestTemplate(restTemplate, uri, token, executorService);
    }

    @Test
    void should_fetch_user_github_repositories() {
        // given
        String username = "Senegalion";

        when(restTemplate.exchange(
                eq("https://api.github.com/users/" + username),
                eq(HttpMethod.GET),
                any(),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        GitHubRepositoryResponseDto repo = new GitHubRepositoryResponseDto("test-repo-name", false, new OwnerDto("test-owner-login"));
        List<GitHubRepositoryResponseDto> mockResponse = List.of(repo);
        ResponseEntity<List<GitHubRepositoryResponseDto>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        String branchResponseJson = GitHubRepositoryBranchesMockData.branchJson();

        String branchesUrl = "https://api.github.com/repos/test-owner-login/test-repo-name/branches";

        when(restTemplate.exchange(
                eq("https://api.github.com/users/Senegalion/repos"),
                eq(HttpMethod.GET),
                any(),
                eq(new ParameterizedTypeReference<List<GitHubRepositoryResponseDto>>() {
                })
        )).thenReturn(responseEntity);

        when(restTemplate.exchange(
                eq(branchesUrl),
                eq(HttpMethod.GET),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(branchResponseJson, HttpStatus.OK));

        // when
        List<GithubRepositoryDto> repositories = gitHubFetcherRestTemplate.fetchGitHubRepositories(username);

        // then
        assertNotNull(repositories);
        assertEquals(1, repositories.size());
        assertEquals("test-repo-name", repositories.getFirst().repositoryName());
    }
}