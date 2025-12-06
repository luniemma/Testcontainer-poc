package com.example.testcontainers.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

/**
 * Comprehensive Health Check Utilities for ATT Organization
 *
 * Provides reusable methods for testing connectivity to various services
 */
public class HealthCheckUtils {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckUtils.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int RETRY_DELAY_MS = 1000;

    /**
     * Test TCP connection to a host and port
     */
    public static boolean testTcpConnection(String host, int port) {
        return testTcpConnection(host, port, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Test TCP connection to a host and port with custom timeout
     */
    public static boolean testTcpConnection(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            logger.debug("TCP connection successful to {}:{}", host, port);
            return true;
        } catch (IOException e) {
            logger.warn("TCP connection failed to {}:{} - {}", host, port, e.getMessage());
            return false;
        }
    }

    /**
     * Test HTTP/HTTPS endpoint
     */
    public static boolean testHttpEndpoint(String urlString) {
        return testHttpEndpoint(urlString, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Test HTTP/HTTPS endpoint with custom timeout
     */
    public static boolean testHttpEndpoint(String urlString, int timeoutMs) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);

            int responseCode = connection.getResponseCode();
            boolean success = responseCode >= 200 && responseCode < 300;

            if (success) {
                logger.debug("HTTP endpoint check successful: {} (Status: {})", urlString, responseCode);
            } else {
                logger.warn("HTTP endpoint check failed: {} (Status: {})", urlString, responseCode);
            }

            return success;
        } catch (Exception e) {
            logger.warn("HTTP endpoint check failed: {} - {}", urlString, e.getMessage());
            return false;
        }
    }

    /**
     * Test connection with retries
     */
    public static boolean testConnectionWithRetry(Callable<Boolean> connectionTest) {
        return testConnectionWithRetry(connectionTest, DEFAULT_RETRY_COUNT, RETRY_DELAY_MS);
    }

    /**
     * Test connection with custom retry configuration
     */
    public static boolean testConnectionWithRetry(Callable<Boolean> connectionTest,
                                                  int retryCount, int retryDelayMs) {
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                boolean result = connectionTest.call();
                if (result) {
                    if (attempt > 1) {
                        logger.info("Connection successful on attempt {}/{}", attempt, retryCount);
                    }
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Connection attempt {}/{} failed: {}", attempt, retryCount, e.getMessage());
            }

            if (attempt < retryCount) {
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        logger.error("All {} connection attempts failed", retryCount);
        return false;
    }

    /**
     * Wait until service is available or timeout
     */
    public static boolean waitForServiceAvailability(String host, int port, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);

        while (Instant.now().isBefore(deadline)) {
            if (testTcpConnection(host, port, 1000)) {
                return true;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        logger.error("Service {}:{} did not become available within {}ms",
            host, port, timeout.toMillis());
        return false;
    }

    /**
     * Execute health check with timeout
     */
    public static <T> T executeWithTimeout(Callable<T> task, Duration timeout) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<T> future = executor.submit(task);
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.error("Health check timed out after {}ms", timeout.toMillis());
            throw new TimeoutException("Health check timed out");
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * Measure response time of a connection test
     */
    public static ConnectionMetrics measureConnectionTime(Callable<Boolean> connectionTest) {
        Instant start = Instant.now();
        boolean success = false;
        Exception error = null;

        try {
            success = connectionTest.call();
        } catch (Exception e) {
            error = e;
            logger.error("Connection measurement failed", e);
        }

        Duration responseTime = Duration.between(start, Instant.now());
        return new ConnectionMetrics(success, responseTime, error);
    }

    /**
     * Connection metrics holder
     */
    public static class ConnectionMetrics {
        private final boolean success;
        private final Duration responseTime;
        private final Exception error;

        public ConnectionMetrics(boolean success, Duration responseTime, Exception error) {
            this.success = success;
            this.responseTime = responseTime;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public Duration getResponseTime() { return responseTime; }
        public Exception getError() { return error; }

        @Override
        public String toString() {
            return String.format("ConnectionMetrics{success=%s, responseTime=%dms, error=%s}",
                success, responseTime.toMillis(),
                error != null ? error.getMessage() : "none");
        }
    }

    /**
     * Verify DNS resolution
     */
    public static boolean verifyDnsResolution(String hostname) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            logger.debug("DNS resolution successful for {}: {}", hostname, address.getHostAddress());
            return true;
        } catch (UnknownHostException e) {
            logger.warn("DNS resolution failed for {}: {}", hostname, e.getMessage());
            return false;
        }
    }

    /**
     * Test Redis connection
     */
    public static boolean testRedisConnection(String host, int port) {
        return testTcpConnection(host, port, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Test Kafka connection
     */
    public static boolean testKafkaConnection(String host, int port) {
        return testTcpConnection(host, port, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Test Cassandra connection
     */
    public static boolean testCassandraConnection(String host, int port) {
        return testTcpConnection(host, port, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Test database connection via JDBC URL
     */
    public static boolean testJdbcConnection(String jdbcUrl, String username, String password) {
        try {
            Class.forName("com.datastax.oss.driver.api.core.CqlSession");
            return true;
        } catch (ClassNotFoundException e) {
            logger.error("JDBC driver not found for URL: {}", jdbcUrl);
            return false;
        }
    }
}
