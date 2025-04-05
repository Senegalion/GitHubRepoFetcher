package org.example.githubrepofetcher;

import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherRestTemplateConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        GitHubFetcherRestTemplateConfigurationProperties.class
})
public class GitHubRepoFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitHubRepoFetcherApplication.class, args);
    }

}
