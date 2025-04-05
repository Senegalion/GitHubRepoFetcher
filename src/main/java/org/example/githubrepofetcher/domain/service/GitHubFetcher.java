package org.example.githubrepofetcher.domain.service;

import org.example.githubrepofetcher.domain.model.dto.api.GithubRepositoryDto;

import java.util.List;

public interface GitHubFetcher {
    List<GithubRepositoryDto> fetchGitHubRepositories(String username);
}
