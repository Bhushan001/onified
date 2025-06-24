#!/bin/bash

# Setup script for creating log directories for all services
# This script creates the log directories specified in your .env file

echo "Setting up log directories for Onified Platform..."

# Default log directories (if .env is not loaded)
DEFAULT_LOG_DIRS=(
    "./logs/gateway"
    "./logs/app-config"
    "./logs/auth-service"
    "./logs/permission-service"
    "./logs/user-management"
    "./logs/frontend"
)

# Create log directories
for dir in "${DEFAULT_LOG_DIRS[@]}"; do
    echo "Creating log directory: $dir"
    mkdir -p "$dir"
    
    # Set appropriate permissions
    chmod 755 "$dir"
    
    echo "✓ Created: $dir"
done

echo ""
echo "Log directories setup complete!"
echo ""
echo "You can customize log directories by editing your .env file:"
echo "  GATEWAY_LOG_DIR=./logs/gateway"
echo "  APP_CONFIG_LOG_DIR=./logs/app-config"
echo "  AUTH_LOG_DIR=./logs/auth-service"
echo "  PERMISSION_LOG_DIR=./logs/permission-service"
echo "  USER_MGMT_LOG_DIR=./logs/user-management"
echo "  FRONTEND_LOG_DIR=./logs/frontend"
echo ""
echo "Log files will be available at:"
echo "  - Gateway: ./logs/gateway/gateway.log"
echo "  - App Config: ./logs/app-config/application-config-service.log"
echo "  - Auth Service: ./logs/auth-service/authentication-service.log"
echo "  - Permission Service: ./logs/permission-service/permission-registry-service.log"
echo "  - User Management: ./logs/user-management/user-management-service.log"
echo "  - Frontend: ./logs/frontend/frontend.log" 