package org.example.githubrepofetcher.domain.model.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitDto(
        String sha
) {
}
