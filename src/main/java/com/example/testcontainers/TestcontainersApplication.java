package com.example.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableCassandraRepositories
public class TestcontainersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcontainersApplication.class, args);
    }
}
