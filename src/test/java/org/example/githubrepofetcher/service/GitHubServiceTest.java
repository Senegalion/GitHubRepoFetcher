package org.example.githubrepofetcher.service;

import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {
    @Mock
    private GitHubFetcher gitHubFetcher;
    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        gitHubService = new GitHubService(gitHubFetcher);
    }

    @Test
    void should_return_user_github_repositories() {
        // given
        String username = "Senegalion";
        GithubRepositoryDto repo =
                new GithubRepositoryDto("test-repo", "test-owner-login", null);
        List<GithubRepositoryDto> mockResponse = List.of(repo);
        when(gitHubFetcher.fetchGitHubRepositories(username)).thenReturn(mockResponse);

        // when
        List<GithubRepositoryDto> repositories = gitHubService.getUserGitHubRepositories(username);

        // then
        assertNotNull(repositories);
        assertEquals(1, repositories.size());
        assertEquals("test-repo", repositories.getFirst().repositoryName());
    }
}