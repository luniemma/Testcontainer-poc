package com.example.testcontainers;

import com.example.testcontainers.config.TestcontainersConfig;
import com.example.testcontainers.service.CassandraService;
import com.example.testcontainers.service.KafkaProducerService;
import com.example.testcontainers.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
@DisplayName("Smoke Test - Verify all services are operational")
class SmokeTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private CassandraService cassandraService;

    @Test
    @DisplayName("Redis should be accessible and functional")
    void testRedisIsWorking() {
        String key = "smoke-test-redis";
        String value = "redis-is-working";

        redisService.set(key, value);
        String result = redisService.get(key);

        assertNotNull(result, "Redis should return a value");
        assertEquals(value, result, "Redis should store and retrieve values correctly");
    }

    @Test
    @DisplayName("Kafka should be accessible and functional")
    void testKafkaIsWorking() {
        String message = "smoke-test-kafka-message";

        assertDoesNotThrow(() -> {
            kafkaProducerService.sendMessage(message);
        }, "Kafka should accept messages without throwing exceptions");
    }

    @Test
    @DisplayName("Cassandra should be accessible and functional")
    void testCassandraIsWorking() {
        assertDoesNotThrow(() -> {
            cassandraService.getAllUsers();
        }, "Cassandra should be queryable without throwing exceptions");
    }

    @Test
    @DisplayName("All services should be operational together")
    void testAllServicesWorking() {
        assertAll("All infrastructure services should be operational",
            () -> assertNotNull(redisService, "RedisService should be injected"),
            () -> assertNotNull(kafkaProducerService, "KafkaProducerService should be injected"),
            () -> assertNotNull(cassandraService, "CassandraService should be injected"),
            () -> {
                String testKey = "smoke-all-services";
                String testValue = "working";
                redisService.set(testKey, testValue);
                assertEquals(testValue, redisService.get(testKey), "Redis should work in combined test");
            }
        );
    }
}
