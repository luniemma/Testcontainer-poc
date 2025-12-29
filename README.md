# Testcontainers Demo POC

A Spring Boot application demonstrating integration testing with Testcontainers for Cassandra, Kafka, and Redis.

## Quick Start

### Automated Build and Test

Use the automated script to build the Docker image and run smoke tests:

```bash
./build-and-smoke-test.sh
```

This script will:
1. Check prerequisites (Docker, Maven, Java)
2. Build the application with Maven
3. Build the Docker image
4. Run smoke tests with Testcontainers
5. Optionally run all integration tests

### Manual Build and Test

For detailed instructions, see [BUILD_AND_TEST.md](BUILD_AND_TEST.md)

#### Build Application
```bash
mvn clean package -DskipTests
```

#### Build Docker Image
```bash
docker build -t testcontainers-demo:latest .
```

#### Run Smoke Tests
```bash
mvn test -Dtest=EnhancedSmokeTest
```

## Documentation

- **[BUILD_AND_TEST.md](BUILD_AND_TEST.md)** - Comprehensive build and testing guide
- **[TESTING.md](TESTING.md)** - Testing strategy and guidelines
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Deployment instructions
- **[DOCKER_QUICKSTART.md](DOCKER_QUICKSTART.md)** - Docker quick start guide
- **[TESTCONTAINERS_SETUP.md](TESTCONTAINERS_SETUP.md)** - Testcontainers configuration

## Features

- Spring Boot 3.x application
- Integration with Cassandra, Kafka, and Redis
- Comprehensive smoke tests using Testcontainers
- Multi-stage Docker build
- CI/CD with GitHub Actions
- Health checks and monitoring

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker (for containers and tests)
- 8GB+ RAM for Docker

## Project Structure

```
.
├── src/
│   ├── main/java/           # Application source code
│   └── test/java/           # Test files including smoke tests
├── docker-compose.yml       # Development environment
├── docker-compose.prod.yml  # Production environment
├── Dockerfile               # Multi-stage Docker build
├── pom.xml                  # Maven configuration
└── build-and-smoke-test.sh  # Automated build script
```

## Running the Application

### With Docker Compose
```bash
docker-compose up -d
```

### With Docker Run
```bash
docker run -p 8080:8080 testcontainers-demo:latest
```

### Locally
```bash
mvn spring-boot:run
```

## API Endpoints

- **Health**: `GET /actuator/health`
- **Cassandra**: `POST /api/cassandra/users`, `GET /api/cassandra/users`
- **Kafka**: `POST /api/kafka/send?message={msg}`
- **Redis**: `POST /api/cache/set?key={k}&value={v}`, `GET /api/cache/get/{key}`

## Testing

Run all tests:
```bash
mvn verify
```

Run smoke tests only:
```bash
mvn test -Dtest=EnhancedSmokeTest
```

## CI/CD Pipeline

The project uses a comprehensive GitHub Actions pipeline with multiple stages and quality gates.

### Pipeline Architecture

The CI/CD pipeline is implemented as a reusable workflow (`ci-cd.yml`) that is called by:
- **build.yml** - Runs on push to main/develop and pull requests
- **release.yml** - Runs on version tags (v*.*.*)

### Pipeline Stages

#### 1. Build & Unit Tests
- Compiles the application with Maven
- Runs unit tests
- Publishes test results with detailed reports
- Uploads JAR artifact
- Runs SonarCloud analysis (if token available)

#### 2. Integration Tests (Testcontainers)
- Runs integration tests for Cassandra, Kafka, and Redis
- Uses Testcontainers for infrastructure
- Publishes test results and reports
- Generates test summary with pass/fail metrics

#### 3. Docker Build & Push
- Builds multi-platform Docker images (amd64, arm64)
- Pushes to Docker registry
- Generates SBOM (Software Bill of Materials)
- Creates multiple tags: latest, branch name, SHA, semver
- Skipped for pull requests

#### 4. Smoke Tests (Testcontainers Cloud)
- Runs comprehensive smoke tests on the published Docker image
- Uses Testcontainers Cloud for remote container execution
- Validates all service integrations end-to-end
- Publishes smoke test results
- Automatically skipped if TC_CLOUD_TOKEN not available

#### 5. Security Scan
- OWASP Dependency Check for vulnerabilities
- CodeQL static analysis for code security issues
- Uploads security reports as artifacts
- Only runs on non-PR events

#### 6. Production Deploy
- Automatically triggers on version tags (v*.*.*)
- Creates GitHub release with auto-generated notes
- Only runs if all previous stages pass

#### 7. Pipeline Summary
- Generates comprehensive summary of all job results
- Shows pass/fail status for each stage
- Displays Docker image tag
- Provides links to artifacts and detailed logs

### Required Secrets

Configure these in your GitHub repository settings:

| Secret | Required | Description |
|--------|----------|-------------|
| `DOCKER_USERNAME` | Yes | Docker Hub username |
| `DOCKER_PASSWORD` | Yes | Docker Hub access token |
| `SONAR_TOKEN` | No | SonarCloud authentication token |
| `TC_CLOUD_TOKEN` | No | Testcontainers Cloud token for smoke tests |

### Workflow Triggers

**build.yml**:
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop`
- Manual workflow dispatch

**release.yml**:
- Git tags matching `v*.*.*` pattern (e.g., v1.0.0)

### Pipeline Features

- **Test Result Publishing**: All test results appear as check runs in PRs
- **Job Summaries**: Each job generates a markdown summary with key metrics
- **Artifact Management**: Test reports, SBOM, and build artifacts retained
- **Conditional Execution**: Jobs skip gracefully when dependencies fail or secrets unavailable
- **Multi-platform Builds**: Docker images for both AMD64 and ARM64
- **Security Integration**: Automated vulnerability scanning and code analysis

### Viewing Pipeline Results

1. **GitHub Actions Tab**: See overall pipeline status and job logs
2. **Pull Request Checks**: Test results appear as status checks
3. **Job Summaries**: Click on any job to see detailed metrics and summaries
4. **Artifacts**: Download test reports, SBOM, and other outputs
5. **Security Tab**: Review security scan findings

### Local Testing

Before pushing, test the pipeline stages locally:

```bash
# Run all tests
mvn verify

# Build Docker image
docker build -t testcontainers-demo:latest .

# Run smoke tests
mvn test -Dtest=EnhancedSmokeTest
```
