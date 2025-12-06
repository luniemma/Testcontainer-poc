# Docker & CI/CD Setup

This document describes the enterprise-grade Docker and CI/CD infrastructure for the Testcontainers Demo application.

## Overview

The project includes:

- **Multi-stage Dockerfile** for optimized production builds
- **Reusable GitHub Actions workflow** for CI/CD automation
- **Security scanning** with Trivy and OWASP Dependency Check
- **Code coverage** with JaCoCo
- **Multi-platform support** (AMD64 and ARM64)
- **Production-ready Docker Compose** with monitoring

## Project Structure

```
.
├── Dockerfile                      # Multi-stage production Dockerfile
├── .dockerignore                   # Optimize Docker build context
├── docker-compose.yml              # Development environment
├── docker-compose.prod.yml         # Production deployment
├── prometheus.yml                  # Metrics configuration
├── dependency-check-suppressions.xml
├── DEPLOYMENT.md                   # Deployment guide
├── .github/
│   └── workflows/
│       ├── ci-cd.yml              # Reusable workflow
│       ├── build.yml              # Main build workflow
│       └── release.yml            # Release workflow
└── pom.xml                        # Maven with security plugins
```

## Dockerfile Features

### Security Hardening

1. **Multi-stage build**: Separates build and runtime environments
2. **Non-root user**: Runs as user ID 1001 (appuser)
3. **Minimal base image**: Uses Alpine-based Eclipse Temurin JRE
4. **dumb-init**: Proper signal handling for graceful shutdowns
5. **Health checks**: Built-in container health monitoring

### Performance Optimization

1. **Layer caching**: Optimized layer ordering for faster builds
2. **Dependency caching**: Maven dependencies cached separately
3. **JVM tuning**: Container-aware JVM settings
4. **G1GC**: Modern garbage collector for low latency

### Build Command

```bash
# Simple build
docker build -t testcontainers-demo:latest .

# Multi-platform build
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t testcontainers-demo:latest \
  --push .

# Build with metadata
docker build \
  --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
  --build-arg VCS_REF=$(git rev-parse --short HEAD) \
  --build-arg VERSION=1.0.0 \
  -t testcontainers-demo:1.0.0 .
```

## GitHub Actions Workflow

### Reusable Workflow Features

The `.github/workflows/ci-cd.yml` provides:

#### Build & Test
- Maven build with dependency caching
- Unit and integration tests
- JaCoCo code coverage
- Codecov integration
- SonarCloud analysis (optional)

#### Security Scanning
- OWASP Dependency Check (CVSS ≥ 7 fails build)
- CodeQL static analysis
- Trivy container vulnerability scanning
- SBOM (Software Bill of Materials) generation

#### Docker Build & Push
- Multi-platform builds (AMD64, ARM64)
- Automatic tagging (semver, branch, SHA)
- GitHub Container Registry integration
- Build cache optimization

#### Deployment
- Staging: Auto-deploy on `develop` branch
- Production: Manual approval for tagged releases

### Workflow Inputs

```yaml
inputs:
  java-version: '17'           # Java version
  maven-version: '3.9.6'       # Maven version
  docker-registry: 'ghcr.io'   # Container registry
  docker-platforms: 'linux/amd64,linux/arm64'
  enable-security-scan: true   # Enable Trivy/OWASP
  run-tests: true              # Run test suite
```

### Using the Workflow

**Method 1: Direct push/tag**
```bash
# Trigger on push to main
git push origin main

# Trigger release pipeline
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

**Method 2: Call from another workflow**
```yaml
name: My Custom Build

on:
  push:
    branches: [main]

jobs:
  build:
    uses: ./.github/workflows/ci-cd.yml
    with:
      java-version: '17'
      enable-security-scan: true
    secrets:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

## Maven Configuration

### Added Plugins

#### 1. JaCoCo (Code Coverage)

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.11</version>
</plugin>
```

Run coverage:
```bash
mvn clean test jacoco:report
```

View report: `target/site/jacoco/index.html`

#### 2. OWASP Dependency Check

```xml
<plugin>
  <groupId>org.owasp</groupId>
  <artifactId>dependency-check-maven</artifactId>
  <version>9.0.7</version>
</plugin>
```

Run security scan:
```bash
mvn dependency-check:check
```

View report: `target/dependency-check-report.html`

#### 3. SonarQube Scanner

```xml
<plugin>
  <groupId>org.sonarsource.scanner.maven</groupId>
  <artifactId>sonar-maven-plugin</artifactId>
  <version>3.10.0.2594</version>
</plugin>
```

Run SonarCloud analysis:
```bash
mvn sonar:sonar \
  -Dsonar.projectKey=your-project \
  -Dsonar.organization=your-org \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=$SONAR_TOKEN
```

### Spring Boot Actuator

Added for health checks and metrics:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      probes:
        enabled: true
```

Endpoints:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

## Docker Compose Production Setup

### Services

The `docker-compose.prod.yml` includes:

1. **Application** (testcontainers-demo)
   - Health checks
   - Resource limits
   - Log volumes
   - Auto-restart

2. **Redis** (Cache)
   - Persistence enabled
   - Password protected
   - Health monitoring

3. **Kafka + Zookeeper** (Message Queue)
   - Auto topic creation
   - Log retention configured
   - Persistent volumes

