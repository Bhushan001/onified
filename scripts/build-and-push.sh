#!/bin/bash

set -e

# Configuration
REGISTRY="your-registry.com"  # Replace with your registry
NAMESPACE="onified"
VERSION="latest"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting build and push process for Onified Platform...${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Check if user is logged in to registry
if ! docker info | grep -q "Username"; then
    echo -e "${YELLOW}Warning: You may need to login to your registry first:${NC}"
    echo "docker login $REGISTRY"
fi

# Array of services to build (removed eureka-server)
services=(
    "onified-gateway"
    "platform-management-service"
    "user-management-service"
    "tenant-management-service"
    "permission-registry-service"
    "application-config-service"
    "authentication-service"
)

# Build and push microservices
for service in "${services[@]}"; do
    echo -e "${GREEN}Building $service...${NC}"
    
    # Check if service directory exists
    if [ ! -d "$service" ]; then
        echo -e "${RED}Error: Directory $service not found. Skipping...${NC}"
        continue
    fi
    
    # Build image
    docker build -t $REGISTRY/$NAMESPACE/$service:$VERSION ./$service
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Successfully built $service${NC}"
        
        # Push image
        echo -e "${YELLOW}Pushing $service to registry...${NC}"
        docker push $REGISTRY/$NAMESPACE/$service:$VERSION
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}Successfully pushed $service${NC}"
        else
            echo -e "${RED}Failed to push $service${NC}"
            exit 1
        fi
    else
        echo -e "${RED}Failed to build $service${NC}"
        exit 1
    fi
done

# Build and push frontend
echo -e "${GREEN}Building frontend...${NC}"
if [ -d "web" ]; then
    docker build -t $REGISTRY/$NAMESPACE/frontend:$VERSION ./web
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Successfully built frontend${NC}"
        
        echo -e "${YELLOW}Pushing frontend to registry...${NC}"
        docker push $REGISTRY/$NAMESPACE/frontend:$VERSION
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}Successfully pushed frontend${NC}"
        else
            echo -e "${RED}Failed to push frontend${NC}"
            exit 1
        fi
    else
        echo -e "${RED}Failed to build frontend${NC}"
        exit 1
    fi
else
    echo -e "${RED}Error: Directory web not found. Skipping frontend...${NC}"
fi

echo -e "${GREEN}All images built and pushed successfully!${NC}"
echo -e "${YELLOW}Registry: $REGISTRY/$NAMESPACE${NC}"
echo -e "${YELLOW}Version: $VERSION${NC}" 