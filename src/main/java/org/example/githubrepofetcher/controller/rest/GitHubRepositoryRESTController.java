package org.example.githubrepofetcher.controller.rest;

import lombok.AllArgsConstructor;
import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.example.githubrepofetcher.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/github")
@AllArgsConstructor
public class GitHubRepositoryRESTController {
    private final GitHubService gitHubService;

    @GetMapping("/{username}/repositories")
    public ResponseEntity<List<GithubRepositoryDto>> getUserGitHubRepositories(@PathVariable String username) {
        List<GithubRepositoryDto> gitHubRepositories = gitHubService.getUserGitHubRepositories(username);
        return ResponseEntity.ok(gitHubRepositories);
    }
}
