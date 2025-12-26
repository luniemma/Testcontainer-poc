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
✅ **Framework Agnostic** - Can be implemented in Java, Node.js, Python, Go, .NET, and more

## Core Concepts

The framework is built on three fundamental pillars:

### 1. Container Health Validation
Validates that all Testcontainers are:
- Running and accessible
- Responding to health checks
- Properly exposed on mapped ports
- Ready to accept connections

### 2. External Service Connectivity
Tests connectivity to:
- Production/staging databases
- External APIs and services
- Third-party integrations
- Cloud services

### 3. End-to-End Testing
Executes real workflows:
- Data storage and retrieval
- Message queue processing
- Complete business logic flows
- Integration between services

## Step-by-Step Implementation Guide

This guide shows how to implement the smoke test framework from scratch in any technology stack.

### Step 1: Understand the Architecture

The framework consists of three main components:

```
┌─────────────────────────────────────────┐
│     Smoke Test Framework Base Class     │
├─────────────────────────────────────────┤
│  - testTestcontainersHealth()           │
│  - testExternalServicesConnectivity()   │
│  - testEndToEndFunctionality()          │
└─────────────────────────────────────────┘
            ↓ extends
┌─────────────────────────────────────────┐
│      Your Application Smoke Test        │
├─────────────────────────────────────────┤
│  - getTestcontainers()                  │
│  - getExternalServices()                │
│  - performEndToEndTest()                │
└─────────────────────────────────────────┘
            ↓ uses
┌─────────────────────────────────────────┐
│        Health Check Utilities           │
├─────────────────────────────────────────┤
│  - testTcpConnection()                  │
│  - testHttpEndpoint()                   │
│  - testConnectionWithRetry()            │
│  - waitForServiceAvailability()         │
└─────────────────────────────────────────┘
```

### Step 2: Create Health Check Utilities

First, create utilities for testing different types of connections. These are reusable across all tests.

**Key Functions to Implement:**

1. **TCP Connection Test**
   - Opens a socket connection to host:port
   - Times out after configurable duration
   - Returns true/false for success/failure

2. **HTTP Endpoint Test**
   - Makes HTTP GET request to URL
   - Checks for 2xx response code
   - Returns true/false for success/failure

3. **Connection Retry Logic**
   - Wraps any connection test
   - Retries N times with delay
   - Useful for flaky connections

4. **Wait for Availability**
   - Polls connection until successful
   - Times out after maximum duration
   - Useful for services with slow startup

**Example Implementation (Pseudocode):**

```pseudocode
function testTcpConnection(host, port, timeoutMs):
    try:
        socket = createSocket()
        socket.connect(host, port, timeoutMs)
        socket.close()
        log("TCP connection successful to {host}:{port}")
        return true
    catch error:
        log("TCP connection failed to {host}:{port} - {error}")
        return false

function testHttpEndpoint(url, timeoutMs):
    try:
        response = httpGet(url, timeout=timeoutMs)
        success = response.statusCode >= 200 AND response.statusCode < 300
        log("HTTP check {url} returned {response.statusCode}")
        return success
    catch error:
        log("HTTP check failed for {url} - {error}")
        return false

function testConnectionWithRetry(connectionTest, retryCount, delayMs):
    for attempt in 1 to retryCount:
        result = connectionTest()
        if result:
            if attempt > 1:
                log("Connection successful on attempt {attempt}/{retryCount}")
            return true
        if attempt < retryCount:
            sleep(delayMs)
    log("All {retryCount} connection attempts failed")
    return false

function waitForServiceAvailability(host, port, timeoutDuration):
    deadline = currentTime() + timeoutDuration
    while currentTime() < deadline:
        if testTcpConnection(host, port, 1000):
            return true
        sleep(500)
    log("Service {host}:{port} did not become available within timeout")
    return false
```

### Step 3: Create Data Models

Define classes/structures to hold information about containers and services.

