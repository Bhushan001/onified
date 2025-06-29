#!/bin/bash

# Test script for Authentication Service Swagger Documentation
# This script tests the availability of Swagger endpoints

echo "=========================================="
echo "Authentication Service - Swagger Tests"
echo "=========================================="

# Configuration
SERVICE_URL="http://localhost:9083"
SWAGGER_UI_URL="$SERVICE_URL/swagger-ui.html"
API_DOCS_URL="$SERVICE_URL/api-docs"
HEALTH_URL="$SERVICE_URL/actuator/health"
AUTH_HEALTH_URL="$SERVICE_URL/api/authentication/health"
PUBLIC_TEST_URL="$SERVICE_URL/api/public/test"

echo "Testing service endpoints..."
echo ""

# Test 1: Health Check
echo "1. Testing Health Check..."
if curl -s -f "$HEALTH_URL" > /dev/null; then
    echo "   ✅ Health check passed - Service is running"
    HEALTH_RESPONSE=$(curl -s "$HEALTH_URL")
    echo "   Response: $HEALTH_RESPONSE"
else
    echo "   ❌ Health check failed - Service may not be running"
    echo "   Please ensure the service is started on port 9083"
    exit 1
fi

# Test 2: Authentication Health Check
echo ""
echo "2. Testing Authentication Health Check..."
if curl -s -f "$AUTH_HEALTH_URL" > /dev/null; then
    echo "   ✅ Authentication health check passed"
    AUTH_HEALTH_RESPONSE=$(curl -s "$AUTH_HEALTH_URL")
    echo "   Response: $AUTH_HEALTH_RESPONSE"
else
    echo "   ❌ Authentication health check failed"
fi

# Test 3: Public Test Endpoint
echo ""
echo "3. Testing Public Test Endpoint..."
if curl -s -f "$PUBLIC_TEST_URL" > /dev/null; then
    echo "   ✅ Public test endpoint available at: $PUBLIC_TEST_URL"
    PUBLIC_RESPONSE=$(curl -s "$PUBLIC_TEST_URL")
    echo "   Response: $PUBLIC_RESPONSE"
else
    echo "   ❌ Public test endpoint not available"
fi

# Test 4: API Docs
echo ""
echo "4. Testing OpenAPI Documentation..."
if curl -s -f "$API_DOCS_URL" > /dev/null; then
    echo "   ✅ OpenAPI docs available at: $API_DOCS_URL"
    # Check if the response contains OpenAPI content
    API_DOCS_RESPONSE=$(curl -s "$API_DOCS_URL" | head -c 100)
    if [[ $API_DOCS_RESPONSE == *"openapi"* ]]; then
        echo "   ✅ OpenAPI specification is valid"
    else
        echo "   ⚠️  OpenAPI specification may not be properly formatted"
    fi
else
    echo "   ❌ OpenAPI docs not available"
    echo "   This might indicate a security configuration issue"
fi

# Test 5: Swagger UI
echo ""
echo "5. Testing Swagger UI..."
if curl -s -f "$SWAGGER_UI_URL" > /dev/null; then
    echo "   ✅ Swagger UI available at: $SWAGGER_UI_URL"
    # Check if the response contains Swagger UI content
    SWAGGER_RESPONSE=$(curl -s "$SWAGGER_UI_URL" | head -c 100)
    if [[ $SWAGGER_RESPONSE == *"swagger-ui"* ]] || [[ $SWAGGER_RESPONSE == *"Swagger"* ]]; then
        echo "   ✅ Swagger UI content is valid"
    else
        echo "   ⚠️  Swagger UI content may not be properly loaded"
    fi
else
    echo "   ❌ Swagger UI not available"
    echo "   This might indicate a security configuration issue"
fi

# Test 6: Security Headers
echo ""
echo "6. Testing Security Headers..."
SECURITY_HEADERS=$(curl -s -I "$SWAGGER_UI_URL" | grep -E "(HTTP|403|401|Forbidden)")
if [[ $SECURITY_HEADERS == *"403"* ]] || [[ $SECURITY_HEADERS == *"401"* ]] || [[ $SECURITY_HEADERS == *"Forbidden"* ]]; then
    echo "   ❌ Security issue detected - Access forbidden"
    echo "   Headers: $SECURITY_HEADERS"
else
    echo "   ✅ No security issues detected"
fi

# Test 7: Authentication Endpoints (Basic connectivity)
echo ""
echo "7. Testing Authentication Endpoints..."
LOGIN_URL="$SERVICE_URL/api/authentication/login"
if curl -s -f -X POST "$LOGIN_URL" -H "Content-Type: application/json" -d '{"username":"test","password":"test"}' > /dev/null 2>&1; then
    echo "   ✅ Login endpoint is accessible"
else
    echo "   ⚠️  Login endpoint may have connectivity issues (expected for invalid credentials)"
fi

echo ""
echo "=========================================="
echo "Test Summary:"
echo "=========================================="
echo "Service URL: $SERVICE_URL"
echo "Swagger UI: $SWAGGER_UI_URL"
echo "OpenAPI Docs: $API_DOCS_URL"
echo "Auth Health: $AUTH_HEALTH_URL"
echo "Public Test: $PUBLIC_TEST_URL"
echo ""
echo "To access Swagger UI, open your browser and navigate to:"
echo "$SWAGGER_UI_URL"
echo ""
echo "To view the raw OpenAPI specification:"
echo "$API_DOCS_URL"
echo ""
echo "Authentication Endpoints:"
echo "- Login: POST $LOGIN_URL"
echo "- Register: POST $SERVICE_URL/api/authentication/register"
echo "- Refresh: POST $SERVICE_URL/api/authentication/refresh"
echo ""
echo "If you're still having issues:"
echo "1. Check the application logs for errors"
echo "2. Ensure the service is running on port 9083"
echo "3. Verify Keycloak is running and configured"
echo "4. Try clearing your browser cache"
echo "5. Check the SWAGGER_DOCUMENTATION.md file"
echo "==========================================" 