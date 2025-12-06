# Testing Guide

## Prerequisites

**IMPORTANT**: These integration tests require Docker to be running on your machine.

### Docker Installation

- **Windows**: Install [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)
- **macOS**: Install [Docker Desktop for Mac](https://docs.docker.com/desktop/install/mac-install/)
- **Linux**: Install [Docker Engine](https://docs.docker.com/engine/install/)

### Verify Docker is Running

Before running tests, verify Docker is accessible:

```bash
docker --version
docker ps
```

If these commands fail, start Docker Desktop (Windows/macOS) or the Docker daemon (Linux).

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=RedisIntegrationTest
mvn test -Dtest=KafkaIntegrationTest
mvn test -Dtest=CassandraIntegrationTest
mvn test -Dtest=SmokeTest
```

### Skip Tests

If Docker is not available and you need to build without running tests:

```bash
mvn clean install -DskipTests
```

## Test Architecture

The test suite uses [Testcontainers](https://www.testcontainers.org/) to provide:

- Redis container (redis:7-alpine)
- Kafka container (confluentinc/cp-kafka:7.5.0)
- Cassandra container (cassandra:4.1)

All tests extend `BaseIntegrationTest` which manages the container lifecycle.

## Troubleshooting

### "Could not find a valid Docker environment"

**Solution**: Start Docker Desktop (Windows/macOS) or ensure Docker daemon is running (Linux).

### Tests are slow on first run

**Solution**: This is normal. Testcontainers needs to pull Docker images on first run. Subsequent runs will be faster due to image caching and container reuse.

### Port conflicts

**Solution**: Ensure ports 6379 (Redis), 9092 (Kafka), and 9042 (Cassandra) are not in use by other applications.

## CI/CD Considerations

When running in CI/CD pipelines:

1. Ensure Docker is available in the build environment
2. Use Docker-in-Docker (DinD) or Docker socket mounting for containerized CI systems
3. Consider using Testcontainers Cloud for faster, managed container infrastructure
