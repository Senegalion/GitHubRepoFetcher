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

4. **Generate a GitHub Personal Access Token (PAT):**

   To authenticate with the GitHub API and increase your rate limit, you need to generate a PAT:

   - Log in to your GitHub account.
   - Go to **Settings > Developer settings > Personal access tokens** or click [here](https://github.com/settings/tokens).
   - Click **"Fine-grained tokens"** or **"Tokens (classic)"** → then **"Generate new token"**.
   - **Choose token type:**
      - For public data access only, select **"Tokens (classic)"**.
   - **Scopes to select:**
      - You can **leave all scopes unchecked** if you're only accessing public repositories.
      - If needed, check `public_repo` for enhanced access to public repositories.
   - Click **Generate token**, then copy and save it. (It will only be shown once!)

   📌 _Make sure not to share this token or commit it to version control._

5. **Set the token as an environment variable:**

   On your local machine, you can set the `GITHUB_TOKEN` environment variable like so:

   - **On Linux/MacOS**:
       ```bash
       export GITHUB_TOKEN=ghp_YourGeneratedTokenHere1234567890
       ```

   - **On Windows**:
       ```powershell
       $env:GITHUB_TOKEN="ghp_YourGeneratedTokenHere1234567890"
       ```

   Alternatively, you can place the token directly in your `application.yml` (but **don't commit this file** to a public repository, as it will expose your token).

   Example of `application.yml`:

    ```yaml
    server:
      port: 8082

    spring:
      application:
        name: GitHubRepoFetcher

    github:
      fetcher:
        http:
          client:
            config:
              uri: https://api.github.com
              token: ${GITHUB_TOKEN}
              connectionTimeout: 5000
              readTimeout: 5000
    ```

6. Run the application:

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
        "status": 404,
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

Tests include:
- verifying that the correct repositories are fetched for existing users.
- ensuring the application correctly handles non-existent GitHub users.

## Contribution

If you'd like to contribute to this project, feel free to fork the repository and create a pull request with your changes.

⚠️ GitHub API Rate Limiting
When using the GitHub API without authentication, GitHub enforces a very low rate limit — 60 requests per hour per IP address.

If this limit is exceeded, you'll receive a response like this:

   ```json
   {
       "message": "API rate limit exceeded for 87.92.3.111. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
       "documentation_url": "https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"
   }
   ```

To avoid this, the application uses a GitHub Personal Access Token (PAT) for authentication, which increases the rate limit to 5,000 requests per hour.

👉 Make sure to follow the Running the Application instructions and provide your GitHub token via the GITHUB_TOKEN environment variable.

More details on GitHub rate limiting: GitHub Docs – Rate Limiting

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
