package com.example.testcontainers.service;

import com.example.testcontainers.model.User;
import com.example.testcontainers.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CassandraService {

    private final UserRepository userRepository;

    public CassandraService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
}
