CREATE KEYSPACE IF NOT EXISTS test_keyspace
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE test_keyspace;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT
);
