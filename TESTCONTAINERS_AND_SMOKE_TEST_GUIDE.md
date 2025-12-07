# Testcontainers and Smoke Test Configuration Guide

## Overview

This guide explains how Testcontainers are configured to work with smoke tests and external service connectivity validation.

## Architecture

### 1. Testcontainers Setup

The project uses Testcontainers to spin up real instances of:
- **Redis** (version 7-alpine) - for caching
- **Kafka** (Confluent Platform 7.5.0) - for messaging
- **Cassandra** (version 4.1) - for database

### 2. Base Integration Test

**File**: `src/test/java/com/example/testcontainers/config/BaseIntegrationTest.java`

All integration tests extend this class, which:
- Starts all required containers before tests run
- Registers dynamic properties for Spring to connect to containers
- Enables container reuse for faster test execution
- Initializes Cassandra with schema using `cassandra-init.cql`

### 3. Cassandra Configuration

**Configuration Class**: `src/main/java/com/example/testcontainers/config/CassandraConfig.java`

This class:
- Extends `AbstractCassandraConfiguration`
- Enables Cassandra repositories
- Configures connection properties from application.yml
- Sets schema action to `CREATE_IF_NOT_EXISTS`

**Init Script**: `src/test/resources/cassandra-init.cql`

This script runs when Cassandra container starts and:
- Creates the `test_keyspace` keyspace
- Creates the `users` table with proper schema
- Ensures database is ready before tests run

### 4. Application Configuration

**Main Application**: `src/main/java/com/example/testcontainers/TestcontainersApplication.java`

Enables:
- `@SpringBootApplication` - Auto-configuration
- `@EnableKafka` - Kafka support
- `@EnableCassandraRepositories` - Cassandra repository support

### 5. Test Configuration

**File**: `src/test/resources/application-test.yml`

- Removed autoconfiguration exclusions to allow Spring Boot to create all required beans
- Configures external service URLs for connectivity testing
- Sets appropriate logging levels for debugging

## Smoke Tests

### 1. Basic Smoke Test

**File**: `src/test/java/com/example/testcontainers/SmokeTest.java`

Tests basic functionality:
- Redis read/write operations
- Kafka message production
- Cassandra query execution
- All services working together

### 2. Enhanced Smoke Test

**File**: `src/test/java/com/example/testcontainers/EnhancedSmokeTest.java`

Comprehensive testing including:
- **Testcontainers Health Check** - Validates all containers are running and healthy
- **External Services Connectivity** - Tests connections to external Redis, Kafka, Cassandra, and APIs
- **End-to-End Functionality** - Performs full workflow tests across all services

### 3. Smoke Test Framework

**File**: `src/test/java/com/example/testcontainers/framework/SmokeTestFramework.java`

Reusable framework providing:
- Container health validation with detailed diagnostics
- External service connectivity checks with timeout handling
- Performance metrics (response times)
- Detailed logging and reporting
- Support for required vs optional services

## How It Works

### Container Startup Flow

1. **@BeforeAll** in BaseIntegrationTest starts containers
2. Containers are started with reuse enabled for performance
3. Cassandra executes init script to create keyspace and tables
4. **@DynamicPropertySource** registers container connection details
5. Spring Boot uses these properties to configure services
6. Tests execute against real containerized services

### Smoke Test Execution Flow

1. **Testcontainers Health Check**
   - Verifies each container is running
   - Checks container health status
   - Logs host, port, and image information

2. **External Services Connectivity** (if configured)
   - Tests connection to external Redis
   - Tests connection to external Kafka
   - Tests connection to external Cassandra
   - Tests API health check endpoints
   - Distinguishes between required and optional services

3. **End-to-End Functionality**
   - Performs Redis write/read operations
   - Sends Kafka messages
   - Queries Cassandra database
   - Validates complete workflow

## Configuration

### Testcontainers

Properties are dynamically registered:

```java
registry.add("spring.data.redis.host", redisContainer::getHost);
registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
registry.add("spring.cassandra.contact-points", cassandraContainer::getHost);
registry.add("spring.cassandra.port", cassandraContainer::getFirstMappedPort);
```

### External Services

Configure in `application-test.yml`:

```yaml
external:
  redis:
    url: ${EXTERNAL_REDIS_URL:}
  kafka:
    url: ${EXTERNAL_KAFKA_URL:}
  cassandra:
    url: ${EXTERNAL_CASSANDRA_URL:}
  api:
    health-check-url: ${EXTERNAL_API_HEALTH_CHECK_URL:}
```

Or via environment variables:
- `EXTERNAL_REDIS_URL=redis://prod-redis:6379`
- `EXTERNAL_KAFKA_URL=kafka-broker:9092`
- `EXTERNAL_CASSANDRA_URL=cassandra-cluster:9042`
- `EXTERNAL_API_HEALTH_CHECK_URL=https://api.example.com/health`

## Running Tests

### Using Maven

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=SmokeTest
mvn test -Dtest=EnhancedSmokeTest

# Run with external services configured
EXTERNAL_API_HEALTH_CHECK_URL=https://api.example.com/health mvn test
```

### Using IDE

1. Run `SmokeTest` or `EnhancedSmokeTest` directly
2. Containers will start automatically
3. Tests will execute against containerized services
4. Containers will stop after tests complete (unless reuse is enabled)

## Container Reuse

To enable container reuse for faster test execution:

1. Create file: `~/.testcontainers.properties`
2. Add: `testcontainers.reuse.enable=true`

This keeps containers running between test executions, significantly reducing startup time.

## Troubleshooting

### Cassandra Connection Issues

- Ensure `cassandraTemplate` bean is created (check Spring logs)
- Verify keyspace is created (check `cassandra-init.cql`)
- Check Cassandra container is healthy: `docker ps`

### Redis Connection Issues

- Verify Redis container is running
- Check port mapping is correct
- Test connection: `redis-cli -h localhost -p <mapped-port>`

### Kafka Connection Issues

- Ensure Kafka container has fully started (can take 10-20 seconds)
- Check bootstrap servers configuration
- Verify topic creation (auto-created by default)

## Benefits

### For Development
- Test against real services, not mocks
- Consistent environment across developers
- Fast feedback loop

### For CI/CD
- No need for external service dependencies
- Reproducible test environment
- Parallel test execution capability

### For Production Confidence
- Tests validate actual integrations
- Catch configuration issues early
- Verify external service connectivity before deployment

## Best Practices

1. **Always extend BaseIntegrationTest** for integration tests
2. **Use @DynamicPropertySource** for container properties
3. **Enable container reuse** for local development
4. **Configure external services** in application-test.yml
5. **Distinguish required vs optional** external services
6. **Add comprehensive logging** for debugging
7. **Test end-to-end workflows** in smoke tests

## Next Steps

1. Add more smoke test scenarios for specific use cases
2. Configure external service URLs for staging/production testing
3. Add performance benchmarks to smoke tests
4. Integrate smoke tests into CI/CD pipeline
5. Add test result reporting and notifications
