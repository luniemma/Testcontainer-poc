package com.example.testcontainers.framework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reusable Smoke Test Framework for ATT Organization
 *
 * This framework provides:
 * - Testcontainers health validation
 * - External service connectivity checks
 * - Detailed diagnostics and reporting
 * - Reusable across all applications
 *
 * Usage: Extend this class in your smoke tests and implement the abstract methods
 */
public abstract class SmokeTestFramework {

    private static final Logger logger = LoggerFactory.getLogger(SmokeTestFramework.class);
    private final Map<String, HealthCheckResult> healthCheckResults = new ConcurrentHashMap<>();

    /**
     * Test that all Testcontainers are running and healthy
     */
    @Test
    @DisplayName("Testcontainers Health Check")
    void testTestcontainersHealth() {
        logger.info("=== Starting Testcontainers Health Check ===");
        Instant startTime = Instant.now();

        List<ContainerInfo> containers = getTestcontainers();
        assertFalse(containers.isEmpty(), "At least one Testcontainer should be configured");

        for (ContainerInfo container : containers) {
            HealthCheckResult result = validateContainer(container);
            healthCheckResults.put(container.getName(), result);

            assertTrue(result.isHealthy(),
                String.format("Container '%s' health check failed: %s",
                    container.getName(), result.getMessage()));
        }

        Duration duration = Duration.between(startTime, Instant.now());
        logger.info("=== Testcontainers Health Check Completed in {}ms ===", duration.toMillis());
        logHealthCheckSummary();
    }

    /**
     * Test connectivity to external services
     */
    @Test
    @DisplayName("External Services Connectivity Check")
    void testExternalServicesConnectivity() {
        logger.info("=== Starting External Services Connectivity Check ===");
        Instant startTime = Instant.now();

        List<ExternalServiceInfo> services = getExternalServices();

        if (services.isEmpty()) {
            logger.info("No external services configured for connectivity check");
            return;
        }

        for (ExternalServiceInfo service : services) {
            HealthCheckResult result = validateExternalService(service);
            healthCheckResults.put(service.getName(), result);

            if (service.isRequired()) {
                assertTrue(result.isHealthy(),
                    String.format("Required external service '%s' connectivity failed: %s",
                        service.getName(), result.getMessage()));
            } else {
                if (!result.isHealthy()) {
                    logger.warn("Optional external service '{}' connectivity failed: {}",
                        service.getName(), result.getMessage());
                }
            }
        }

        Duration duration = Duration.between(startTime, Instant.now());
        logger.info("=== External Services Connectivity Check Completed in {}ms ===", duration.toMillis());
        logHealthCheckSummary();
    }

    /**
     * Test end-to-end service functionality
     */
    @Test
    @DisplayName("End-to-End Service Functionality Check")
    void testEndToEndFunctionality() {
        logger.info("=== Starting End-to-End Functionality Check ===");
        Instant startTime = Instant.now();

        try {
            performEndToEndTest();
            logger.info("End-to-end functionality check passed");
        } catch (Exception e) {
            logger.error("End-to-end functionality check failed", e);
            fail("End-to-end functionality test failed: " + e.getMessage());
        }

        Duration duration = Duration.between(startTime, Instant.now());
        logger.info("=== End-to-End Functionality Check Completed in {}ms ===", duration.toMillis());
    }

    /**
     * Validate a Testcontainer
     */
    private HealthCheckResult validateContainer(ContainerInfo container) {
        Instant startTime = Instant.now();

        try {
            logger.info("Checking container: {} ({})", container.getName(), container.getType());

            if (!container.isRunning()) {
                return HealthCheckResult.unhealthy(
                    container.getName(),
                    "Container is not running",
                    Duration.between(startTime, Instant.now())
                );
            }

            if (!container.isHealthy()) {
                return HealthCheckResult.unhealthy(
                    container.getName(),
                    "Container health check failed",
                    Duration.between(startTime, Instant.now())
                );
            }

            String diagnostics = String.format(
                "Host: %s, Port: %s, Image: %s",
                container.getHost(),
                container.getPort(),
                container.getImage()
            );

            logger.info("Container '{}' is healthy: {}", container.getName(), diagnostics);

            return HealthCheckResult.healthy(
                container.getName(),
                diagnostics,
                Duration.between(startTime, Instant.now())
            );

        } catch (Exception e) {
            logger.error("Error checking container '{}'", container.getName(), e);
            return HealthCheckResult.unhealthy(
                container.getName(),
                "Exception: " + e.getMessage(),
                Duration.between(startTime, Instant.now())
            );
        }
    }

