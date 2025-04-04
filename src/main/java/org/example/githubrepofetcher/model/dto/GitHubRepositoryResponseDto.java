package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

@Builder
public record GitHubRepositoryResponseDto(
        String name,
        boolean fork,
        OwnerDto owner
) {
}