**ContainerInfo Model:**
```pseudocode
class ContainerInfo:
    name: string          # e.g., "Redis", "Kafka"
    type: string          # e.g., "Cache", "Messaging"
    host: string          # e.g., "localhost"
    port: integer         # e.g., 6379
    image: string         # e.g., "redis:7-alpine"
    running: boolean      # Is container running?
    healthy: boolean      # Is container healthy?
```

**ExternalServiceInfo Model:**
```pseudocode
class ExternalServiceInfo:
    name: string              # e.g., "Production API"
    type: string              # e.g., "REST API"
    url: string               # e.g., "https://api.example.com"
    required: boolean         # Must this service be available?
    connectionTester: function # Function that returns boolean
```

**HealthCheckResult Model:**
```pseudocode
class HealthCheckResult:
    serviceName: string
    healthy: boolean
    message: string
    responseTime: duration
    timestamp: datetime
```

### Step 4: Create the Base Framework Class

This is the core of the framework. It provides three test methods and abstract methods for subclasses.

**Framework Structure:**

```pseudocode
abstract class SmokeTestFramework:

    healthCheckResults: map<string, HealthCheckResult>

    # Abstract methods that subclasses must implement
    abstract function getTestcontainers(): list<ContainerInfo>
    abstract function getExternalServices(): list<ExternalServiceInfo>
    abstract function performEndToEndTest(): void

    # Test Method 1: Validate all Testcontainers
    @Test
    function testTestcontainersHealth():
        log("=== Starting Testcontainers Health Check ===")
        startTime = currentTime()

        containers = getTestcontainers()
        assert containers is not empty, "At least one container required"

        for container in containers:
            result = validateContainer(container)
            healthCheckResults[container.name] = result

            assert result.healthy,
                "Container '{container.name}' health check failed: {result.message}"

        duration = currentTime() - startTime
        log("=== Testcontainers Health Check Completed in {duration}ms ===")
        logHealthCheckSummary()

    # Test Method 2: Validate external service connectivity
    @Test
    function testExternalServicesConnectivity():
        log("=== Starting External Services Connectivity Check ===")
        startTime = currentTime()

        services = getExternalServices()

        if services is empty:
            log("No external services configured")
            return

        for service in services:
            result = validateExternalService(service)
            healthCheckResults[service.name] = result

            if service.required:
                assert result.healthy,
                    "Required service '{service.name}' connectivity failed: {result.message}"
            else:
                if not result.healthy:
                    log("Optional service '{service.name}' connectivity failed: {result.message}")

        duration = currentTime() - startTime
        log("=== External Services Connectivity Check Completed in {duration}ms ===")
        logHealthCheckSummary()

    # Test Method 3: Validate end-to-end functionality
    @Test
    function testEndToEndFunctionality():
        log("=== Starting End-to-End Functionality Check ===")
        startTime = currentTime()

        try:
            performEndToEndTest()
            log("End-to-end functionality check passed")
        catch error:
            log("End-to-end functionality check failed: {error}")
            fail("End-to-end test failed: {error}")

        duration = currentTime() - startTime
        log("=== End-to-End Functionality Check Completed in {duration}ms ===")

    # Helper: Validate a container
    function validateContainer(container):
        startTime = currentTime()

        log("Checking container: {container.name} ({container.type})")

        if not container.running:
            return HealthCheckResult.unhealthy(
                container.name,
                "Container is not running",
                currentTime() - startTime
            )

        if not container.healthy:
            return HealthCheckResult.unhealthy(
                container.name,
                "Container health check failed",
                currentTime() - startTime
            )

        diagnostics = "Host: {container.host}, Port: {container.port}, Image: {container.image}"
        log("Container '{container.name}' is healthy: {diagnostics}")

        return HealthCheckResult.healthy(
            container.name,
            diagnostics,
            currentTime() - startTime
        )

    # Helper: Validate an external service
    function validateExternalService(service):
        startTime = currentTime()

        log("Checking external service: {service.name} ({service.url})")

        isConnected = service.connectionTester()

        if not isConnected:
            return HealthCheckResult.unhealthy(
                service.name,
                "Failed to connect to external service",
                currentTime() - startTime
            )

        diagnostics = "URL: {service.url}, Type: {service.type}"
        log("External service '{service.name}' is accessible: {diagnostics}")

        return HealthCheckResult.healthy(
            service.name,
            diagnostics,
            currentTime() - startTime
        )

    # Helper: Log summary of all health checks
    function logHealthCheckSummary():
        log("=== Health Check Summary ===")

        healthyCount = count(healthCheckResults, where result.healthy is true)
        unhealthyCount = count(healthCheckResults, where result.healthy is false)

        log("Total Checks: {healthCheckResults.size}")
        log("Healthy: {healthyCount}")
        log("Unhealthy: {unhealthyCount}")

        for (name, result) in healthCheckResults:
            log("  - {name}: {result.healthy ? 'HEALTHY' : 'UNHEALTHY'} ({result.responseTime}ms)")

        log("============================")
```

