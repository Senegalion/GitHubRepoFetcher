
# GitHub Repository Fetcher

This project allows you to fetch GitHub repositories for a given user, with a focus on repositories that are not forks. It also handles the case when a GitHub user does not exist and returns an appropriate error response.

## Requirements

- Java 21
- Spring Boot
- GitHub API

## Features

- Fetch GitHub repositories for a given user.
- Filters repositories to exclude forks.
- For each repository, fetches branches and their last commit SHA.
- Handles the case where the user does not exist on GitHub and returns a 404 error with a custom response format.

## Running the Application

To run the application locally, follow these steps:

1. Clone the repository:

    ```bash
    git clone https://github.com/Senegalion/GitHubRepoFetcher.git
    cd GitHubRepoFetcher
    ```

2. Make sure Java 21 is installed. You can check your Java version by running:

    ```bash
    java -version
    ```

    If Java 21 is not installed, download and install it from [AdoptOpenJDK](https://adoptopenjdk.net/) or the official Java website.

3. Build the application using Maven:

    ```bash
    ./mvnw clean install
    ```

4. Run the application:

    ```bash
    ./mvnw spring-boot:run
    ```

    By default, the application will run on port `8082`. You can access it at:

    ```bash
    http://localhost:8082
    ```

## API Endpoints

### Fetch GitHub Repositories

`GET /github/{username}/repositories`

Fetches GitHub repositories for a given user. Only repositories that are not forks are returned.

**Response:**

```json
[
    {
        "repositoryName": "repo-name",
        "ownerLogin": "owner-login",
        "branches": [
            {
                "branchName": "branch-name",
                "lastCommitSha": "sha-value"
            }
        ]
    }
]
```

**Error Response:**

- **404** - If the user does not exist on GitHub, the response format is:

    ```json
    {
        "status": "404",
        "message": "User not found on GitHub"
    }
    ```

## Error Handling

- **404** - User not found on GitHub.
- **500** - Server error when interacting with the GitHub API.
- **503** - Service unavailable if the GitHub API is unreachable.

## Testing

The application includes integration tests to validate the functionality of the endpoints.

To run the tests, use:

```bash
./mvnw test
```

## Contribution

If you'd like to contribute to this project, feel free to fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
