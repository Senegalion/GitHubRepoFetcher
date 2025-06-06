package org.example.githubrepofetcher.infrastructure.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.example.githubrepofetcher.domain.service.GitHubFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Configuration
@AllArgsConstructor
public class GitHubFetcherClientConfig {
    private final GitHubFetcherRestTemplateConfigurationProperties properties;

    @Bean
    public RestTemplateResponseErrorHandler restTemplateResponseErrorHandler() {
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateResponseErrorHandler restTemplateResponseErrorHandler) {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(properties.readTimeout()))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(properties.connectionTimeout());
        factory.setReadTimeout(properties.readTimeout());

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
        return restTemplate;
    }

    @Bean
    public GitHubFetcher remoteNumberGeneratorClient(RestTemplate restTemplate, ExecutorService branchFetchExecutor) {
        return new GitHubFetcherRestTemplate(
                restTemplate,
                properties.uri(),
                properties.token(),
                branchFetchExecutor
        );
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService branchFetchExecutor() {
        return Executors.newFixedThreadPool(30);
    }
}
