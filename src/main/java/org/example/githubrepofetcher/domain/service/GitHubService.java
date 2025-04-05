package org.example.githubrepofetcher.domain.service;

import lombok.AllArgsConstructor;
import org.example.githubrepofetcher.domain.model.dto.api.GithubRepositoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GitHubService {
    private final GitHubFetcher gitHubFetcher;

    public List<GithubRepositoryDto> getUserGitHubRepositories(String username) {
        return gitHubFetcher.fetchGitHubRepositories(username);
    }
}
