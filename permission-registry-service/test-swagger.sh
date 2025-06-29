#!/bin/bash

# Permission Registry Service - Swagger Test Script
# This script tests the Swagger endpoints to verify the documentation is working correctly

set -e

# Configuration
SERVICE_URL="http://localhost:9084"
SERVICE_NAME="Permission Registry Service"

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

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    local description=$5
    
    print_status "Testing $description..."
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$SERVICE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            "$SERVICE_URL$endpoint")
    fi
    
    # Extract status code (last line)
    status_code=$(echo "$response" | tail -n1)
    # Extract response body (all lines except last)
    response_body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" -eq "$expected_status" ]; then
        print_success "$description - Status: $status_code"
        echo "Response: $response_body" | head -c 200
        if [ ${#response_body} -gt 200 ]; then
            echo "..."
        fi
        echo
    else
        print_error "$description - Expected: $expected_status, Got: $status_code"
        echo "Response: $response_body"
        echo
    fi
}

# Function to check if service is running
check_service() {
    print_status "Checking if $SERVICE_NAME is running..."
    
    if curl -s "$SERVICE_URL/actuator/health" > /dev/null 2>&1; then
        print_success "$SERVICE_NAME is running"
        return 0
    else
        print_error "$SERVICE_NAME is not running or not accessible at $SERVICE_URL"
        print_warning "Please start the service and try again"
        return 1
    fi
}

# Function to test Swagger UI accessibility
test_swagger_ui() {
    print_status "Testing Swagger UI accessibility..."
    
    if curl -s "$SERVICE_URL/swagger-ui.html" | grep -q "Swagger UI"; then
        print_success "Swagger UI is accessible"
        echo "URL: $SERVICE_URL/swagger-ui.html"
    else
        print_error "Swagger UI is not accessible"
    fi
    echo
}

# Function to test OpenAPI documentation
test_openapi_docs() {
    print_status "Testing OpenAPI documentation..."
    
    if curl -s "$SERVICE_URL/api-docs" | grep -q "openapi"; then
        print_success "OpenAPI JSON documentation is accessible"
        echo "URL: $SERVICE_URL/api-docs"
    else
        print_error "OpenAPI JSON documentation is not accessible"
    fi
    
    if curl -s "$SERVICE_URL/api-docs.yaml" | grep -q "openapi"; then
        print_success "OpenAPI YAML documentation is accessible"
        echo "URL: $SERVICE_URL/api-docs.yaml"
    else
        print_error "OpenAPI YAML documentation is not accessible"
    fi
    echo
}

# Main test execution
main() {
    echo "=========================================="
    echo "  $SERVICE_NAME - Swagger Test Suite"
    echo "=========================================="
    echo
    
    # Check if service is running
    if ! check_service; then
        exit 1
    fi
    
    # Test Swagger UI and OpenAPI docs
    test_swagger_ui
    test_openapi_docs
    
    # Test public endpoints
    print_status "Testing public endpoints..."
    echo
    
    test_endpoint "GET" "/api/public/health" "" 200 "Health check endpoint"
    test_endpoint "GET" "/api/public/info" "" 200 "Service information endpoint"
    test_endpoint "POST" "/api/public/echo" '{"test": "data"}' 200 "Echo endpoint"
    
    # Test role management endpoints
    print_status "Testing role management endpoints..."
    echo
    
    # Create a test role
    test_endpoint "POST" "/api/roles" '{
        "roleId": "TEST_ROLE",
        "displayName": "Test Role",
        "appCode": "TEST_APP",
        "moduleCode": "TEST_MODULE",
        "roleFunction": "TEST_FUNCTION",
        "isActive": true,
        "tenantCustomizable": false
    }' 201 "Create role"
    
    # Get all roles
    test_endpoint "GET" "/api/roles" "" 200 "Get all roles"
    
    # Get specific role
    test_endpoint "GET" "/api/roles/TEST_ROLE" "" 200 "Get role by ID"
    
    # Update role
    test_endpoint "PUT" "/api/roles/TEST_ROLE" '{
        "roleId": "TEST_ROLE",
        "displayName": "Updated Test Role",
        "appCode": "TEST_APP",
        "moduleCode": "TEST_MODULE",
        "roleFunction": "TEST_FUNCTION",
        "isActive": true,
        "tenantCustomizable": true
    }' 200 "Update role"
    
    # Test PBU endpoints
    print_status "Testing Permission Bundle Unit endpoints..."
    echo
    
    # Create a test PBU
    test_endpoint "POST" "/api/pbus" '{
        "pbuId": "TEST_PBU",
        "displayName": "Test Permission",
        "apiEndpoint": "/api/test",
        "actionCode": "TEST",
        "scopeCode": "TEST_SCOPE",
        "isActive": true,
        "version": "1.0"
    }' 201 "Create PBU"
    
    # Get all PBUs
    test_endpoint "GET" "/api/pbus" "" 200 "Get all PBUs"
    
    # Get specific PBU
    test_endpoint "GET" "/api/pbus/TEST_PBU" "" 200 "Get PBU by ID"
    
    # Test error cases
    print_status "Testing error cases..."
    echo
    
    test_endpoint "GET" "/api/roles/NONEXISTENT_ROLE" "" 404 "Get non-existent role"
    test_endpoint "GET" "/api/pbus/NONEXISTENT_PBU" "" 404 "Get non-existent PBU"
    
    # Cleanup test data
    print_status "Cleaning up test data..."
    echo
    
    test_endpoint "DELETE" "/api/roles/TEST_ROLE" "" 204 "Delete test role"
    test_endpoint "DELETE" "/api/pbus/TEST_PBU" "" 204 "Delete test PBU"
    
    echo "=========================================="
    print_success "All tests completed!"
    echo "=========================================="
    echo
    print_status "Swagger UI is available at: $SERVICE_URL/swagger-ui.html"
    print_status "OpenAPI docs are available at: $SERVICE_URL/api-docs"
    echo
}

# Run main function
main "$@" 