# Multi-stage Dockerfile for Spring Boot Application
# Stage 1: Build the application (multi-arch friendly base)
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /build

# Copy Maven configuration files first (for better layer caching)
COPY pom.xml .

# Download dependencies (cached layer if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests in Docker build for faster builds)
# Tests are run in CI/CD pipeline
RUN mvn clean package -DskipTests -B

# Stage 2: Create the runtime image (multi-arch friendly base)
FROM eclipse-temurin:17-jre AS runtime

# Add metadata labels
LABEL maintainer="your-team@example.com"
LABEL org.opencontainers.image.source="https://github.com/your-org/testcontainers-demo"
LABEL org.opencontainers.image.description="Spring Boot Testcontainers Demo Application"
LABEL org.opencontainers.image.licenses="MIT"

# Install dumb-init and utilities for proper signal handling and health checks
RUN apt-get update && \
    apt-get install -y --no-install-recommends dumb-init wget ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd --gid 1001 appgroup && \
    useradd --uid 1001 --gid appgroup --shell /bin/bash --create-home appuser

# Set working directory
WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Configure JVM options for containerized environments
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