### Step 5: Implement Your Application's Smoke Test

Now create a concrete implementation for your application by extending the base framework.

```pseudocode
class MyApplicationSmokeTest extends SmokeTestFramework:

    # Dependency injection or initialization
    redisContainer: Container
    kafkaContainer: Container
    cassandraContainer: Container

    redisService: RedisService
    kafkaProducer: KafkaProducer
    cassandraService: CassandraService

    # Implementation 1: Define your containers
    function getTestcontainers():
        containers = []

        if redisContainer is not null:
            containers.add(new ContainerInfo(
                name: "Redis",
                type: "Cache",
                host: redisContainer.getHost(),
                port: redisContainer.getMappedPort(6379),
                image: redisContainer.getDockerImageName(),
                running: redisContainer.isRunning(),
                healthy: redisContainer.isRunning()
            ))

        if kafkaContainer is not null:
            containers.add(new ContainerInfo(
                name: "Kafka",
                type: "Messaging",
                host: kafkaContainer.getHost(),
                port: kafkaContainer.getFirstMappedPort(),
                image: kafkaContainer.getDockerImageName(),
                running: kafkaContainer.isRunning(),
                healthy: kafkaContainer.isRunning()
            ))

        if cassandraContainer is not null:
            containers.add(new ContainerInfo(
                name: "Cassandra",
                type: "Database",
                host: cassandraContainer.getHost(),
                port: cassandraContainer.getFirstMappedPort(),
                image: cassandraContainer.getDockerImageName(),
                running: cassandraContainer.isRunning(),
                healthy: cassandraContainer.isRunning()
            ))

        return containers

    # Implementation 2: Define your external services
    function getExternalServices():
        services = []

        externalRedisUrl = getEnvVar("EXTERNAL_REDIS_URL")
        if externalRedisUrl is not empty:
            services.add(new ExternalServiceInfo(
                name: "External Redis",
                type: "Cache",
                url: externalRedisUrl,
                required: false,
                connectionTester: function() {
                    parts = externalRedisUrl.split(":")
                    host = parts[0]
                    port = parseInt(parts[1]) or 6379
                    return testTcpConnection(host, port)
                }
            ))

        externalApiUrl = getEnvVar("EXTERNAL_API_HEALTH_CHECK_URL")
        if externalApiUrl is not empty:
            services.add(new ExternalServiceInfo(
                name: "External API",
                type: "REST API",
                url: externalApiUrl,
                required: true,  # This is a critical service
                connectionTester: function() {
                    return testHttpEndpoint(externalApiUrl)
                }
            ))

        return services

    # Implementation 3: Define your end-to-end test
    function performEndToEndTest():
        # Test 1: Redis cache workflow
        testKey = "e2e-smoke-test"
        testValue = "end-to-end-working"

        redisService.set(testKey, testValue)
        redisResult = redisService.get(testKey)

        assert redisResult == testValue, "Redis end-to-end test failed"

        # Test 2: Kafka messaging workflow
        kafkaProducer.sendMessage("e2e-smoke-test-message")

        # Test 3: Cassandra database workflow
        users = cassandraService.getAllUsers()
        assert users is not null, "Cassandra query failed"

        # Cleanup
        redisService.delete(testKey)
```

