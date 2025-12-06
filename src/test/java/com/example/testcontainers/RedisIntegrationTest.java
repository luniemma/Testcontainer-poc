package com.example.testcontainers;

import com.example.testcontainers.config.TestcontainersConfig;
import com.example.testcontainers.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
class RedisIntegrationTest {

    @Autowired
    private RedisService redisService;

    @Test
    void testRedisSetAndGet() {
        String key = "integration-test-key";
        String value = "integration-test-value";

        redisService.set(key, value);
        String retrievedValue = redisService.get(key);

        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue);
    }

    @Test
    void testRedisGetNonExistentKey() {
        String key = "non-existent-key";
        String retrievedValue = redisService.get(key);

        assertNull(retrievedValue);
    }

    @Test
    void testRedisOverwriteValue() {
        String key = "overwrite-test-key";
        String value1 = "first-value";
        String value2 = "second-value";

        redisService.set(key, value1);
        String retrieved1 = redisService.get(key);
        assertEquals(value1, retrieved1);

        redisService.set(key, value2);
        String retrieved2 = redisService.get(key);
        assertEquals(value2, retrieved2);
    }
}
