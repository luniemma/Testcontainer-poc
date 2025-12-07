# Build and Smoke Test Instructions

This guide provides step-by-step instructions to build the Docker image and run smoke tests.

## Prerequisites

- Docker installed and running
- Maven 3.9+ installed
- Java 17 installed
- At least 8GB RAM available for Docker

## Step 1: Build the Application

Build the Spring Boot application using Maven:

```bash
mvn clean package -DskipTests
```

This creates the JAR file in `target/testcontainers-demo-1.0.0.jar`

## Step 2: Build the Docker Image

Build the Docker image using the multi-stage Dockerfile:

```bash
docker build -t testcontainers-demo:latest .
```

Or with a specific tag:

```bash
docker build -t luniemma/testcontainer-poc:latest .
```

### Verify the Image

Check that the image was created successfully:

```bash
docker images | grep testcontainers-demo
```

## Step 3: Run Smoke Tests

### Option A: Run Smoke Tests with Maven

Run all smoke tests including enhanced smoke tests:

```bash
mvn test -Dtest=SmokeTest,EnhancedSmokeTest
```

Or run just the enhanced smoke test:

```bash
mvn test -Dtest=EnhancedSmokeTest
```

### Option B: Run All Integration Tests

Run all integration tests including Cassandra, Kafka, and Redis:

```bash
mvn verify
```

### Option C: Run Using Docker Compose

Start all services with Docker Compose:

```bash
docker-compose up -d
```

Wait for services to be healthy, then run smoke tests:

```bash
# Check service health
docker-compose ps

# Run tests against running services
mvn test -Dspring.profiles.active=test
```

Cleanup:

```bash
docker-compose down -v
```

## Step 4: Run the Application in Docker

### Using Docker Run

```bash
docker run -d \
  -p 8080:8080 \
  --name testcontainers-app \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CASSANDRA_CONTACT_POINTS=cassandra \
  -e CASSANDRA_PORT=9042 \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  testcontainers-demo:latest
```

### Using Docker Compose

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Step 5: Verify Application Health

Check application health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Step 6: Manual Smoke Test Validation

Test each endpoint manually:

### Cassandra Endpoint
```bash
# Create a user
curl -X POST http://localhost:8080/api/cassandra/users \
  -H "Content-Type: application/json" \
  -d '{"id":"123e4567-e89b-12d3-a456-426614174000","name":"John Doe","email":"john@example.com"}'

# Get all users
curl http://localhost:8080/api/cassandra/users

# Get user by ID
curl http://localhost:8080/api/cassandra/users/123e4567-e89b-12d3-a456-426614174000
```

### Kafka Endpoint
```bash
# Send a message
curl -X POST "http://localhost:8080/api/kafka/send?message=Hello%20Kafka"

# Verify message was consumed (check application logs)
docker logs testcontainers-app | grep "Received message"
```

### Redis Cache Endpoint
```bash
# Set a value
curl -X POST "http://localhost:8080/api/cache/set?key=test&value=hello"

# Get a value
curl http://localhost:8080/api/cache/get/test

# Delete a value
curl -X DELETE http://localhost:8080/api/cache/delete/test
```

## Test Output Interpretation

### Successful Smoke Test Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.testcontainers.EnhancedSmokeTest
[INFO] Starting test containers...
[INFO] ✓ Redis container started
[INFO] ✓ Kafka container started
[INFO] ✓ Cassandra container started
[INFO] ✓ Application health check passed
[INFO] ✓ All smoke tests passed
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

### Common Issues and Solutions

#### Issue: Port Already in Use
```
Error: Bind for 0.0.0.0:8080 failed: port is already allocated
```
Solution: Stop the conflicting service or use a different port:
```bash
docker run -p 8081:8080 testcontainers-demo:latest
```

#### Issue: Docker Daemon Not Running
```
Error: Cannot connect to the Docker daemon
```
Solution: Start Docker Desktop or Docker service:
```bash
sudo systemctl start docker  # Linux
open -a Docker              # macOS
```

#### Issue: Insufficient Memory
```
Error: Container killed due to memory constraints
```
Solution: Increase Docker memory limit in Docker Desktop settings (recommend 8GB minimum)

## Cleanup

Remove all containers and volumes:

```bash
# Stop and remove containers
docker-compose down -v

# Remove the application container
docker stop testcontainers-app
docker rm testcontainers-app

# Optional: Remove the image
docker rmi testcontainers-demo:latest
```

## CI/CD Integration

The project includes GitHub Actions workflows in `.github/workflows/`:

- `ci-cd.yml` - Runs on every push, executes tests
- `build.yml` - Builds and pushes Docker image
- `release.yml` - Creates releases and tags

These workflows automatically build the Docker image and run smoke tests in CI/CD.
