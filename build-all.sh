#!/bin/bash

# Onified Platform - Build All Services Script
# This script builds all Docker images for the Onified platform

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
}

# Function to build Java service
build_java_service() {
    local service_name=$1
    local service_dir=$2
    
    print_status "Building Java service: $service_name"
    
    if [ ! -f "$service_dir/pom.xml" ]; then
        print_warning "No pom.xml found in $service_dir, skipping Maven build"
        return 0
    fi
    
    # Build JAR file
    print_status "Running Maven build for $service_name..."
    (cd "$service_dir" && mvn clean package -DskipTests)
    
    if [ $? -eq 0 ]; then
        print_success "Maven build completed for $service_name"
    else
        print_error "Maven build failed for $service_name"
        return 1
    fi
}

# Function to build Docker image
build_docker_image() {
    local service_name=$1
    local service_dir=$2
    
    print_status "Building Docker image for: $service_name"
    
    if [ ! -f "$service_dir/Dockerfile" ]; then
        print_warning "No Dockerfile found in $service_dir, skipping Docker build"
        return 0
    fi
    
    # Build Docker image
    docker build -t "$service_name" "$service_dir"
    
    if [ $? -eq 0 ]; then
        print_success "Docker image built successfully: $service_name"
    else
        print_error "Docker build failed for $service_name"
        return 1
    fi
}

# Main build process
main() {
    print_status "Starting Onified Platform build process..."
    echo "=================================================="
    
    # Check prerequisites
    check_prerequisites
    
    # Define services to build (using parallel arrays for zsh compatibility)
    service_names=("onified-gateway" \
                  "platform-management-service" \
                  "authentication-service" \
                  "user-management-service" \
                  "permission-registry-service" \
                  "application-config-service" \
                  "tenant-management-service" \
                  "onified-frontend")
    service_dirs=("./onified-gateway" \
                 "./platform-management-service" \
                 "./authentication-service" \
                 "./user-management-service" \
                 "./permission-registry-service" \
                 "./application-config-service" \
                 "./tenant-management-service" \
                 "./web")
    
    # Build Java services first (JAR files)
    print_status "Building Java services (JAR files)..."
    echo "--------------------------------------------------"
    
    for i in "${!service_names[@]}"; do
        service_name="${service_names[$i]}"
        service_dir="${service_dirs[$i]}"
        
        # Skip frontend for Maven build
        if [ "$service_name" != "onified-frontend" ]; then
            if ! build_java_service "$service_name" "$service_dir"; then
                print_error "Failed to build Java service: $service_name"
                exit 1
            fi
        fi
    done
    
    # Build Docker images
    print_status "Building Docker images..."
    echo "--------------------------------------------------"
    
    for i in "${!service_names[@]}"; do
        service_name="${service_names[$i]}"
        service_dir="${service_dirs[$i]}"
        
        if ! build_docker_image "$service_name" "$service_dir"; then
            print_error "Failed to build Docker image: $service_name"
            exit 1
        fi
    done
    
    echo "=================================================="
    print_success "All builds completed successfully!"
    print_status "You can now run: docker-compose up -d"
}

# Run main function
main "$@" 