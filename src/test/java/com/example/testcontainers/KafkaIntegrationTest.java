package com.example.testcontainers;

import com.example.testcontainers.config.BaseIntegrationTest;
import com.example.testcontainers.service.KafkaConsumerService;
import com.example.testcontainers.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class KafkaIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private KafkaConsumerService consumerService;

    @BeforeEach
    void setUp() {
        consumerService.clearMessages();
    }

    @Test
    void testKafkaProduceAndConsume() {
        String testMessage = "Test message from integration test";

        producerService.sendMessage(testMessage);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<String> messages = consumerService.getMessages();
                    assertFalse(messages.isEmpty());
                    assertTrue(messages.contains(testMessage));
                });
    }

    @Test
    void testKafkaMultipleMessages() {
        String message1 = "First message";
        String message2 = "Second message";
        String message3 = "Third message";

        producerService.sendMessage(message1);
        producerService.sendMessage(message2);
        producerService.sendMessage(message3);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<String> messages = consumerService.getMessages();
                    assertTrue(messages.size() >= 3);
                    assertTrue(messages.contains(message1));
                    assertTrue(messages.contains(message2));
                    assertTrue(messages.contains(message3));
                });
    }
}
