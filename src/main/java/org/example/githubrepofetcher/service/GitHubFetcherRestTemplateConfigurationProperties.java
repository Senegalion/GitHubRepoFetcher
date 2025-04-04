package org.example.githubrepofetcher.service;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github.fetcher.http.client.config")
@Builder
public record GitHubFetcherRestTemplateConfigurationProperties(
        String uri,
        int connectionTimeout,
        int readTimeout
) {
}
