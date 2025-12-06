# ATT Organization Smoke Test Framework

## Overview

This document describes the reusable Smoke Test Framework designed for use across all applications in the ATT organization. The framework provides comprehensive validation of:

- **Testcontainers Health** - Ensures all containerized dependencies are running correctly
- **External Service Connectivity** - Validates connections to production/staging external services
- **End-to-End Functionality** - Verifies complete application workflows

## Why Use This Framework?

✅ **Standardized Testing** - Consistent smoke testing across all ATT applications
✅ **Early Detection** - Catch infrastructure issues before they reach production
✅ **Comprehensive Diagnostics** - Detailed logging and reporting for troubleshooting
✅ **Flexible Configuration** - Supports both Testcontainers and external services
✅ **Reusable Components** - Reduce code duplication across projects

## Quick Start

### 1. Add Framework to Your Project

Copy the following files to your test directory:

```
src/test/java/com/example/testcontainers/framework/
├── SmokeTestFramework.java      # Base framework class
├── HealthCheckUtils.java        # Health check utilities
└── SmokeTestReport.java         # Report generator
```

### 2. Create Your Smoke Test

Extend `SmokeTestFramework` and implement the required methods:

```java
package com.yourcompany.yourapp;

import com.example.testcontainers.framework.SmokeTestFramework;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class YourAppSmokeTest extends SmokeTestFramework {

    @Override
    protected List<ContainerInfo> getTestcontainers() {
        // Return list of your Testcontainers
        return Arrays.asList(
            new ContainerInfo("Redis", "Cache",
                redisContainer.getHost(),
                redisContainer.getMappedPort(6379),
                "redis:7-alpine",
                redisContainer.isRunning(),
                true)
        );
    }

    @Override
    protected List<ExternalServiceInfo> getExternalServices() {
        // Return list of external services to test
        return Arrays.asList(
            new ExternalServiceInfo("Production API", "REST API",
                "https://api.att.com/health",
                true,  // required
                () -> testHttpEndpoint("https://api.att.com/health"))
        );
    }

    @Override
    protected void performEndToEndTest() throws Exception {
        // Implement your end-to-end test
        // Example: Create user, send message, verify in database
    }
}
```

### 3. Configure External Services

Add external service configuration to your `application-test.yml`:

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

### 4. Run Your Smoke Test

```bash
mvn test -Dtest=YourAppSmokeTest
```

## Framework Components

### 1. SmokeTestFramework

The base class that all smoke tests extend. Provides three main test methods:

- `testTestcontainersHealth()` - Validates all Testcontainers
- `testExternalServicesConnectivity()` - Tests external service connections
- `testEndToEndFunctionality()` - Runs application-specific end-to-end tests

### 2. HealthCheckUtils

Utility methods for testing various types of connections:

```java
// TCP connection
HealthCheckUtils.testTcpConnection("localhost", 6379);

// HTTP endpoint
HealthCheckUtils.testHttpEndpoint("https://api.example.com/health");

// With retry logic
HealthCheckUtils.testConnectionWithRetry(() ->
    testTcpConnection("localhost", 9092), 3, 1000);

// Wait for service availability
HealthCheckUtils.waitForServiceAvailability("localhost", 5432,
    Duration.ofSeconds(30));

// Measure connection time
ConnectionMetrics metrics = HealthCheckUtils.measureConnectionTime(() ->
    testTcpConnection("localhost", 6379));
```

### 3. SmokeTestReport

Generate detailed reports in multiple formats:

```java
SmokeTestReport report = new SmokeTestReport("MyApp", "Production");
report.addTestResult("Redis Health", true, "Connected successfully", 150);
report.addTestResult("Kafka Health", true, "Connected successfully", 200);

// Generate reports
report.printConsoleReport();
report.generateJsonReport("target/smoke-test-report.json");
report.generateHtmlReport("target/smoke-test-report.html");
report.generateMarkdownReport("target/smoke-test-report.md");
```

## Configuration Options

### Environment Variables

Set these environment variables to test external services:

```bash
# External service URLs
export EXTERNAL_REDIS_URL="redis://prod-redis.att.com:6379"
export EXTERNAL_KAFKA_URL="prod-kafka.att.com:9092"
export EXTERNAL_CASSANDRA_URL="prod-cassandra.att.com:9042"
export EXTERNAL_API_HEALTH_CHECK_URL="https://api.att.com/health"
```

### Spring Configuration

```yaml
external:
  redis:
    url: ${EXTERNAL_REDIS_URL:}
    enabled: ${EXTERNAL_REDIS_ENABLED:false}
  kafka:
    url: ${EXTERNAL_KAFKA_URL:}
    enabled: ${EXTERNAL_KAFKA_ENABLED:false}
  cassandra:
    url: ${EXTERNAL_CASSANDRA_URL:}
    enabled: ${EXTERNAL_CASSANDRA_ENABLED:false}
```

## Best Practices

### 1. Container Health Checks

Always implement proper health checks for Testcontainers:

```java
@Override
protected List<ContainerInfo> getTestcontainers() {
    List<ContainerInfo> containers = new ArrayList<>();

    if (redisContainer != null && redisContainer.isRunning()) {
        containers.add(new ContainerInfo(
            "Redis",
            "Cache",
            redisContainer.getHost(),
            redisContainer.getMappedPort(6379),
            redisContainer.getDockerImageName(),
            redisContainer.isRunning(),
            redisContainer.isHealthy()
        ));
    }

    return containers;
}
```

### 2. External Service Testing

Use connection testers that are appropriate for each service type:

