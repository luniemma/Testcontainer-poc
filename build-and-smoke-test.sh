#!/bin/bash

# Build and Smoke Test Script
# This script builds the Docker image and runs smoke tests

set -e  # Exit on error

echo "=================================================="
echo "  Testcontainers Demo - Build & Smoke Test"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check prerequisites
echo "Step 1: Checking prerequisites..."
echo "-----------------------------------"

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi
print_success "Docker found: $(docker --version)"

if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH"
    exit 1
fi
print_success "Maven found: $(mvn --version | head -n 1)"

if ! command -v java &> /dev/null; then
    print_error "Java is not installed or not in PATH"
    exit 1
fi
print_success "Java found: $(java --version | head -n 1)"

# Check if Docker is running
if ! docker info &> /dev/null; then
    print_error "Docker daemon is not running"
    exit 1
fi
print_success "Docker daemon is running"

echo ""

# Build the application
echo "Step 2: Building the application..."
echo "-----------------------------------"
print_info "Running: mvn clean package -DskipTests"
mvn clean package -DskipTests -B
print_success "Application built successfully"
echo ""

# Build Docker image
echo "Step 3: Building Docker image..."
echo "-----------------------------------"
IMAGE_NAME="testcontainers-demo"
IMAGE_TAG="latest"
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"

print_info "Building image: ${FULL_IMAGE_NAME}"
docker build -t ${FULL_IMAGE_NAME} .
print_success "Docker image built successfully"

# Verify image
docker images | grep ${IMAGE_NAME}
echo ""

# Run smoke tests
echo "Step 4: Running smoke tests..."
echo "-----------------------------------"
print_info "This will start Testcontainers and run all smoke tests"
print_info "Running: mvn test -Dtest=EnhancedSmokeTest"

if mvn test -Dtest=EnhancedSmokeTest; then
    print_success "Smoke tests passed!"
else
    print_error "Smoke tests failed!"
    exit 1
fi

echo ""

# Optional: Run all integration tests
read -p "Do you want to run all integration tests? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Step 5: Running all integration tests..."
    echo "-----------------------------------"
    print_info "Running: mvn verify"

    if mvn verify; then
        print_success "All integration tests passed!"
    else
        print_error "Some integration tests failed!"
        exit 1
    fi
    echo ""
fi

# Summary
echo "=================================================="
echo "  Build and Test Summary"
echo "=================================================="
print_success "Application JAR: target/testcontainers-demo-1.0.0.jar"
print_success "Docker Image: ${FULL_IMAGE_NAME}"
print_success "All smoke tests passed"
echo ""
echo "Next steps:"
echo "  - Run the app: docker run -p 8080:8080 ${FULL_IMAGE_NAME}"
echo "  - Use docker-compose: docker-compose up -d"
echo "  - Push to registry: docker tag ${FULL_IMAGE_NAME} <registry>/<name>:${IMAGE_TAG}"
echo "  - View logs: docker logs -f <container-name>"
echo ""
echo "For more details, see BUILD_AND_TEST.md"
echo "=================================================="
