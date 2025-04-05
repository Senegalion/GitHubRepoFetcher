package org.example.githubrepofetcher.domain.model.dto.api;

import lombok.Builder;

@Builder
public record ErrorResponseDto(
        int status,
        String message
) {
}
