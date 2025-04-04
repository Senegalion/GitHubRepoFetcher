package org.example.githubrepofetcher.service;

import lombok.AllArgsConstructor;
import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GitHubService {
    private final GitHubFetcher gitHubFetcher;

    public List<GithubRepositoryDto> getUserGitHubRepositories(String username) {
        List<GithubRepositoryDto> gitHubRepositories = gitHubFetcher.fetchGitHubRepositories(username);
        return gitHubRepositories;
    }
}
