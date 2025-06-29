#!/bin/bash

# Test Swagger/OpenAPI Documentation for Platform Management Service
# This script tests the Swagger endpoints to ensure they are working correctly

SERVICE_URL=${1:-"http://localhost:9081"}
VERBOSE=${2:-false}

echo "🔍 Testing Platform Management Service Swagger/OpenAPI Documentation"
echo "Service URL: $SERVICE_URL"
echo ""

# Function to test endpoint
test_endpoint() {
    local url=$1
    local description=$2
    local method=${3:-"GET"}
    
    echo "Testing: $description"
    echo "  URL: $method $url"
    
    if command -v curl &> /dev/null; then
        response=$(curl -s -o /tmp/response.txt -w "%{http_code}" -X "$method" "$url" --connect-timeout 10)
        
        if [ "$response" = "200" ]; then
            echo "  ✅ Success (Status: $response)"
            if [ "$VERBOSE" = "true" ]; then
                content_length=$(wc -c < /tmp/response.txt)
                echo "  Response Length: $content_length characters"
            fi
        else
            echo "  ⚠️  Unexpected Status: $response"
        fi
    else
        echo "  ❌ Error: curl command not found"
    fi
    echo ""
}

# Test public endpoint first
echo "📋 Testing Public Endpoints"
test_endpoint "$SERVICE_URL/api/public/test" "Public Test Endpoint"

# Test Swagger UI
echo "📋 Testing Swagger UI"
test_endpoint "$SERVICE_URL/swagger-ui.html" "Swagger UI Interface"

# Test OpenAPI JSON specification
echo "📋 Testing OpenAPI Specifications"
test_endpoint "$SERVICE_URL/api-docs" "OpenAPI JSON Specification"
test_endpoint "$SERVICE_URL/api-docs.yaml" "OpenAPI YAML Specification"

# Test API endpoints (these might require authentication)
echo "📋 Testing API Endpoints (may require authentication)"
test_endpoint "$SERVICE_URL/api/password-policies" "Get All Password Policies"
test_endpoint "$SERVICE_URL/api/tenants" "Get All Tenants"

# Test health endpoint
echo "📋 Testing Health Endpoint"
test_endpoint "$SERVICE_URL/actuator/health" "Service Health Check"

# Test security configuration
echo "🔐 Testing Security Configuration"
test_endpoint "$SERVICE_URL/actuator/info" "Actuator Info Endpoint"

echo "🎯 Swagger Documentation Test Complete!"
echo ""
echo "📖 To access the interactive Swagger UI:"
echo "   Open your browser and navigate to: $SERVICE_URL/swagger-ui.html"
echo ""
echo "📄 To get the OpenAPI specification:"
echo "   JSON: $SERVICE_URL/api-docs"
echo "   YAML: $SERVICE_URL/api-docs.yaml"
echo ""
echo "🔧 For more information, see: platform-management-service/SWAGGER_README.md"
echo "🔐 Security configuration: platform-management-service/SWAGGER_SECURITY_CONFIG.md"

# Clean up
rm -f /tmp/response.txt 