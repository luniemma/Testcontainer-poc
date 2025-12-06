# Spring Boot 3.x Testcontainers Demo

A complete proof-of-concept application demonstrating integration testing with Testcontainers for Redis, Kafka, and Cassandra.

## Project Structure

```
testcontainers-demo/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/example/testcontainers/
│   │   │   ├── TestcontainersApplication.java          # Main Spring Boot application
│   │   │   ├── controller/
│   │   │   │   ├── CacheController.java                # Redis endpoints
│   │   │   │   ├── KafkaController.java                # Kafka endpoints
│   │   │   │   └── CassandraController.java            # Cassandra endpoints
│   │   │   ├── service/
│   │   │   │   ├── RedisService.java                   # Redis operations
│   │   │   │   ├── KafkaProducerService.java           # Kafka producer
│   │   │   │   ├── KafkaConsumerService.java           # Kafka consumer
│   │   │   │   └── CassandraService.java               # Cassandra operations
│   │   │   ├── model/
│   │   │   │   └── User.java                           # Cassandra entity
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java                 # Spring Data Cassandra
│   │   │   └── config/
│   │   │       └── KafkaConfig.java                    # Kafka configuration
│   │   └── resources/
│   │       └── application.yml                         # Main configuration
│   └── test/
│       ├── java/com/example/testcontainers/
│       │   ├── config/
│       │   │   └── TestcontainersConfig.java           # Testcontainers setup
│       │   ├── RedisIntegrationTest.java               # Redis tests
│       │   ├── KafkaIntegrationTest.java               # Kafka tests
│       │   └── CassandraIntegrationTest.java           # Cassandra tests
│       └── resources/
│           └── application-test.yml                    # Test configuration
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker** (required for Testcontainers)
- Docker daemon must be running

## Dependencies

### Main Dependencies
- `spring-boot-starter-web` - REST API framework
- `spring-boot-starter-data-redis` - Redis integration
- `spring-boot-starter-data-cassandra` - Cassandra integration
- `spring-kafka` - Kafka integration

### Test Dependencies
- `testcontainers-junit-jupiter` - JUnit 5 integration
- `testcontainers:redis` - Redis container
- `testcontainers:kafka` - Kafka container
- `testcontainers:cassandra` - Cassandra container
- `awaitility` - Async testing utilities

## REST API Endpoints

### 1. Redis Cache Test
```bash
GET /cache/test
```
**Response:**
```json
{
  "key": "test-key",
  "setValue": "Hello from Redis!",
  "retrievedValue": "Hello from Redis!",
  "success": "true"
}
```

### 2. Kafka Producer
```bash
POST /kafka/produce
Content-Type: application/json

{
  "message": "Your test message"
}
```
**Response:**
```json
{
  "status": "Message sent to Kafka",
  "message": "Your test message"
}
```

### 3. Kafka Consumer
```bash
GET /kafka/messages
```
**Response:**
```json
{
  "count": 2,
  "messages": ["Message 1", "Message 2"]
}
```

### 4. Cassandra Users
```bash
# Get all users
GET /cassandra/users

# Create user
POST /cassandra/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com"
}

# Get user by ID
GET /cassandra/users/{id}
```

## Testcontainers Configuration

The `TestcontainersConfig` class automatically:

1. **Starts Docker containers** before tests:
   - Redis 7 (Alpine)
   - Kafka 7.5.0 (Confluent)
   - Cassandra 4.1

2. **Injects dynamic properties** using `@DynamicPropertySource`:
   - Redis host and port
   - Kafka bootstrap servers
   - Cassandra contact points and port

3. **Enables container reuse** for faster test execution

## Running the Application

### Option 1: Run Tests Only

```bash
# Clean and run all tests
mvn clean test

# Run specific test
mvn test -Dtest=RedisIntegrationTest

# Run tests with verbose output
mvn test -X
```

### Option 2: Run Application Locally

**Note:** You must have Redis, Kafka, and Cassandra running locally on default ports.

#### Start Required Services

**Using Docker Compose (Recommended):**

Create `docker-compose.yml`:

```yaml
version: '3.8'
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  cassandra:
    image: cassandra:4.1
    ports:
      - "9042:9042"
    environment:
      CASSANDRA_CLUSTER_NAME: TestCluster
```

```bash
# Start services
docker-compose up -d

# Run Spring Boot application
mvn spring-boot:run

# Stop services
docker-compose down
```

**Or start services individually:**

```bash
# Redis
docker run -d -p 6379:6379 redis:7-alpine

# Kafka (requires Zookeeper)
docker run -d -p 2181:2181 confluentinc/cp-zookeeper:7.5.0
docker run -d -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 confluentinc/cp-kafka:7.5.0

# Cassandra
docker run -d -p 9042:9042 cassandra:4.1
```

Then run:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Integration Tests

### RedisIntegrationTest
Tests Redis SET/GET operations:
- ✓ Setting and retrieving values
- ✓ Handling non-existent keys
- ✓ Overwriting existing values

### KafkaIntegrationTest
Tests Kafka produce/consume workflow:
- ✓ Message production and consumption
- ✓ Multiple message handling
- ✓ Uses Awaitility for async verification

### CassandraIntegrationTest
Tests Cassandra CRUD operations:
- ✓ Saving and retrieving users
- ✓ Fetching all users
- ✓ Handling non-existent records

## Testing Strategy

All integration tests use `@SpringBootTest` with:
- `@Import(TestcontainersConfig.class)` - Loads Testcontainers
- `@ActiveProfiles("test")` - Uses test profile
- `@DynamicPropertySource` - Injects runtime container properties

## Build and Package

```bash
# Clean build
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Run JAR
java -jar target/testcontainers-demo-1.0.0.jar
```

## Troubleshooting

### Docker Not Running
```
Error: Could not find a valid Docker environment
```
**Solution:** Start Docker Desktop or Docker daemon

### Port Already in Use
```
Error: Address already in use
```
**Solution:** Stop conflicting services or change ports in `application.yml`

### Test Timeouts
```
Error: Container startup timeout
```
**Solution:**
- Increase Docker memory allocation
- Pull images manually: `docker pull redis:7-alpine`

### Kafka Consumer Issues
Tests use `@KafkaListener` which is asynchronous. The tests use Awaitility to wait for messages to be consumed.

## Key Features

1. **Dynamic Property Injection**: Container ports are dynamically assigned and injected into Spring context
2. **Container Reuse**: Containers are reused across test classes for faster execution
3. **Realistic Testing**: Tests run against real Redis, Kafka, and Cassandra instances
4. **Clean Isolation**: Each test class has isolated state
5. **Production-Ready**: Same configuration works for both tests and production

## Technologies Used

- Spring Boot 3.2.0
- Java 17
- Maven
- Testcontainers 1.19.3
- Redis 7
- Apache Kafka 7.5.0
- Apache Cassandra 4.1
- JUnit 5
- Awaitility 4.2.0

## License

This is a proof-of-concept demonstration project.

## Additional Notes

- Testcontainers requires Docker to be running
- First test execution will download Docker images (slow)
- Subsequent runs reuse containers and are much faster
- All containers automatically stop when tests complete
- Integration tests are tagged with `@SpringBootTest` for easy filtering
