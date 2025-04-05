package org.example.githubrepofetcher.domain.model.dto.github;

import lombok.Builder;

@Builder
public record OwnerDto(
        String login
) {
}