### Step 6: Configure External Services (Optional)

Add configuration to test against real production/staging services.

**Configuration File (YAML example):**
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
  api:
    health-check-url: ${EXTERNAL_API_HEALTH_CHECK_URL:}
    enabled: ${EXTERNAL_API_ENABLED:false}
```

**Environment Variables:**
```bash
export EXTERNAL_REDIS_URL="redis://prod-redis.att.com:6379"
export EXTERNAL_KAFKA_URL="prod-kafka.att.com:9092"
export EXTERNAL_CASSANDRA_URL="prod-cassandra.att.com:9042"
export EXTERNAL_API_HEALTH_CHECK_URL="https://api.att.com/health"
```

### Step 7: Run Your Smoke Tests

Execute the tests using your testing framework's runner.

**Java (Maven):**
```bash
mvn test -Dtest=MyApplicationSmokeTest
```

**Node.js (Jest):**
```bash
npm test -- MyApplicationSmokeTest
```

**Python (pytest):**
```bash
pytest tests/smoke/test_my_application_smoke.py
```

**Go:**
```bash
go test -v ./tests/smoke/...
```

**.NET:**
```bash
dotnet test --filter "FullyQualifiedName~SmokeTest"
```

## Quick Start (Java)

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

## Framework-Specific Implementations

### Node.js / TypeScript Implementation

```typescript
// health-check-utils.ts
import net from 'net';
import http from 'http';
import https from 'https';

export class HealthCheckUtils {
  static async testTcpConnection(host: string, port: number, timeoutMs: number = 5000): Promise<boolean> {
    return new Promise((resolve) => {
      const socket = new net.Socket();
      const timeout = setTimeout(() => {
        socket.destroy();
        resolve(false);
      }, timeoutMs);

      socket.connect(port, host, () => {
        clearTimeout(timeout);
        socket.destroy();
        resolve(true);
      });

      socket.on('error', () => {
        clearTimeout(timeout);
        resolve(false);
      });
    });
  }

  static async testHttpEndpoint(url: string, timeoutMs: number = 5000): Promise<boolean> {
    return new Promise((resolve) => {
      const httpModule = url.startsWith('https') ? https : http;
      const req = httpModule.get(url, { timeout: timeoutMs }, (res) => {
        resolve(res.statusCode >= 200 && res.statusCode < 300);
      });

      req.on('error', () => resolve(false));
      req.on('timeout', () => {
        req.destroy();
        resolve(false);
      });
    });
  }

  static async testConnectionWithRetry(
    connectionTest: () => Promise<boolean>,
    retryCount: number = 3,
    delayMs: number = 1000
  ): Promise<boolean> {
    for (let attempt = 1; attempt <= retryCount; attempt++) {
      if (await connectionTest()) {
        return true;
      }
      if (attempt < retryCount) {
        await new Promise(resolve => setTimeout(resolve, delayMs));
      }
    }
    return false;
  }
}

// smoke-test-framework.ts
export interface ContainerInfo {
  name: string;
  type: string;
  host: string;
  port: number;
  image: string;
  running: boolean;
  healthy: boolean;
}

export interface ExternalServiceInfo {
  name: string;
  type: string;
  url: string;
  required: boolean;
  connectionTester: () => Promise<boolean>;
}

export abstract class SmokeTestFramework {
  protected abstract getTestcontainers(): ContainerInfo[];
  protected abstract getExternalServices(): ExternalServiceInfo[];
  protected abstract performEndToEndTest(): Promise<void>;

