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

## CI/CD

GitHub Actions workflows:
- **ci-cd.yml** - Continuous integration and testing
- **build.yml** - Docker image build and push
- **release.yml** - Release management

Testcontainer-poc
