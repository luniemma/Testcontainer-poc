package com.example.testcontainers.config;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    protected static GenericContainer<?> redisContainer;
    protected static KafkaContainer kafkaContainer;
    protected static CassandraContainer<?> cassandraContainer;

    @BeforeAll
    static void startContainers() {
        if (redisContainer == null) {
            redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379)
                    .withReuse(true);
            redisContainer.start();
        }

        if (kafkaContainer == null) {
            kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
                    .withReuse(true);
            kafkaContainer.start();
        }

        if (cassandraContainer == null) {
            cassandraContainer = new CassandraContainer<>(DockerImageName.parse("cassandra:4.1"))
                    .withReuse(true);
            cassandraContainer.start();
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (redisContainer != null && redisContainer.isRunning()) {
            registry.add("spring.data.redis.host", redisContainer::getHost);
            registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        }

        if (kafkaContainer != null && kafkaContainer.isRunning()) {
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        }

        if (cassandraContainer != null && cassandraContainer.isRunning()) {
            registry.add("spring.cassandra.contact-points", cassandraContainer::getHost);
            registry.add("spring.cassandra.port", cassandraContainer::getFirstMappedPort);
            registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
            registry.add("spring.cassandra.keyspace-name", () -> "test_keyspace");
            registry.add("spring.cassandra.schema-action", () -> "create_if_not_exists");
        }
    }
}