  async testTestcontainersHealth(): Promise<void> {
    const containers = this.getTestcontainers();
    expect(containers.length).toBeGreaterThan(0);

    for (const container of containers) {
      expect(container.running).toBe(true);
      expect(container.healthy).toBe(true);
    }
  }

  async testExternalServicesConnectivity(): Promise<void> {
    const services = this.getExternalServices();

    for (const service of services) {
      const connected = await service.connectionTester();
      if (service.required) {
        expect(connected).toBe(true);
      }
    }
  }

  async testEndToEndFunctionality(): Promise<void> {
    await this.performEndToEndTest();
  }
}

// my-app-smoke-test.ts
import { GenericContainer, StartedTestContainer } from 'testcontainers';
import { SmokeTestFramework, ContainerInfo, ExternalServiceInfo } from './smoke-test-framework';
import { HealthCheckUtils } from './health-check-utils';

class MyAppSmokeTest extends SmokeTestFramework {
  private redisContainer: StartedTestContainer;

  async beforeAll() {
    this.redisContainer = await new GenericContainer('redis:7-alpine')
      .withExposedPorts(6379)
      .start();
  }

  protected getTestcontainers(): ContainerInfo[] {
    return [{
      name: 'Redis',
      type: 'Cache',
      host: this.redisContainer.getHost(),
      port: this.redisContainer.getMappedPort(6379),
      image: 'redis:7-alpine',
      running: true,
      healthy: true
    }];
  }

  protected getExternalServices(): ExternalServiceInfo[] {
    const externalApiUrl = process.env.EXTERNAL_API_HEALTH_CHECK_URL;
    if (!externalApiUrl) return [];

    return [{
      name: 'External API',
      type: 'REST API',
      url: externalApiUrl,
      required: true,
      connectionTester: () => HealthCheckUtils.testHttpEndpoint(externalApiUrl)
    }];
  }

  protected async performEndToEndTest(): Promise<void> {
    // Your end-to-end test logic here
  }
}

describe('Smoke Tests', () => {
  const smokeTest = new MyAppSmokeTest();

  beforeAll(async () => {
    await smokeTest.beforeAll();
  });

  test('Testcontainers Health Check', async () => {
    await smokeTest.testTestcontainersHealth();
  });

  test('External Services Connectivity Check', async () => {
    await smokeTest.testExternalServicesConnectivity();
  });

  test('End-to-End Functionality Check', async () => {
    await smokeTest.testEndToEndFunctionality();
  });
});
```

### Python Implementation

```python
# health_check_utils.py
import socket
import requests
import time
from typing import Callable

class HealthCheckUtils:
    @staticmethod
    def test_tcp_connection(host: str, port: int, timeout_ms: int = 5000) -> bool:
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(timeout_ms / 1000.0)
            result = sock.connect_ex((host, port))
            sock.close()
            return result == 0
        except Exception:
            return False

    @staticmethod
    def test_http_endpoint(url: str, timeout_ms: int = 5000) -> bool:
        try:
            response = requests.get(url, timeout=timeout_ms / 1000.0)
            return 200 <= response.status_code < 300
        except Exception:
            return False

    @staticmethod
    def test_connection_with_retry(
        connection_test: Callable[[], bool],
        retry_count: int = 3,
        delay_ms: int = 1000
    ) -> bool:
        for attempt in range(1, retry_count + 1):
            if connection_test():
                return True
            if attempt < retry_count:
                time.sleep(delay_ms / 1000.0)
        return False

# smoke_test_framework.py
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List, Callable

@dataclass
class ContainerInfo:
    name: str
    type: str
    host: str
    port: int
    image: str
    running: bool
    healthy: bool

@dataclass
class ExternalServiceInfo:
    name: str
    type: str
    url: str
    required: bool
    connection_tester: Callable[[], bool]

