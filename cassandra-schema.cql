-- Cassandra Schema Setup Script
-- Run this after Cassandra starts if running locally

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS test_keyspace
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

-- Use the keyspace
USE test_keyspace;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT
);

-- Create index on email for faster lookups
CREATE INDEX IF NOT EXISTS users_email_idx ON users (email);

-- Insert sample data (optional)
INSERT INTO users (id, name, email) VALUES (uuid(), 'Alice Smith', 'alice@example.com');
INSERT INTO users (id, name, email) VALUES (uuid(), 'Bob Johnson', 'bob@example.com');
INSERT INTO users (id, name, email) VALUES (uuid(), 'Charlie Brown', 'charlie@example.com');

-- Verify data
SELECT * FROM users;
