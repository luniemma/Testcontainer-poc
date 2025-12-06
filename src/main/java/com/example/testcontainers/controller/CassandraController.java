package com.example.testcontainers.controller;

import com.example.testcontainers.model.User;
import com.example.testcontainers.service.CassandraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cassandra")
public class CassandraController {

    private final CassandraService cassandraService;

    public CassandraController(CassandraService cassandraService) {
        this.cassandraService = cassandraService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = cassandraService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        User savedUser = cassandraService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        User user = cassandraService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}
