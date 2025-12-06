# Quick Start Guide

## Prerequisites Check

```bash
# Check Java version (must be 17+)
java -version

# Check Maven version (must be 3.8+)
mvn -version

# Check Docker is running
docker ps
```

## Installation Steps

### 1. Copy the Project

Copy all files to a new directory:
```bash
mkdir testcontainers-demo
cd testcontainers-demo
# Copy all files from this project
```

### 2. Run Integration Tests

```bash
# This will download dependencies and run all tests
# Docker images will be pulled automatically
mvn clean test
```

**Expected Output:**
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 3. Run the Application

**Option A: With Docker Compose (Recommended)**

```bash
# Start all services
docker-compose up -d

# Wait 30 seconds for services to start
sleep 30

# Run the application
mvn spring-boot:run
```

**Option B: Standalone Containers**

```bash
# Start Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Start Zookeeper
docker run -d --name zookeeper -p 2181:2181 \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  confluentinc/cp-zookeeper:7.5.0

# Start Kafka
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.5.0

# Start Cassandra
docker run -d --name cassandra -p 9042:9042 cassandra:4.1

# Wait for services
sleep 30

# Run application
mvn spring-boot:run
```

### 4. Test the Endpoints

Once the application is running on `http://localhost:8080`:

```bash
# Test Redis
curl http://localhost:8080/cache/test

# Test Kafka Producer
curl -X POST http://localhost:8080/kafka/produce \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello Kafka!"}'

# Test Kafka Consumer
curl http://localhost:8080/kafka/messages

# Test Cassandra - Create User
curl -X POST http://localhost:8080/cassandra/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'

# Test Cassandra - Get All Users
curl http://localhost:8080/cassandra/users
```

### 5. Stop Services

```bash
# If using Docker Compose
docker-compose down

# If using standalone containers
docker stop redis zookeeper kafka cassandra
docker rm redis zookeeper kafka cassandra
```

## Project Structure Overview

```
testcontainers-demo/
├── pom.xml                                          # Maven configuration
├── docker-compose.yml                               # Docker services setup
├── JAVA_PROJECT_README.md                           # Full documentation
├── QUICKSTART.md                                    # This file
└── src/
    ├── main/
    │   ├── java/com/example/testcontainers/
    │   │   ├── TestcontainersApplication.java       # Main class
    │   │   ├── controller/                          # REST endpoints
    │   │   ├── service/                             # Business logic
    │   │   ├── model/                               # Data models
    │   │   ├── repository/                          # Data access
    │   │   └── config/                              # Configuration
    │   └── resources/
    │       └── application.yml                      # App config
    └── test/
        ├── java/com/example/testcontainers/
        │   ├── config/
        │   │   └── TestcontainersConfig.java        # Test containers setup
        │   ├── RedisIntegrationTest.java            # Redis tests
        │   ├── KafkaIntegrationTest.java            # Kafka tests
        │   └── CassandraIntegrationTest.java        # Cassandra tests
        └── resources/
            └── application-test.yml                 # Test config
```

## Common Commands

```bash
# Run all tests
mvn clean test

# Run specific test
mvn test -Dtest=RedisIntegrationTest

# Run application
mvn spring-boot:run

# Build JAR
mvn clean package

# Run JAR
java -jar target/testcontainers-demo-1.0.0.jar

# Skip tests during build
mvn clean package -DskipTests
```

## Troubleshooting

### Issue: Docker not running
```
Error: Could not find a valid Docker environment
```
**Fix:** Start Docker Desktop or Docker daemon

### Issue: Port already in use
```
Error: Address already in use
```
**Fix:**
```bash
# Find process using port
lsof -i :8080  # or :6379, :9092, :9042

# Kill the process
kill -9 <PID>
```

### Issue: Maven dependencies not downloading
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Re-download dependencies
mvn clean install
```

### Issue: Cassandra not ready
Wait longer for Cassandra to start (can take 60+ seconds):
```bash
# Check Cassandra logs
docker logs cassandra

# Wait for "Startup complete" message
```

## Next Steps

1. Read the full documentation in `JAVA_PROJECT_README.md`
2. Explore the code structure
3. Modify the tests and run them
4. Add your own endpoints and tests
5. Experiment with Testcontainers features

## Support

For detailed information about:
- REST API endpoints → See `JAVA_PROJECT_README.md`
- Test configuration → See `TestcontainersConfig.java`
- Application config → See `application.yml`
- Docker setup → See `docker-compose.yml`
