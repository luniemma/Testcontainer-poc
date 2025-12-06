package com.example.testcontainers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.testcontainers.config.BaseIntegrationTest;
import com.example.testcontainers.model.User;
import com.example.testcontainers.service.CassandraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CassandraIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CassandraService cassandraService;

    @Autowired
    private CqlSession cqlSession;

    @BeforeEach
    void setUp() {
        cqlSession.execute("CREATE KEYSPACE IF NOT EXISTS test_keyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};");
        cqlSession.execute("USE test_keyspace;");
        cqlSession.execute("CREATE TABLE IF NOT EXISTS users (id UUID PRIMARY KEY, name TEXT, email TEXT);");
        cqlSession.execute("TRUNCATE users;");
    }

    @Test
    void testCassandraSaveAndRetrieveUser() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe", "john@example.com");

        User savedUser = cassandraService.saveUser(user);

        assertNotNull(savedUser);
        assertEquals(userId, savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());

        User retrievedUser = cassandraService.getUserById(userId);
        assertNotNull(retrievedUser);
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void testCassandraGetAllUsers() {
        User user1 = new User(UUID.randomUUID(), "Alice", "alice@example.com");
        User user2 = new User(UUID.randomUUID(), "Bob", "bob@example.com");

        cassandraService.saveUser(user1);
        cassandraService.saveUser(user2);

        List<User> users = cassandraService.getAllUsers();

        assertTrue(users.size() >= 2);
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Alice")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Bob")));
    }

    @Test
    void testCassandraGetNonExistentUser() {
        UUID randomId = UUID.randomUUID();
        User user = cassandraService.getUserById(randomId);

        assertNull(user);
    }
}
