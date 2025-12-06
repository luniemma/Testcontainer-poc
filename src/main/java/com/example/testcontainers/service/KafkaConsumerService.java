package com.example.testcontainers.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaConsumerService {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clearMessages() {
        messages.clear();
    }
}
