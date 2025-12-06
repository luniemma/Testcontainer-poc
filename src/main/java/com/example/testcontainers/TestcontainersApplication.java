package com.example.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class TestcontainersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcontainersApplication.class, args);
    }
}
