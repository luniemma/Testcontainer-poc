package com.example.testcontainers;

import com.example.testcontainers.config.BaseIntegrationTest;
import com.example.testcontainers.framework.SmokeTestFramework;
import com.example.testcontainers.service.CassandraService;
import com.example.testcontainers.service.KafkaProducerService;
import com.example.testcontainers.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Smoke Test using the reusable ATT Smoke Test Framework
 *
 * This test validates:
 * 1. All Testcontainers are running and healthy
 * 2. External services are accessible (if configured)
 * 3. End-to-end application functionality
 */
@DisplayName("Enhanced Smoke Test - Comprehensive Infrastructure Validation")
class EnhancedSmokeTest extends BaseIntegrationTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private CassandraService cassandraService;

    @Value("${external.redis.url:#{null}}")
    private String externalRedisUrl;

    @Value("${external.kafka.url:#{null}}")
    private String externalKafkaUrl;

    @Value("${external.cassandra.url:#{null}}")
    private String externalCassandraUrl;

    @Value("${external.api.health-check-url:#{null}}")
    private String externalApiHealthCheckUrl;

    @Test
    @DisplayName("Testcontainers Health Check")
    void testTestcontainersHealth() {
        SmokeTestFramework framework = createFramework();
        List<SmokeTestFramework.ContainerInfo> containers = getTestcontainersInfo();

        for (SmokeTestFramework.ContainerInfo container : containers) {
            assertTrue(container.isRunning(),
                String.format("Container '%s' should be running", container.getName()));
            assertTrue(container.isHealthy(),
                String.format("Container '%s' should be healthy", container.getName()));
        }
    }

    @Test
    @DisplayName("External Services Connectivity Check")
    void testExternalServicesConnectivity() {
        List<SmokeTestFramework.ExternalServiceInfo> services = getExternalServicesInfo();

        for (SmokeTestFramework.ExternalServiceInfo service : services) {
            boolean connected = service.testConnection();
            if (service.isRequired()) {
                assertTrue(connected,
                    String.format("Required external service '%s' should be accessible", service.getName()));
            }
        }
    }

    @Test
    @DisplayName("End-to-End Service Functionality Check")
    void testEndToEndFunctionality() throws Exception {
        performEndToEndTest();
    }

    /**
     * Define all Testcontainers to validate
     */
    protected List<SmokeTestFramework.ContainerInfo> getTestcontainersInfo() {
        List<SmokeTestFramework.ContainerInfo> containers = new ArrayList<>();

        if (redisContainer != null) {
            containers.add(new SmokeTestFramework.ContainerInfo(
                "Redis",
                "Cache",
                redisContainer.getHost(),
                redisContainer.getMappedPort(6379),
                redisContainer.getDockerImageName(),
                redisContainer.isRunning(),
                redisContainer.isRunning()
            ));
        }

        if (kafkaContainer != null) {
            containers.add(new SmokeTestFramework.ContainerInfo(
                "Kafka",
                "Messaging",
                kafkaContainer.getHost(),
                kafkaContainer.getFirstMappedPort(),
                kafkaContainer.getDockerImageName(),
                kafkaContainer.isRunning(),
                kafkaContainer.isRunning()
            ));
        }

        if (cassandraContainer != null) {
            containers.add(new SmokeTestFramework.ContainerInfo(
                "Cassandra",
                "Database",
                cassandraContainer.getHost(),
                cassandraContainer.getFirstMappedPort(),
                cassandraContainer.getDockerImageName(),
                cassandraContainer.isRunning(),
                cassandraContainer.isRunning()
            ));
        }

        return containers;
    }

    /**
     * Define all external services to validate
     * Configure via application-test.yml or environment variables
     */
    protected List<SmokeTestFramework.ExternalServiceInfo> getExternalServicesInfo() {
        List<SmokeTestFramework.ExternalServiceInfo> services = new ArrayList<>();

        if (externalRedisUrl != null && !externalRedisUrl.isEmpty()) {
            services.add(new SmokeTestFramework.ExternalServiceInfo(
                "External Redis",
                "Cache",
                externalRedisUrl,
                false,
                this::testExternalRedisConnection
            ));
        }

        if (externalKafkaUrl != null && !externalKafkaUrl.isEmpty()) {
            services.add(new SmokeTestFramework.ExternalServiceInfo(
                "External Kafka",
                "Messaging",
                externalKafkaUrl,
                false,
                this::testExternalKafkaConnection
            ));
        }

        if (externalCassandraUrl != null && !externalCassandraUrl.isEmpty()) {
            services.add(new SmokeTestFramework.ExternalServiceInfo(
                "External Cassandra",
                "Database",
                externalCassandraUrl,
                false,
                this::testExternalCassandraConnection
            ));
        }

        if (externalApiHealthCheckUrl != null && !externalApiHealthCheckUrl.isEmpty()) {
            services.add(new SmokeTestFramework.ExternalServiceInfo(
                "External API",
                "REST API",
                externalApiHealthCheckUrl,
                true,
                this::testExternalApiConnection
            ));
        }

        return services;
    }

    /**
     * Perform comprehensive end-to-end test
     */
    protected void performEndToEndTest() throws Exception {
        String testKey = "e2e-smoke-test";
        String testValue = "end-to-end-working";

        redisService.set(testKey, testValue);
        String redisResult = redisService.get(testKey);

        if (!testValue.equals(redisResult)) {
            throw new AssertionError("Redis end-to-end test failed");
        }

        kafkaProducerService.sendMessage("e2e-smoke-test-message");

        cassandraService.getAllUsers();
    }

    /**
     * Create smoke test framework instance
     */
    private SmokeTestFramework createFramework() {
        return new SmokeTestFramework() {
            @Override
            protected List<ContainerInfo> getTestcontainers() {
                return getTestcontainersInfo();
            }

            @Override
            protected List<ExternalServiceInfo> getExternalServices() {
                return getExternalServicesInfo();
            }

            @Override
            protected void performEndToEndTest() throws Exception {
                EnhancedSmokeTest.this.performEndToEndTest();
            }
        };
    }

    /**
     * External service connection testers
     */

    private boolean testExternalRedisConnection() {
        try {
            String[] parts = externalRedisUrl.replace("redis://", "").split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 6379;

            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 5000);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testExternalKafkaConnection() {
        try {
            String[] parts = externalKafkaUrl.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9092;

            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 5000);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testExternalCassandraConnection() {
        try {
            String[] parts = externalCassandraUrl.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9042;

            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 5000);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean testExternalApiConnection() {
        try {
            URL url = new URL(externalApiHealthCheckUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
        } catch (Exception e) {
            return false;
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