class SmokeTestFramework(ABC):
    @abstractmethod
    def get_testcontainers(self) -> List[ContainerInfo]:
        pass

    @abstractmethod
    def get_external_services(self) -> List[ExternalServiceInfo]:
        pass

    @abstractmethod
    def perform_end_to_end_test(self) -> None:
        pass

    def test_testcontainers_health(self):
        containers = self.get_testcontainers()
        assert len(containers) > 0, "At least one container required"

        for container in containers:
            assert container.running, f"Container '{container.name}' is not running"
            assert container.healthy, f"Container '{container.name}' is not healthy"

    def test_external_services_connectivity(self):
        services = self.get_external_services()

        for service in services:
            connected = service.connection_tester()
            if service.required:
                assert connected, f"Required service '{service.name}' is not accessible"

    def test_end_to_end_functionality(self):
        self.perform_end_to_end_test()

# test_my_app_smoke.py
import pytest
import os
from testcontainers.redis import RedisContainer
from smoke_test_framework import SmokeTestFramework, ContainerInfo, ExternalServiceInfo
from health_check_utils import HealthCheckUtils

class TestMyAppSmoke(SmokeTestFramework):
    @pytest.fixture(scope="class", autouse=True)
    def setup_containers(self):
        self.redis_container = RedisContainer("redis:7-alpine")
        self.redis_container.start()
        yield
        self.redis_container.stop()

    def get_testcontainers(self) -> List[ContainerInfo]:
        return [
            ContainerInfo(
                name="Redis",
                type="Cache",
                host=self.redis_container.get_container_host_ip(),
                port=int(self.redis_container.get_exposed_port(6379)),
                image="redis:7-alpine",
                running=True,
                healthy=True
            )
        ]

    def get_external_services(self) -> List[ExternalServiceInfo]:
        external_api_url = os.getenv("EXTERNAL_API_HEALTH_CHECK_URL")
        if not external_api_url:
            return []

        return [
            ExternalServiceInfo(
                name="External API",
                type="REST API",
                url=external_api_url,
                required=True,
                connection_tester=lambda: HealthCheckUtils.test_http_endpoint(external_api_url)
            )
        ]

    def perform_end_to_end_test(self):
        # Your end-to-end test logic here
        pass

    def test_containers_health(self):
        self.test_testcontainers_health()

    def test_services_connectivity(self):
        self.test_external_services_connectivity()

    def test_e2e_functionality(self):
        self.test_end_to_end_functionality()
```

### Go Implementation

```go
// health_check_utils.go
package smoketest

import (
    "net"
    "net/http"
    "time"
)

type HealthCheckUtils struct{}

func (h *HealthCheckUtils) TestTCPConnection(host string, port string, timeoutMs int) bool {
    timeout := time.Duration(timeoutMs) * time.Millisecond
    conn, err := net.DialTimeout("tcp", net.JoinHostPort(host, port), timeout)
    if err != nil {
        return false
    }
    defer conn.Close()
    return true
}

func (h *HealthCheckUtils) TestHTTPEndpoint(url string, timeoutMs int) bool {
    timeout := time.Duration(timeoutMs) * time.Millisecond
    client := &http.Client{Timeout: timeout}

    resp, err := client.Get(url)
    if err != nil {
        return false
    }
    defer resp.Body.Close()

    return resp.StatusCode >= 200 && resp.StatusCode < 300
}

func (h *HealthCheckUtils) TestConnectionWithRetry(
    connectionTest func() bool,
    retryCount int,
    delayMs int,
) bool {
    for attempt := 1; attempt <= retryCount; attempt++ {
        if connectionTest() {
            return true
        }
        if attempt < retryCount {
            time.Sleep(time.Duration(delayMs) * time.Millisecond)
        }
    }
    return false
}

// smoke_test_framework.go
package smoketest

type ContainerInfo struct {
    Name    string
    Type    string
    Host    string
    Port    string
    Image   string
    Running bool
    Healthy bool
}

