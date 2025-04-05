package org.example.githubrepofetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(classes = GitHubRepoFetcherApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BaseIntegrationTest {
    public static final String WIRE_MOCK_HOST = "http://localhost";

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("github.fetcher.http.client.config.uri", () -> WIRE_MOCK_HOST + ":" + wireMockServer.getPort());
        registry.add("github.fetcher.http.client.config.port", () -> String.valueOf(wireMockServer.getPort()));
    }
}
