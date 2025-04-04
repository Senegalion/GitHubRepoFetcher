package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GithubRepositoryDto(
        String repositoryName,
        String ownerLogin,
        List<BranchDto> branches
) {
}
