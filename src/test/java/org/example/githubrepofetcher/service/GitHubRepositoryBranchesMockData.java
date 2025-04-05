package org.example.githubrepofetcher.service;

public interface GitHubRepositoryBranchesMockData {
    static String branchJson() {
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

    static String branchesJson() {
        return """
                [
                    {
                        "name": "LyaLya-pie-patch",
                        "commit": {
                            "sha": "0f69ad5cf7ff57e91487568cdd2d9c8a9d2e855a"
                        }
                    },
                    {
                        "name": "jmarlena-patch-1",
                        "commit": {
                            "sha": "a6f5072795a54e9024fed3431faf13880a89cad2"
                        }
                    },
                    {
                        "name": "master",
                        "commit": {
                            "sha": "d09e445076bbcd163fc9abfbe6d2fce09a611281"
                        }
                    }
                ]
                """.trim();
    }
}
