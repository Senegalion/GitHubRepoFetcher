package org.example.githubrepofetcher.domain.model.dto.api;

import lombok.Builder;

@Builder
public record BranchDto(
        String branchName,
        String lastCommitSha
) {
}
