package com.example.testcontainers.controller;

import com.example.testcontainers.service.KafkaConsumerService;
import com.example.testcontainers.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerService producerService;
    private final KafkaConsumerService consumerService;

    public KafkaController(KafkaProducerService producerService,
                          KafkaConsumerService consumerService) {
        this.producerService = producerService;
        this.consumerService = consumerService;
    }

    @PostMapping("/produce")
    public ResponseEntity<Map<String, String>> produceMessage(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "Default test message");
        producerService.sendMessage(message);

        Map<String, String> response = new HashMap<>();
        response.put("status", "Message sent to Kafka");
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages() {
        List<String> messages = consumerService.getMessages();

        Map<String, Object> response = new HashMap<>();
        response.put("count", messages.size());
        response.put("messages", messages);

        return ResponseEntity.ok(response);
    }
}