4. **Cassandra** (Database)
   - Single node (scale to 3+ in production)
   - Persistent storage
   - Health checks with startup delay

5. **Prometheus** (Metrics Collection)
   - Scrapes app metrics
   - 30-day retention
   - Custom alerting rules

6. **Grafana** (Visualization)
   - Pre-configured dashboards
   - Prometheus data source
   - Password protected

### Resource Allocation

```yaml
app:
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
      reservations:
        cpus: '1'
        memory: 1G
```

Adjust based on your environment.

### Starting Production Stack

```bash
# Start all services
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Check status
docker-compose -f docker-compose.prod.yml ps

# Stop everything
docker-compose -f docker-compose.prod.yml down
```

### Environment Variables

Create `.env` file:

```env
# Redis
REDIS_PASSWORD=your-secure-password

# Grafana
GRAFANA_PASSWORD=your-admin-password

# Application
SPRING_PROFILES_ACTIVE=prod
```

## Image Tagging Strategy

The CI/CD pipeline automatically tags images:

| Trigger | Tag Example | Use Case |
|---------|-------------|----------|
| Push to main | `latest`, `main-abc1234` | Latest development |
| Push to develop | `develop`, `develop-abc1234` | Staging/testing |
| Tag `v1.2.3` | `v1.2.3`, `1.2`, `1`, `latest` | Production releases |
| Pull request | `pr-123` | Testing PRs |

## Security Best Practices

### 1. Image Scanning

Every image is scanned with Trivy:
```bash
trivy image ghcr.io/your-org/testcontainers-demo:latest
```

### 2. SBOM Generation

Software Bill of Materials is generated:
```bash
# Download from GitHub Actions artifacts
# File: sbom.spdx.json
```

### 3. Dependency Scanning

OWASP checks all dependencies:
```bash
mvn dependency-check:check
```

### 4. Static Analysis

CodeQL scans for security issues:
- SQL injection
- XSS vulnerabilities
- Insecure deserialization
- Hard-coded credentials

### 5. Non-Root Container

Dockerfile runs as UID 1001:
```dockerfile
USER appuser
```

Verify:
```bash
docker run --rm testcontainers-demo whoami
# Output: appuser
```

## Monitoring & Observability

### Metrics

Access Prometheus metrics:
```bash
curl http://localhost:8080/actuator/prometheus
```

Key metrics:
- `jvm_memory_used_bytes`
- `http_server_requests_seconds`
- `kafka_consumer_lag`
- `redis_commands_processed_total`

### Health Checks

Kubernetes-style probes:

```bash
# Liveness (is app running?)
curl http://localhost:8080/actuator/health/liveness

# Readiness (can app handle traffic?)
curl http://localhost:8080/actuator/health/readiness
```

### Grafana Dashboards

Import these dashboard IDs:

1. **Spring Boot Statistics** - ID: 10280
2. **JVM Dashboard** - ID: 4701
3. **Kafka Exporter** - ID: 7589

Access: http://localhost:3000

## Troubleshooting

### Build Failures

**Maven build fails:**
```bash
# Check Java version
java -version

# Clean and rebuild
mvn clean install -U
```

**Docker build fails:**
```bash
# Check build context
docker build --progress=plain -t test .

# Clean Docker cache
docker builder prune -a
```

### Container Issues

**App won't start:**
```bash
# Check logs
docker logs testcontainers-app

# Check health
docker inspect testcontainers-app | grep Health -A 10
```

**Out of memory:**
```bash
# Increase JVM heap
JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Check container memory
docker stats testcontainers-app
```

### CI/CD Issues

**Workflow not triggering:**
- Check branch protection rules
- Verify `.github/workflows/` location
- Check workflow syntax with `yamllint`

**Docker push fails:**
```bash
# Re-login to registry
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin
```

**Security scan failures:**
```bash
# Review and suppress false positives
# Edit: dependency-check-suppressions.xml
```

## Performance Tuning

### JVM Options

```bash
JAVA_OPTS="
  -XX:+UseContainerSupport
  -XX:MaxRAMPercentage=75.0
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+UseStringDeduplication
  -Xlog:gc:file=/app/logs/gc.log
"
```

### Maven Build

```bash
# Parallel builds
mvn -T 4 clean package

# Offline mode (after dependencies cached)
mvn -o clean package
```

### Docker Build

```bash
# Use BuildKit
DOCKER_BUILDKIT=1 docker build .

# Multi-stage caching
docker build --target builder -t builder-cache .
docker build --cache-from builder-cache -t final .
```

## Best Practices Summary

✅ **DO:**
- Use multi-stage builds
- Run containers as non-root
- Enable health checks
- Scan images for vulnerabilities
- Use semantic versioning
- Cache dependencies
- Monitor application metrics
- Review security scan reports
- Test in staging before production

❌ **DON'T:**
- Commit secrets to Git
- Run as root user
- Skip security scans
- Use `latest` tag in production
- Ignore vulnerability warnings
- Deploy without health checks
- Skip testing in CI
- Use unverified base images

## Additional Resources

- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Production Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [Trivy Documentation](https://aquasecurity.github.io/trivy/)

## Support

For issues:
- GitHub Issues: https://github.com/your-org/testcontainers-demo/issues
- Security: security@your-org.com
