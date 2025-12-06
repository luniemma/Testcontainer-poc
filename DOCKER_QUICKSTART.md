# Docker Quick Reference

Quick commands for building and deploying the Testcontainers Demo application.

## Build Commands

```bash
# Build Docker image locally
docker build -t testcontainers-demo:latest .

# Build for multiple platforms
docker buildx build --platform linux/amd64,linux/arm64 -t testcontainers-demo:latest .

# Build with version tag
docker build -t testcontainers-demo:1.0.0 .

# Build and tag for registry
docker build -t ghcr.io/your-org/testcontainers-demo:latest .
```

## Run Commands

```bash
# Run container locally
docker run -p 8080:8080 testcontainers-demo:latest

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATA_REDIS_HOST=redis \
  testcontainers-demo:latest

# Run in background
docker run -d -p 8080:8080 --name app testcontainers-demo:latest
```

## Docker Compose Commands

```bash
# Development environment
docker-compose up -d
docker-compose logs -f
docker-compose down

# Production environment
docker-compose -f docker-compose.prod.yml up -d
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs -f app
docker-compose -f docker-compose.prod.yml down

# Rebuild and restart
docker-compose -f docker-compose.prod.yml up -d --build
```

## Maven Commands

```bash
# Build JAR
mvn clean package

# Skip tests
mvn clean package -DskipTests

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report

# Run security scan
mvn dependency-check:check

# Run SonarCloud analysis
mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN
```

## Health & Monitoring

```bash
# Check health
curl http://localhost:8080/actuator/health

# Liveness probe
curl http://localhost:8080/actuator/health/liveness

# Readiness probe
curl http://localhost:8080/actuator/health/readiness

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

## Container Management

```bash
# View logs
docker logs testcontainers-app
docker logs -f testcontainers-app --tail 100

# Execute command in container
docker exec -it testcontainers-app sh

# Check container health
docker inspect testcontainers-app | grep -A 10 Health

# View container stats
docker stats testcontainers-app

# Restart container
docker restart testcontainers-app

# Stop container
docker stop testcontainers-app

# Remove container
docker rm testcontainers-app
```

## Service Access

```bash
# Redis CLI
docker exec -it testcontainers-redis redis-cli

# Cassandra CQL
docker exec -it testcontainers-cassandra cqlsh

# Kafka topics
docker exec -it testcontainers-kafka kafka-topics \
  --list --bootstrap-server localhost:9092

# Kafka consumer
docker exec -it testcontainers-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic your-topic \
  --from-beginning
```

## Image Management

```bash
# List images
docker images | grep testcontainers-demo

# Remove image
docker rmi testcontainers-demo:latest

# Tag image
docker tag testcontainers-demo:latest testcontainers-demo:1.0.0

# Push to registry
docker push ghcr.io/your-org/testcontainers-demo:latest

# Pull from registry
docker pull ghcr.io/your-org/testcontainers-demo:latest

# Inspect image
docker inspect testcontainers-demo:latest
```

## Cleanup

```bash
# Remove stopped containers
docker-compose -f docker-compose.prod.yml down

# Remove volumes (WARNING: deletes data)
docker-compose -f docker-compose.prod.yml down -v

# Remove all containers
docker container prune

# Remove all images
docker image prune -a

# Remove all volumes
docker volume prune

# Full cleanup
docker system prune -a --volumes
```

## Debugging

```bash
# Debug build issues
docker build --no-cache --progress=plain -t testcontainers-demo:debug .

# Run with debug logging
docker run -p 8080:8080 \
  -e LOGGING_LEVEL_ROOT=DEBUG \
  testcontainers-demo:latest

# Connect to running container
docker exec -it testcontainers-app sh

# Copy files from container
docker cp testcontainers-app:/app/logs/application.log ./local-logs/

# View container processes
docker top testcontainers-app
```

## GitHub Actions

```bash
# Trigger workflow manually
gh workflow run ci-cd.yml

# View workflow runs
gh run list

# View workflow logs
gh run view <run-id>

# Download artifacts
gh run download <run-id>

# Create release tag
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

## Troubleshooting

```bash
# Container won't start
docker logs testcontainers-app
docker inspect testcontainers-app

# Network issues
docker network ls
docker network inspect testcontainers_app-network

# Port conflicts
netstat -tulpn | grep 8080
lsof -i :8080

# Permission issues
docker run --rm testcontainers-demo whoami
ls -la /var/run/docker.sock

# Memory issues
docker stats
free -h
```

## Security Scanning

```bash
# Scan with Trivy
trivy image testcontainers-demo:latest

# High and critical only
trivy image --severity HIGH,CRITICAL testcontainers-demo:latest

# Generate report
trivy image --format json --output report.json testcontainers-demo:latest

# OWASP scan
mvn dependency-check:check

# Update vulnerability database
mvn dependency-check:update-only
```

## Performance Testing

```bash
# Load test with curl
for i in {1..100}; do
  curl http://localhost:8080/actuator/health
done

# Apache Bench
ab -n 1000 -c 10 http://localhost:8080/actuator/health

# Check resource usage
docker stats --no-stream testcontainers-app
```

## URLs

- Application: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- Redis: localhost:6379
- Kafka: localhost:9092
- Cassandra: localhost:9042
