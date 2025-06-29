#!/bin/bash

# Onified Platform - Database Setup Script
# This script creates all required databases in PostgreSQL container

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

# Function to check if PostgreSQL container is running
check_postgres_container() {
    print_status "Checking if PostgreSQL container is running..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    if ! docker ps --format "table {{.Names}}" | grep -q "postgres"; then
        print_error "PostgreSQL container is not running. Please start it first with: docker-compose up -d postgres"
        exit 1
    fi
    
    print_success "PostgreSQL container is running"
}

# Function to wait for PostgreSQL to be ready
wait_for_postgres() {
    print_status "Waiting for PostgreSQL to be ready..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker exec postgres pg_isready -U postgres >/dev/null 2>&1; then
            print_success "PostgreSQL is ready"
            return 0
        fi
        
        print_status "Attempt $attempt/$max_attempts: PostgreSQL not ready yet..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "PostgreSQL failed to start within expected time"
    exit 1
}

# Function to create database if it doesn't exist
create_database() {
    local db_name=$1
    local description=$2
    
    print_status "Creating database: $db_name ($description)"
    
    # Check if database already exists
    if docker exec postgres psql -U postgres -lqt | cut -d \| -f 1 | grep -qw "$db_name"; then
        print_warning "Database '$db_name' already exists, skipping..."
        return 0
    fi
    
    # Create database
    if docker exec postgres createdb -U postgres "$db_name"; then
        print_success "Database '$db_name' created successfully"
    else
        print_error "Failed to create database '$db_name'"
        return 1
    fi
}

# Function to create all databases
create_all_databases() {
    print_status "Creating all required databases..."
    echo "--------------------------------------------------"
    
    # Define databases to create
    declare -A databases=(
        ["platform_mgmt_db"]="Platform Management Service"
        ["tenant_management_db"]="Tenant Management Service"
        ["user_management_db"]="User Management Service"
        ["permission_registry_db"]="Permission Registry Service"
        ["application_config_db"]="Application Config Service"
        ["authentication_db"]="Authentication Service"
        ["keycloak_db"]="Keycloak Identity Provider"
    )
    
    # Create each database
    for db_name in "${!databases[@]}"; do
        description="${databases[$db_name]}"
        if ! create_database "$db_name" "$description"; then
            print_error "Failed to create database: $db_name"
            exit 1
        fi
    done
    
    echo "--------------------------------------------------"
    print_success "All databases created successfully!"
}

# Function to list all databases
list_databases() {
    print_status "Listing all databases:"
    echo "--------------------------------------------------"
    
    docker exec postgres psql -U postgres -c "\l" | grep -E "(platform_mgmt_db|tenant_management_db|user_management_db|permission_registry_db|application_config_db|authentication_db|keycloak_db)" || true
    
    echo "--------------------------------------------------"
}

# Function to test database connections
test_database_connections() {
    print_status "Testing database connections..."
    echo "--------------------------------------------------"
    
    local databases=("platform_mgmt_db" "tenant_management_db" "user_management_db" "permission_registry_db" "application_config_db" "authentication_db" "keycloak_db")
    
    for db_name in "${databases[@]}"; do
        if docker exec postgres psql -U postgres -d "$db_name" -c "SELECT 1;" >/dev/null 2>&1; then
            print_success "✓ Connection to '$db_name' successful"
        else
            print_error "✗ Connection to '$db_name' failed"
        fi
    done
    
    echo "--------------------------------------------------"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  create     Create all required databases (default)"
    echo "  list       List all databases"
    echo "  test       Test database connections"
    echo "  all        Create, list, and test databases"
    echo "  help       Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 create  # Create all databases"
    echo "  $0 list    # List existing databases"
    echo "  $0 test    # Test database connections"
    echo "  $0 all     # Full setup and verification"
}

# Main function
main() {
    local command=${1:-create}
    
    print_status "Starting Onified Platform database setup..."
    echo "=================================================="
    
    case "$command" in
        "create")
            check_postgres_container
            wait_for_postgres
            create_all_databases
            ;;
        "list")
            check_postgres_container
            list_databases
            ;;
        "test")
            check_postgres_container
            test_database_connections
            ;;
        "all")
            check_postgres_container
            wait_for_postgres
            create_all_databases
            echo ""
            list_databases
            echo ""
            test_database_connections
            ;;
        "help"|"-h"|"--help")
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown command: $command"
            show_usage
            exit 1
            ;;
    esac
    
    echo "=================================================="
    print_success "Database setup completed!"
}

# Run main function
main "$@" 