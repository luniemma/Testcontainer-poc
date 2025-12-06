#!/bin/bash

# Script to setup Cassandra schema for local development
# Usage: ./setup-cassandra.sh

echo "Setting up Cassandra schema..."

# Wait for Cassandra to be ready
echo "Waiting for Cassandra to start..."
sleep 30

# Execute the schema file
docker exec -i testcontainers-cassandra cqlsh -f - < cassandra-schema.cql

if [ $? -eq 0 ]; then
    echo "Cassandra schema setup completed successfully!"
else
    echo "Error setting up Cassandra schema"
    exit 1
fi
