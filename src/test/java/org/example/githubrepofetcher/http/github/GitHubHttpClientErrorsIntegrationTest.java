package org.example.githubrepofetcher.http.github;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.hc.core5.http.HttpStatus;
import org.example.githubrepofetcher.domain.service.GitHubFetcher;
import org.example.githubrepofetcher.infrastructure.github.GitHubFetcherRestTemplateConfigurationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.server.ResponseStatusException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class GitHubHttpClientErrorsIntegrationTest extends GitHubHttpClientConfigurationIntegrationTest {
    public static final String WIRE_MOCK_HOST = "http://localhost";
    public static final String TOKEN = "token";
    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;
    public static final String INTERNAL_SERVER_ERROR = "500 INTERNAL_SERVER_ERROR";
    public static final String NO_CONTENT = "204 NO_CONTENT";

    private GitHubFetcher gitHubFetcher;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    public GitHubHttpClientErrorsIntegrationTest() {
        super(new GitHubFetcherRestTemplateConfigurationProperties(
                WIRE_MOCK_HOST,
                TOKEN,
                CONNECTION_TIMEOUT,
                READ_TIMEOUT
        ));
    }

    @BeforeEach
    void setUp() {
        GitHubFetcherRestTemplateConfigurationProperties properties =
                new GitHubFetcherRestTemplateConfigurationProperties(
                        WIRE_MOCK_HOST,
                        TOKEN,
                        CONNECTION_TIMEOUT,
                        READ_TIMEOUT
                );

        gitHubFetcher = new GitHubHttpClientConfigurationIntegrationTest(properties)
                .gitHubRepositoriesFetcherClient();
    }

    @Test
    void should_return_500_internal_server_error_when_response_delay_is_5000_ms_and_client_has_1000ms_read_timeout() {
        wireMockServer.stubFor(WireMock.get("/users/someuser/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\": \"repo1\"}]")
                        .withFixedDelay(5000)));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable.getMessage()).contains(INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_return_500_internal_server_error_when_github_returns_server_error() {
        wireMockServer.stubFor(WireMock.get("/users/someuser/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withBody("GitHub API server error")));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable.getMessage()).contains(INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_return_401_unauthorized_when_github_returns_unauthorized() {
        wireMockServer.stubFor(WireMock.get("/users/someuser")
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SC_UNAUTHORIZED)));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void should_return_404_not_found_when_github_returns_not_found() {
        wireMockServer.stubFor(WireMock.get("/users/nonexistentuser")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND)));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("nonexistentuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void should_return_503_service_unavailable_when_github_is_down() {
        wireMockServer.stubFor(WireMock.get("/users/someuser/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_SERVICE_UNAVAILABLE)
                        .withBody("GitHub API service is unavailable")));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable.getMessage()).contains(INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_return_500_internal_server_error_when_fault_malformed_response_chunk() {
        wireMockServer.stubFor(WireMock.get("/users/someuser/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable.getMessage()).contains(INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_return_500_internal_server_error_when_connection_fails() {
        wireMockServer.stubFor(WireMock.get("/users/someuser/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withBody("Connection failed")));

        Throwable throwable = catchThrowable(() -> gitHubFetcher.fetchGitHubRepositories("someuser"));

        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable.getMessage()).contains(INTERNAL_SERVER_ERROR);
    }
}
