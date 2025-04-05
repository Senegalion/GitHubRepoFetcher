package org.example.githubrepofetcher.http.github;

import org.example.githubrepofetcher.domain.service.GitHubFetcher;
import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherClientConfig;
import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherRestTemplate;
import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherRestTemplateConfigurationProperties;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;

public class GitHubHttpClientConfigurationIntegrationTest extends GitHubFetcherClientConfig {
    public static final String WIRE_MOCK_HOST = "http://localhost";

    public GitHubHttpClientConfigurationIntegrationTest(GitHubFetcherRestTemplateConfigurationProperties properties) {
        super(properties);
    }

    public GitHubFetcher gitHubRepositoriesFetcherClient() {
        RestTemplate restTemplate = restTemplate(restTemplateResponseErrorHandler());
        ExecutorService executorService = branchFetchExecutor();
        return new GitHubFetcherRestTemplate(restTemplate, WIRE_MOCK_HOST, getProperties().token(), executorService);
    }
}
