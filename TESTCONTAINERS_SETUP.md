# Testcontainers Setup Guide

## Prerequisites

1. **Docker Desktop** must be installed and running
2. **Java 17** or higher
3. **Maven 3.6+**

## Running Tests on Windows

### Option 1: Using WSL2 (Recommended)

This is the most reliable way to run Testcontainers on Windows:

```bash
# Open WSL2 terminal
wsl

# Navigate to project directory
cd /mnt/c/Users/chiom/OneDrive/Desktop/testcontainers/Testcontainer-poc

# Run the test script
./run-tests.sh
```

### Option 2: Using Git Bash

```bash
# Open Git Bash
./run-tests.sh
```

### Option 3: Using Windows Command Prompt

```cmd
# Open Command Prompt as Administrator
run-tests.bat
```

### Option 4: Manual Maven Command

If the scripts don't work, try running Maven directly:

**PowerShell:**
```powershell
$env:DOCKER_HOST="npipe:////./pipe/docker_engine"
mvn clean test
```

**Command Prompt:**
```cmd
set DOCKER_HOST=npipe:////./pipe/docker_engine
mvn clean test
```

## Troubleshooting

### Error: "Could not find a valid Docker environment"

**Solution 1: Enable Docker Desktop Integration**

1. Open Docker Desktop
2. Go to Settings → Resources → WSL Integration
3. Enable integration with your WSL2 distributions
4. Click "Apply & Restart"
5. Run tests from WSL2 using `./run-tests.sh`

**Solution 2: Expose Docker Daemon (Not Recommended for Production)**

1. Open Docker Desktop
2. Go to Settings → General
3. Enable "Expose daemon on tcp://localhost:2375 without TLS"
4. Click "Apply & Restart"
5. Set environment variable: `set DOCKER_HOST=tcp://localhost:2375`
6. Run tests

**Solution 3: Use WSL2 Docker Socket**

If running from WSL2, ensure Docker Desktop exposes the socket:

```bash
# Check if Docker socket is accessible
ls -la /var/run/docker.sock

# If accessible, run tests
export DOCKER_HOST=unix:///var/run/docker.sock
mvn clean test
```

### Error: "Connection refused: localhost/127.0.0.1:9042"

This error means Spring Boot is trying to connect to Cassandra before Testcontainers starts it. This should be fixed by the test configuration, but if you still see it:

1. Ensure you're running with the test profile: `-Dspring.profiles.active=test`
2. Check that `application-test.yml` has the autoconfiguration exclusions
3. Verify the test class extends `BaseIntegrationTest`

### Tests are slow

Testcontainers can be slow on first run. To speed up subsequent runs:

1. Container reuse is enabled in `.testcontainers.properties`
2. On first run, containers will be downloaded (one-time cost)
3. Subsequent runs will reuse containers if possible

### Docker Desktop not starting

1. Ensure WSL2 is installed and updated
2. Restart Docker Desktop
3. Restart your computer
4. Check Docker Desktop logs: Settings → Troubleshoot → View Logs

## Running Individual Tests

```bash
# Run only Redis tests
mvn test -Dtest=RedisIntegrationTest

# Run only Kafka tests
mvn test -Dtest=KafkaIntegrationTest

# Run only Cassandra tests
mvn test -Dtest=CassandraIntegrationTest

# Run smoke tests
mvn test -Dtest=SmokeTest
```

## IDE Configuration

### IntelliJ IDEA

1. Go to Run → Edit Configurations
2. Select your test configuration
3. Add environment variable:
   - Name: `DOCKER_HOST`
   - Value: `npipe:////./pipe/docker_engine` (Windows) or `unix:///var/run/docker.sock` (WSL2)
4. Apply and run tests

### Eclipse

1. Right-click test file → Run As → Run Configurations
2. Select Environment tab
3. Add variable:
   - Name: `DOCKER_HOST`
   - Value: `npipe:////./pipe/docker_engine`
4. Apply and run

### VS Code

Add to `.vscode/settings.json`:

```json
{
  "java.test.config": {
    "env": {
      "DOCKER_HOST": "npipe:////./pipe/docker_engine"
    }
  }
}
```

## Verifying Docker Connectivity

Before running tests, verify Docker is accessible:

```bash
# Check Docker is running
docker version

# Check Docker info
docker info

# Try running a test container
docker run --rm hello-world
```

If these commands work, Testcontainers should also work.

## Additional Resources

- [Testcontainers Windows Documentation](https://www.testcontainers.org/supported_docker_environment/#windows)
- [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)
- [WSL2 Setup](https://docs.microsoft.com/en-us/windows/wsl/install)
