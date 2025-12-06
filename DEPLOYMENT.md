# Deployment Guide

This guide covers building, deploying, and operating the Testcontainers Demo application in production.

## Table of Contents

- [Docker Build](#docker-build)
- [Local Deployment](#local-deployment)
- [Production Deployment](#production-deployment)
- [CI/CD Pipeline](#cicd-pipeline)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

## Docker Build

### Building Locally

Build the Docker image:

```bash
docker build -t testcontainers-demo:latest .
```

Build for multiple platforms:

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t testcontainers-demo:latest .
```

### Build Arguments

The Dockerfile supports the following build arguments:

- `BUILD_DATE`: Build timestamp
- `VCS_REF`: Git commit SHA
- `VERSION`: Application version

Example:

```bash
docker build \
  --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
  --build-arg VCS_REF=$(git rev-parse --short HEAD) \
  --build-arg VERSION=1.0.0 \
  -t testcontainers-demo:1.0.0 .
```

## Local Deployment

### Quick Start

Start all services:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

View logs:

```bash
docker-compose -f docker-compose.prod.yml logs -f app
```

Stop all services:

```bash
docker-compose -f docker-compose.prod.yml down
```

### Environment Variables

Create a `.env` file for local deployment:

```env
# Redis
REDIS_PASSWORD=your-redis-password

# Grafana
GRAFANA_PASSWORD=your-grafana-password

# Spring profiles
SPRING_PROFILES_ACTIVE=prod
```

### Accessing Services

- Application: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

## Production Deployment

### Prerequisites

1. Docker and Docker Compose installed
2. Sufficient resources:
   - CPU: 4+ cores
   - RAM: 6GB+ available
   - Disk: 20GB+ available

### Resource Configuration

Edit `docker-compose.prod.yml` to adjust resource limits based on your environment:

```yaml
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 2G
    reservations:
      cpus: '1'
      memory: 1G
```

### High Availability Setup

For production, consider:

1. **Multiple App Instances**: Use Docker Swarm or Kubernetes
2. **Redis Cluster**: Replace single Redis with Redis Sentinel or Cluster
3. **Kafka Cluster**: Deploy multi-broker Kafka setup
4. **Cassandra Cluster**: Use 3+ node Cassandra cluster

### Kubernetes Deployment

Create Kubernetes manifests:

```bash
# Create namespace
kubectl create namespace testcontainers

# Deploy application
kubectl apply -f k8s/ -n testcontainers

# Check deployment status
kubectl get pods -n testcontainers
```

### Security Considerations

1. **Use secrets management**: Never commit passwords to version control
2. **Enable TLS**: Use SSL certificates for external endpoints
3. **Network policies**: Restrict pod-to-pod communication
4. **Image scanning**: Scan images for vulnerabilities (Trivy is integrated in CI)
5. **Non-root user**: Dockerfile runs as non-root user (UID 1001)

## CI/CD Pipeline

### GitHub Actions Workflow

The project includes a reusable workflow at `.github/workflows/ci-cd.yml`.

### Pipeline Stages

1. **Build and Test**
   - Compiles code
   - Runs unit and integration tests
   - Generates code coverage reports

2. **Security Scan**
   - OWASP Dependency Check
   - CodeQL analysis
   - Trivy container scanning

3. **Docker Build**
   - Multi-platform build (amd64/arm64)
   - Push to GitHub Container Registry
   - Generate SBOM (Software Bill of Materials)

4. **Deploy**
   - Staging: Auto-deploy on develop branch
   - Production: Deploy on version tags

### Triggering Builds

**Push to main/develop:**
```bash
git push origin main
```

**Create release:**
```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

### Required Secrets

Configure in GitHub Settings → Secrets:

- `DOCKER_USERNAME`: Docker registry username (optional, uses GitHub actor by default)
- `DOCKER_PASSWORD`: Docker registry password (optional, uses GITHUB_TOKEN by default)
- `SONAR_TOKEN`: SonarCloud token (optional)

### Using the Reusable Workflow

Create a custom workflow:

```yaml
name: Custom Build

on:
  push:
    branches: [main]

jobs:
  build:
    uses: ./.github/workflows/ci-cd.yml
    with:
      java-version: '17'
      docker-platforms: 'linux/amd64,linux/arm64'
      enable-security-scan: true
      run-tests: true
    secrets:
      DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
      DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

## Monitoring

### Health Checks

The application exposes health endpoints:

```bash
# Liveness probe
curl http://localhost:8080/actuator/health/liveness

# Readiness probe
curl http://localhost:8080/actuator/health/readiness

# Detailed health
curl http://localhost:8080/actuator/health
```

### Metrics

Access Prometheus metrics:

```bash
curl http://localhost:8080/actuator/prometheus
```

### Grafana Dashboards

Import pre-built dashboards:

1. Spring Boot 2.1 Statistics (ID: 10280)
2. JVM Dashboard (ID: 4701)
3. Kafka Dashboard (ID: 7589)

### Log Management

Application logs are stored in the `app-logs` volume:

```bash
docker exec testcontainers-app tail -f /app/logs/application.log
```

For production, integrate with:
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Splunk
- CloudWatch Logs

### Alerts

Configure Prometheus alerting rules in `prometheus.yml`:

```yaml
alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

rule_files:
  - 'alerts/*.yml'
```

## Troubleshooting

### Common Issues

**Container fails to start:**
```bash
# Check logs
docker logs testcontainers-app

# Check health
docker inspect testcontainers-app | jq '.[0].State.Health'
```

**Out of memory:**
```bash
# Increase memory limits in docker-compose.prod.yml
environment:
  JAVA_OPTS: -XX:MaxRAMPercentage=75.0
```

**Connection refused to dependencies:**
```bash
# Verify all services are healthy
docker-compose -f docker-compose.prod.yml ps

# Restart services
docker-compose -f docker-compose.prod.yml restart
```

### Performance Tuning

**JVM Settings:**
```bash
JAVA_OPTS: >
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:ParallelGCThreads=4
  -XX:ConcGCThreads=2
  -XX:InitiatingHeapOccupancyPercent=45
```

**Cassandra:**
```bash
# Increase heap size
MAX_HEAP_SIZE: 2G
HEAP_NEWSIZE: 512M
```

**Kafka:**
```bash
# Increase log retention
KAFKA_LOG_RETENTION_HOURS: 168
KAFKA_LOG_SEGMENT_BYTES: 1073741824
```

### Debug Mode

Enable debug logging:

```yaml
environment:
  LOGGING_LEVEL_ROOT: DEBUG
  LOGGING_LEVEL_COM_EXAMPLE_TESTCONTAINERS: TRACE
```

### Accessing Container Shell

```bash
# Spring Boot app
docker exec -it testcontainers-app sh

# Redis CLI
docker exec -it testcontainers-redis redis-cli

# Cassandra CQL
docker exec -it testcontainers-cassandra cqlsh

# Kafka
docker exec -it testcontainers-kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Backup and Recovery

### Redis Backup

```bash
# Create backup
docker exec testcontainers-redis redis-cli BGSAVE

# Copy backup file
docker cp testcontainers-redis:/data/dump.rdb ./backup/
```

### Cassandra Backup

```bash
# Create snapshot
docker exec testcontainers-cassandra nodetool snapshot

# Export data
docker exec testcontainers-cassandra cqlsh -e "COPY keyspace.table TO '/backup/table.csv'"
```

### Kafka Topic Backup

```bash
# Export messages
docker exec testcontainers-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic your-topic \
  --from-beginning > backup.json
```

## Maintenance

### Updating the Application

```bash
# Pull latest image
docker pull ghcr.io/your-org/testcontainers-demo:latest

# Restart with new image
docker-compose -f docker-compose.prod.yml up -d app
```

### Database Migrations

Migrations run automatically on startup. To manually run:

```bash
docker exec testcontainers-app java -jar app.jar --spring.flyway.enabled=true
```

### Cleanup

Remove unused volumes and images:

```bash
# Remove stopped containers
docker-compose -f docker-compose.prod.yml down

# Remove volumes (WARNING: deletes data)
docker-compose -f docker-compose.prod.yml down -v

# Clean up unused images
docker image prune -a
```

## Support

For issues and questions:
- GitHub Issues: https://github.com/your-org/testcontainers-demo/issues
- Documentation: https://github.com/your-org/testcontainers-demo/wiki
