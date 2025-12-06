package com.example.testcontainers.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfig {

    private static final GenericContainer<?> redisContainer;
    private static final KafkaContainer kafkaContainer;
    private static final CassandraContainer<?> cassandraContainer;

    static {
        redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379)
                .withReuse(true);
        redisContainer.start();

        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
                .withReuse(true);
        kafkaContainer.start();

        cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
                .withReuse(true);
        cassandraContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        registry.add("spring.cassandra.contact-points", cassandraContainer::getHost);
        registry.add("spring.cassandra.port", cassandraContainer::getFirstMappedPort);
        registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
        registry.add("spring.cassandra.keyspace-name", () -> "test_keyspace");
        registry.add("spring.cassandra.schema-action", () -> "create_if_not_exists");
    }

    @Bean
    public GenericContainer<?> redisContainer() {
        return redisContainer;
    }

    @Bean
    public KafkaContainer kafkaContainer() {
        return kafkaContainer;
    }

    @Bean
    public CassandraContainer<?> cassandraContainer() {
        return cassandraContainer;
    }
}
