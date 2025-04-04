package org.example.githubrepofetcher.model.dto;

import lombok.Builder;

@Builder
public record BranchDto(
        String branchName,
        String lastCommitSha
) {
}