    /**
     * Validate an external service
     */
    private HealthCheckResult validateExternalService(ExternalServiceInfo service) {
        Instant startTime = Instant.now();

        try {
            logger.info("Checking external service: {} ({})", service.getName(), service.getUrl());

            boolean isConnected = service.testConnection();

            if (!isConnected) {
                return HealthCheckResult.unhealthy(
                    service.getName(),
                    "Failed to connect to external service",
                    Duration.between(startTime, Instant.now())
                );
            }

            String diagnostics = String.format(
                "URL: %s, Type: %s",
                service.getUrl(),
                service.getType()
            );

            logger.info("External service '{}' is accessible: {}", service.getName(), diagnostics);

            return HealthCheckResult.healthy(
                service.getName(),
                diagnostics,
                Duration.between(startTime, Instant.now())
            );

        } catch (Exception e) {
            logger.error("Error checking external service '{}'", service.getName(), e);
            return HealthCheckResult.unhealthy(
                service.getName(),
                "Exception: " + e.getMessage(),
                Duration.between(startTime, Instant.now())
            );
        }
    }

    /**
     * Log summary of all health checks
     */
    private void logHealthCheckSummary() {
        logger.info("=== Health Check Summary ===");

        long healthyCount = healthCheckResults.values().stream()
            .filter(HealthCheckResult::isHealthy)
            .count();

        long unhealthyCount = healthCheckResults.values().stream()
            .filter(r -> !r.isHealthy())
            .count();

        logger.info("Total Checks: {}", healthCheckResults.size());
        logger.info("Healthy: {}", healthyCount);
        logger.info("Unhealthy: {}", unhealthyCount);

        healthCheckResults.forEach((name, result) -> {
            logger.info("  - {}: {} ({}ms)",
                name,
                result.isHealthy() ? "HEALTHY" : "UNHEALTHY",
                result.getResponseTime().toMillis()
            );
        });

        logger.info("============================");
    }

    /**
     * Get all health check results for reporting
     */
    protected Map<String, HealthCheckResult> getHealthCheckResults() {
        return Collections.unmodifiableMap(healthCheckResults);
    }

    /**
     * Abstract methods to be implemented by specific smoke tests
     */

    /**
     * Return list of Testcontainers to validate
     */
    protected abstract List<ContainerInfo> getTestcontainers();

    /**
     * Return list of external services to validate
     * Return empty list if no external services
     */
    protected abstract List<ExternalServiceInfo> getExternalServices();

    /**
     * Perform end-to-end test of application functionality
     */
    protected abstract void performEndToEndTest() throws Exception;

    /**
     * Container information holder
     */
    public static class ContainerInfo {
        private final String name;
        private final String type;
        private final String host;
        private final Integer port;
        private final String image;
        private final boolean running;
        private final boolean healthy;

        public ContainerInfo(String name, String type, String host, Integer port,
                           String image, boolean running, boolean healthy) {
            this.name = name;
            this.type = type;
            this.host = host;
            this.port = port;
            this.image = image;
            this.running = running;
            this.healthy = healthy;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getHost() { return host; }
        public Integer getPort() { return port; }
        public String getImage() { return image; }
        public boolean isRunning() { return running; }
        public boolean isHealthy() { return healthy; }
    }

    /**
     * External service information holder
     */
    public static class ExternalServiceInfo {
        private final String name;
        private final String type;
        private final String url;
        private final boolean required;
        private final ConnectionTester connectionTester;

        public ExternalServiceInfo(String name, String type, String url,
                                 boolean required, ConnectionTester connectionTester) {
            this.name = name;
            this.type = type;
            this.url = url;
            this.required = required;
            this.connectionTester = connectionTester;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getUrl() { return url; }
        public boolean isRequired() { return required; }

        public boolean testConnection() {
            return connectionTester.test();
        }

        @FunctionalInterface
        public interface ConnectionTester {
            boolean test();
        }
    }

    /**
     * Health check result holder
     */
    public static class HealthCheckResult {
        private final String serviceName;
        private final boolean healthy;
        private final String message;
        private final Duration responseTime;
        private final Instant timestamp;

        private HealthCheckResult(String serviceName, boolean healthy,
                                String message, Duration responseTime) {
            this.serviceName = serviceName;
            this.healthy = healthy;
            this.message = message;
            this.responseTime = responseTime;
            this.timestamp = Instant.now();
        }

        public static HealthCheckResult healthy(String serviceName, String message, Duration responseTime) {
            return new HealthCheckResult(serviceName, true, message, responseTime);
        }

        public static HealthCheckResult unhealthy(String serviceName, String message, Duration responseTime) {
            return new HealthCheckResult(serviceName, false, message, responseTime);
        }

        public String getServiceName() { return serviceName; }
        public boolean isHealthy() { return healthy; }
        public String getMessage() { return message; }
        public Duration getResponseTime() { return responseTime; }
        public Instant getTimestamp() { return timestamp; }
    }
}
