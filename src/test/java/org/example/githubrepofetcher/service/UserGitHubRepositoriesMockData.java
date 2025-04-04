package org.example.githubrepofetcher.service;

public interface UserGitHubRepositoriesMockData {
    static String repositoriesJsonWithValidRepos() {
        return """
                [
                    {
                        "name": "repo1",
                        "fork": false,
                        "owner": {
                            "login": "existing-user"
                        }
                    },
                    {
                        "name": "repo2",
                        "fork": false,
                        "owner": {
                            "login": "existing-user"
                        }
                    }
                ]
                """.trim();
    }

    static String repositoriesJsonWithForks() {
        return """
                [
                    {
                        "name": "repo1",
                        "fork": true,
                        "owner": {
                            "login": "owner1"
                        }
                    },
                    {
                        "name": "repo2",
                        "fork": true,
                        "owner": {
                            "login": "owner2"
                        }
                    }
                ]
                """.trim();
    }

    static String emptyRepositoriesJson() {
        return "[]" .trim();
    }

    static String invalidUserJson() {
        return "{}" .trim();
    }
}
