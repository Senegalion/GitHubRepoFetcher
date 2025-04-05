package org.example.githubrepofetcher.domain.model.dto.github;

import lombok.Builder;

@Builder
public record GitHubRepositoryResponseDto(
        String name,
        boolean fork,
        OwnerDto owner
) {
}
