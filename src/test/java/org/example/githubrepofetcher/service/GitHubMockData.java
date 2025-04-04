package org.example.githubrepofetcher.service;

public interface GitHubMockData {
    static String branchesJson() {
        return """
                [
                    {
                        "name": "master",
                        "commit": {
                            "sha": "sha-123"
                        }
                    }
                ]
                """.trim();
    }
}