```java
@Override
protected List<ExternalServiceInfo> getExternalServices() {
    List<ExternalServiceInfo> services = new ArrayList<>();

    // HTTP/REST API
    services.add(new ExternalServiceInfo(
        "User API",
        "REST API",
        apiUrl,
        true,  // required service
        () -> HealthCheckUtils.testHttpEndpoint(apiUrl + "/health")
    ));

    // Database connection
    services.add(new ExternalServiceInfo(
        "Production Database",
        "PostgreSQL",
        dbUrl,
        true,
        () -> HealthCheckUtils.testTcpConnection(dbHost, dbPort)
    ));

    return services;
}
```

### 3. End-to-End Tests

Keep end-to-end tests focused and realistic:

```java
@Override
protected void performEndToEndTest() throws Exception {
    // Test a complete workflow

    // 1. Store data
    String key = "e2e-test-" + UUID.randomUUID();
    cacheService.set(key, "test-value");

    // 2. Verify storage
    String value = cacheService.get(key);
    if (!"test-value".equals(value)) {
        throw new AssertionError("Cache test failed");
    }

    // 3. Send message
    messageService.send("e2e-test-topic", "test-message");

    // 4. Verify in database
    List<User> users = userRepository.findAll();
    if (users == null) {
        throw new AssertionError("Database test failed");
    }

    // 5. Cleanup
    cacheService.delete(key);
}
```

### 4. Logging

Use structured logging for better diagnostics:

```java
logger.info("Starting smoke test for application: {}", applicationName);
logger.debug("Testing container: {} on port {}", containerName, port);
logger.warn("Optional service {} is not available", serviceName);
logger.error("Critical service {} failed health check: {}", serviceName, error);
```

## CI/CD Integration

### Jenkins Pipeline

```groovy
stage('Smoke Tests') {
    steps {
        sh 'mvn test -Dtest=*SmokeTest'
    }
    post {
        always {
            publishHTML([
                reportDir: 'target',
                reportFiles: 'smoke-test-report.html',
                reportName: 'Smoke Test Report'
            ])
        }
    }
}
```

### GitHub Actions

```yaml
- name: Run Smoke Tests
  run: mvn test -Dtest=*SmokeTest

- name: Upload Smoke Test Reports
  uses: actions/upload-artifact@v3
  with:
    name: smoke-test-reports
    path: target/smoke-test-report.*
```

### GitLab CI

```yaml
smoke-test:
  stage: test
  script:
    - mvn test -Dtest=*SmokeTest
  artifacts:
    reports:
      junit: target/surefire-reports/*.xml
    paths:
      - target/smoke-test-report.*
```

## Troubleshooting

### Common Issues

#### 1. Container Not Starting

**Symptom:** Test fails with "Container is not running"

**Solution:**
- Check Docker is running: `docker ps`
- Verify sufficient resources: `docker system df`
- Check container logs: `docker logs <container-id>`
- Increase startup timeout in test configuration

#### 2. External Service Timeout

**Symptom:** External service connectivity test times out

**Solution:**
- Verify network connectivity: `curl <service-url>`
- Check firewall rules and security groups
- Verify service is actually running
- Increase timeout in health check configuration

#### 3. Intermittent Failures

**Symptom:** Tests pass sometimes and fail other times

**Solution:**
- Add retry logic using `HealthCheckUtils.testConnectionWithRetry()`
- Increase wait times for async operations
- Use `waitForServiceAvailability()` for services that need warmup time
- Check for race conditions in test setup

## Examples

### Example 1: Basic Smoke Test

```java
@SpringBootTest
public class BasicSmokeTest extends SmokeTestFramework {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected List<ContainerInfo> getTestcontainers() {
        return Arrays.asList(
            new ContainerInfo("Redis", "Cache",
                "localhost", 6379, "redis:7", true, true)
        );
    }

    @Override
    protected List<ExternalServiceInfo> getExternalServices() {
        return Collections.emptyList();
    }

    @Override
    protected void performEndToEndTest() throws Exception {
        redisTemplate.opsForValue().set("test", "value");
        assertEquals("value", redisTemplate.opsForValue().get("test"));
    }
}
```

### Example 2: Comprehensive Smoke Test with External Services

```java
@SpringBootTest
public class ComprehensiveSmokeTest extends SmokeTestFramework {

    @Value("${production.api.url}")
    private String prodApiUrl;

    @Override
    protected List<ContainerInfo> getTestcontainers() {
        return Arrays.asList(
            createContainerInfo("Redis", redisContainer),
            createContainerInfo("Kafka", kafkaContainer),
            createContainerInfo("Postgres", postgresContainer)
        );
    }

    @Override
    protected List<ExternalServiceInfo> getExternalServices() {
        return Arrays.asList(
            new ExternalServiceInfo("Production API", "REST",
                prodApiUrl, true,
                () -> HealthCheckUtils.testHttpEndpoint(prodApiUrl + "/health")),
            new ExternalServiceInfo("External Cache", "Redis",
                "prod-redis:6379", false,
                () -> HealthCheckUtils.testRedisConnection("prod-redis", 6379))
        );
    }

    @Override
    protected void performEndToEndTest() throws Exception {
        // Full workflow test
        testUserRegistration();
        testDataPersistence();
        testMessageProcessing();
    }
}
```

## Support

For questions or issues with the Smoke Test Framework:

- **Documentation:** This file
- **Examples:** See `EnhancedSmokeTest.java` in this project
- **Issues:** Contact ATT DevOps team

## Version History

- **v1.0.0** (2024-12-06) - Initial release
  - Core framework with Testcontainers and external service validation
  - Health check utilities
  - Multi-format reporting
  - Comprehensive documentation
