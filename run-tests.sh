#!/bin/bash

# Script to run Testcontainers tests with proper Docker configuration

echo "Testcontainers Test Runner"
echo "=========================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker is not running or not accessible"
    echo ""
    echo "Please ensure:"
    echo "1. Docker Desktop is running"
    echo "2. If on Windows, run this script from Git Bash or WSL2"
    echo ""
    exit 1
fi

echo "✓ Docker is accessible"
docker version --format '{{.Server.Version}}'
echo ""

# Set Docker host if not already set
if [ -z "$DOCKER_HOST" ]; then
    if [ -f "/var/run/docker.sock" ]; then
        export DOCKER_HOST=unix:///var/run/docker.sock
        echo "✓ Using Docker socket: unix:///var/run/docker.sock"
    else
        echo "ℹ Using default Docker configuration"
    fi
fi

echo ""
echo "Running Maven tests..."
echo ""

# Run tests with proper configuration
mvn clean test -Dspring.profiles.active=test

echo ""
echo "Tests completed!"
