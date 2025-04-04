package org.example.githubrepofetcher.service;

import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;

import java.util.List;

public interface GitHubFetcher {
    List<GithubRepositoryDto> fetchGitHubRepositories(String username);
}
