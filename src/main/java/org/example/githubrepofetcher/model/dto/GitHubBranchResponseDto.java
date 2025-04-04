package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

@Builder
public record GitHubBranchResponseDto(
        String name,
        CommitDto commit
) {
}
