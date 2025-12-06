package com.example.testcontainers.controller;

import com.example.testcontainers.service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final RedisService redisService;

    public CacheController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testCache() {
        String key = "test-key";
        String value = "Hello from Redis!";

        redisService.set(key, value);
        String retrievedValue = redisService.get(key);

        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("setValue", value);
        response.put("retrievedValue", retrievedValue);
        response.put("success", String.valueOf(value.equals(retrievedValue)));

        return ResponseEntity.ok(response);
    }
}
