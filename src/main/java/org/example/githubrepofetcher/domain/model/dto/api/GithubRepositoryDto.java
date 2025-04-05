package org.example.githubrepofetcher.domain.model.dto.api;

import lombok.Builder;
import org.example.githubrepofetcher.domain.model.dto.api.BranchDto;

import java.util.List;

@Builder
public record GithubRepositoryDto(
        String repositoryName,
        String ownerLogin,
        List<BranchDto> branches
) {
}
