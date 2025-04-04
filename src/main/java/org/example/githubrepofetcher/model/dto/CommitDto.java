package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

@Builder
public record CommitDto(
        String sha
) {
}