type ExternalServiceInfo struct {
    Name             string
    Type             string
    URL              string
    Required         bool
    ConnectionTester func() bool
}

type SmokeTestFramework interface {
    GetTestcontainers() []ContainerInfo
    GetExternalServices() []ExternalServiceInfo
    PerformEndToEndTest() error
}

func TestTestcontainersHealth(framework SmokeTestFramework, t *testing.T) {
    containers := framework.GetTestcontainers()
    if len(containers) == 0 {
        t.Fatal("At least one container required")
    }

    for _, container := range containers {
        if !container.Running {
            t.Errorf("Container '%s' is not running", container.Name)
        }
        if !container.Healthy {
            t.Errorf("Container '%s' is not healthy", container.Name)
        }
    }
}

func TestExternalServicesConnectivity(framework SmokeTestFramework, t *testing.T) {
    services := framework.GetExternalServices()

    for _, service := range services {
        connected := service.ConnectionTester()
        if service.Required && !connected {
            t.Errorf("Required service '%s' is not accessible", service.Name)
        }
    }
}

func TestEndToEndFunctionality(framework SmokeTestFramework, t *testing.T) {
    err := framework.PerformEndToEndTest()
    if err != nil {
        t.Errorf("End-to-end test failed: %v", err)
    }
}

// my_app_smoke_test.go
package smoketest

import (
    "os"
    "testing"
    "github.com/testcontainers/testcontainers-go"
    "github.com/testcontainers/testcontainers-go/wait"
)

type MyAppSmokeTest struct {
    redisContainer testcontainers.Container
    healthCheck    *HealthCheckUtils
}

func (m *MyAppSmokeTest) GetTestcontainers() []ContainerInfo {
    host, _ := m.redisContainer.Host(context.Background())
    port, _ := m.redisContainer.MappedPort(context.Background(), "6379")

    return []ContainerInfo{
        {
            Name:    "Redis",
            Type:    "Cache",
            Host:    host,
            Port:    port.Port(),
            Image:   "redis:7-alpine",
            Running: true,
            Healthy: true,
        },
    }
}

func (m *MyAppSmokeTest) GetExternalServices() []ExternalServiceInfo {
    externalApiURL := os.Getenv("EXTERNAL_API_HEALTH_CHECK_URL")
    if externalApiURL == "" {
        return []ExternalServiceInfo{}
    }

    return []ExternalServiceInfo{
        {
            Name:     "External API",
            Type:     "REST API",
            URL:      externalApiURL,
            Required: true,
            ConnectionTester: func() bool {
                return m.healthCheck.TestHTTPEndpoint(externalApiURL, 5000)
            },
        },
    }
}

func (m *MyAppSmokeTest) PerformEndToEndTest() error {
    // Your end-to-end test logic here
    return nil
}

func TestSmoke(t *testing.T) {
    ctx := context.Background()

    redisContainer, err := testcontainers.GenericContainer(ctx, testcontainers.GenericContainerRequest{
        ContainerRequest: testcontainers.ContainerRequest{
            Image:        "redis:7-alpine",
            ExposedPorts: []string{"6379/tcp"},
            WaitingFor:   wait.ForLog("Ready to accept connections"),
        },
        Started: true,
    })
    if err != nil {
        t.Fatal(err)
    }
    defer redisContainer.Terminate(ctx)

    smokeTest := &MyAppSmokeTest{
        redisContainer: redisContainer,
        healthCheck:    &HealthCheckUtils{},
    }

    t.Run("Testcontainers Health Check", func(t *testing.T) {
        TestTestcontainersHealth(smokeTest, t)
    })

    t.Run("External Services Connectivity Check", func(t *testing.T) {
        TestExternalServicesConnectivity(smokeTest, t)
    })

    t.Run("End-to-End Functionality Check", func(t *testing.T) {
        TestEndToEndFunctionality(smokeTest, t)
    })
}
```

## Validation and Testing

To ensure your implementation is functional:

### 1. Unit Test the Health Check Utilities

Test each utility function independently:

```java
@Test
void testTcpConnectionUtility() {
    // Test with running service
    assertTrue(HealthCheckUtils.testTcpConnection("localhost", 6379));

    // Test with non-existent service
    assertFalse(HealthCheckUtils.testTcpConnection("localhost", 9999));
}

