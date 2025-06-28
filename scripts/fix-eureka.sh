#!/bin/bash

# Quick Fix for Eureka Self-Preservation Warning
# This script restarts Eureka with proper configuration

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

print_status "Fixing Eureka self-preservation warning..."

# Check if Eureka is running
if lsof -Pi :8761 -sTCP:LISTEN -t >/dev/null 2>&1; then
    print_status "Eureka server is running. Stopping it..."
    
    # Find and kill Eureka process
    EUREKA_PID=$(lsof -ti:8761)
    if [ -n "$EUREKA_PID" ]; then
        kill -9 $EUREKA_PID
        print_success "Eureka server stopped (PID: $EUREKA_PID)"
    fi
else
    print_warning "Eureka server is not running"
fi

# Wait a moment for port to be released
sleep 2

# Check if port is now available
if lsof -Pi :8761 -sTCP:LISTEN -t >/dev/null 2>&1; then
    print_error "Port 8761 is still in use. Please manually stop the process using port 8761"
    exit 1
fi

print_success "Port 8761 is now available"

# Start Eureka with updated configuration
print_status "Starting Eureka server with fixed configuration..."

cd eureka-server

# Start Eureka in background
nohup mvn spring-boot:run > ../logs/eureka-server.log 2>&1 &
EUREKA_PID=$!

# Save PID
echo $EUREKA_PID > ../logs/eureka-server.pid

print_success "Eureka server started with PID $EUREKA_PID"

# Wait for Eureka to be ready
print_status "Waiting for Eureka to be ready..."
sleep 10

# Check if Eureka is responding
max_attempts=30
attempt=1

while [ $attempt -le $max_attempts ]; do
    if curl -s http://localhost:8761/actuator/health >/dev/null 2>&1; then
        print_success "Eureka server is ready!"
        break
    fi
    
    print_status "Attempt $attempt/$max_attempts: Eureka not ready yet..."
    sleep 2
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    print_error "Eureka server failed to start within expected time"
    exit 1
fi

cd ..

# Check Eureka status
print_status "Checking Eureka status..."
curl -s http://localhost:8761/eureka/apps | jq '.' 2>/dev/null || print_warning "No services registered yet"

print_success "Eureka fix completed!"
print_status "Eureka dashboard: http://localhost:8761"
print_status "Check the dashboard to verify the self-preservation warning is gone"

# Optional: Restart other services if they were running
read -p "Do you want to restart other services? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_status "Restarting other services..."
    ./scripts/start-local.sh start
fi 