package org.example.githubrepofetcher.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.example.githubrepofetcher.BaseIntegrationTest;
import org.example.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.example.githubrepofetcher.service.GitHubRepositoryBranchesMockData;
import org.example.githubrepofetcher.service.GitHubService;
import org.example.githubrepofetcher.service.UserGitHubRepositoriesMockData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFetchGithubRepositoriesIntegrationTest extends BaseIntegrationTest {
    @DynamicPropertySource
    public static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("offer.http.client.config.uri", () -> WIRE_MOCK_HOST);
        registry.add("offer.http.client.config.port", () -> wireMockServer.getPort());
    }

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void user_wants_to_list_github_repositories_of_a_user_which_are_not_forks_with_information_like_repository_name_owner_login_and_branch() throws Exception {
        // step 1: user enters an invalid GitHub URL (incorrect username)
        // given
        wireMockServer.stubFor(WireMock.get("/users/invalid-user/repos")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(UserGitHubRepositoriesMockData.invalidUserJson())));

        // when
        ResultActions resultForInvalidUser =
                mockMvc.perform(get("/github/invalid-user/repositories"));

        // then
        resultForInvalidUser.andExpect(status().isNotFound());


        // step 2: user enters a valid username but GitHub returns empty body (no repositories)
        // given
        wireMockServer.stubFor(WireMock.get("/users/existing-user")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")));

        wireMockServer.stubFor(WireMock.get("/users/existing-user/repos")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(UserGitHubRepositoriesMockData.emptyRepositoriesJson())));

        // when
        ResultActions resultForNoRepositories =
                mockMvc.perform(get("/github/existing-user/repositories"));

        // then
        resultForNoRepositories.andExpect(status().isOk())
                .andExpect(content().json("[]"));


        // Step 3: user enters a valid username and GitHub returns repositories, but only forks
        // given
        wireMockServer.stubFor(WireMock.get("/users/existing-user/repos")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(UserGitHubRepositoriesMockData.repositoriesJsonWithForks())));

        // when
        ResultActions resultForOnlyForkRepositories =
                mockMvc.perform(get("/github/existing-user/repositories"));

        // then
        resultForOnlyForkRepositories.andExpect(status().isOk())
                .andExpect(content().json("[]"));


        // Step 4: user enters a valid username and GitHub returns valid repositories
        // given
        wireMockServer.stubFor(WireMock.get("/users/existing-user/repos")
                .willReturn(aResponse().withStatus(
                                HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(UserGitHubRepositoriesMockData.repositoriesJsonWithValidRepos())));

        wireMockServer.stubFor(WireMock.get("/repos/existing-user/repo1/branches")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(GitHubRepositoryBranchesMockData.branchesJson())));

        wireMockServer.stubFor(WireMock.get("/repos/existing-user/repo2/branches")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(GitHubRepositoryBranchesMockData.branchesJson())));

        // when
        ResultActions resultForValidRepositories =
                mockMvc.perform(get("/github/existing-user/repositories"));

        // then
        resultForValidRepositories.andExpect(status().isOk());

        MvcResult mvcResult = resultForValidRepositories.andReturn();
        String responseContent = mvcResult.getResponse().getContentAsString();
        GithubRepositoryDto[] repositories = objectMapper.readValue(responseContent, GithubRepositoryDto[].class);

        assertEquals(2, repositories.length);
        assertEquals("repo1", repositories[0].repositoryName());
        assertEquals("repo2", repositories[1].repositoryName());


        // step 5: service handles unexpected errors gracefully (e.g., GitHub API service unavailable)
        // given
        wireMockServer.stubFor(WireMock.get("/users/existing-user/repos")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        .withHeader("Content-Type", "application/json")));

        // when
        ResultActions result = mockMvc.perform(get("/github/existing-user/repositories"));

        // then
        result.andExpect(status().isServiceUnavailable());
    }
}
