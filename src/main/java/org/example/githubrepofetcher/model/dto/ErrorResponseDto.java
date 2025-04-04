package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        int status,
        String message
) {
}