@Test
void testHttpEndpointUtility() {
    // Test with valid endpoint
    assertTrue(HealthCheckUtils.testHttpEndpoint("https://httpbin.org/status/200"));

    // Test with invalid endpoint
    assertFalse(HealthCheckUtils.testHttpEndpoint("https://httpbin.org/status/500"));
}

@Test
void testRetryLogic() {
    AtomicInteger attempts = new AtomicInteger(0);

    // Should succeed on second attempt
    boolean result = HealthCheckUtils.testConnectionWithRetry(() -> {
        return attempts.incrementAndGet() >= 2;
    }, 3, 100);

    assertTrue(result);
    assertEquals(2, attempts.get());
}
```

### 2. Test Container Validation

Verify that your framework correctly identifies container states:

```java
@Test
void testContainerValidation() {
    // Start a real container
    GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    redis.start();

    ContainerInfo info = new ContainerInfo(
        "Redis", "Cache",
        redis.getHost(), redis.getMappedPort(6379),
        "redis:7-alpine", redis.isRunning(), true
    );

    // Verify validation passes
    HealthCheckResult result = validateContainer(info);
    assertTrue(result.isHealthy());

    redis.stop();
}
```

### 3. Test External Service Connectivity

Verify external service checking works:

```java
@Test
void testExternalServiceValidation() {
    ExternalServiceInfo service = new ExternalServiceInfo(
        "Test API", "REST",
        "https://httpbin.org/status/200",
        true,
        () -> HealthCheckUtils.testHttpEndpoint("https://httpbin.org/status/200")
    );

    HealthCheckResult result = validateExternalService(service);
    assertTrue(result.isHealthy());
}
```

### 4. Test End-to-End Workflow

Verify your complete smoke test runs successfully:

```bash
# Run your smoke test
mvn test -Dtest=MyApplicationSmokeTest

# Verify output shows:
# - All containers healthy
# - All services connected
# - End-to-end test passed
```

### 5. Test Failure Scenarios

Ensure framework correctly handles failures:

```java
@Test
void testFailureDetection() {
    // Test with stopped container
    ContainerInfo stoppedContainer = new ContainerInfo(
        "Stopped", "Test", "localhost", 9999,
        "test:latest", false, false
    );

    HealthCheckResult result = validateContainer(stoppedContainer);
    assertFalse(result.isHealthy());
    assertTrue(result.getMessage().contains("not running"));
}

@Test
void testRequiredServiceFailure() {
    ExternalServiceInfo requiredService = new ExternalServiceInfo(
        "Critical API", "REST",
        "https://nonexistent.example.com",
        true,  // required = true
        () -> false
    );

    // Should throw assertion error for required service
    assertThrows(AssertionError.class, () -> {
        testExternalServicesConnectivity();
    });
}
```

## Support

For questions or issues with the Smoke Test Framework:

- **Documentation:** This file
- **Examples:** See `EnhancedSmokeTest.java` in this project
- **Issues:** Contact ATT DevOps team

## Version History

- **v1.1.0** (2025-12-17) - Enhanced implementation guide
  - Step-by-step implementation instructions
  - Framework-agnostic pseudocode examples
  - Node.js/TypeScript implementation
  - Python implementation
  - Go implementation
  - Validation and testing guidelines

- **v1.0.0** (2025-12-16) - Initial release
  - Core framework with Testcontainers and external service validation
  - Health check utilities
  - Multi-format reporting
  - Comprehensive documentation
