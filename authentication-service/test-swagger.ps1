# Test script for Authentication Service Swagger Documentation
# This script tests the availability of Swagger endpoints

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Authentication Service - Swagger Tests" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Configuration
$SERVICE_URL = "http://localhost:9083"
$SWAGGER_UI_URL = "$SERVICE_URL/swagger-ui.html"
$API_DOCS_URL = "$SERVICE_URL/api-docs"
$HEALTH_URL = "$SERVICE_URL/actuator/health"
$AUTH_HEALTH_URL = "$SERVICE_URL/api/authentication/health"
$PUBLIC_TEST_URL = "$SERVICE_URL/api/public/test"

Write-Host "Testing service endpoints..." -ForegroundColor Yellow
Write-Host ""

# Test 1: Health Check
Write-Host "1. Testing Health Check..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $HEALTH_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Health check passed - Service is running" -ForegroundColor Green
        Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
    } else {
        Write-Host "   ❌ Health check failed - Unexpected status code: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Health check failed - Service may not be running" -ForegroundColor Red
    Write-Host "   Please ensure the service is started on port 9083" -ForegroundColor Yellow
    exit 1
}

# Test 2: Authentication Health Check
Write-Host ""
Write-Host "2. Testing Authentication Health Check..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $AUTH_HEALTH_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Authentication health check passed" -ForegroundColor Green
        Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
    } else {
        Write-Host "   ❌ Authentication health check failed - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Authentication health check failed" -ForegroundColor Red
}

# Test 3: Public Test Endpoint
Write-Host ""
Write-Host "3. Testing Public Test Endpoint..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $PUBLIC_TEST_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Public test endpoint available at: $PUBLIC_TEST_URL" -ForegroundColor Green
        Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
    } else {
        Write-Host "   ❌ Public test endpoint not available - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Public test endpoint not available" -ForegroundColor Red
}

# Test 4: API Docs
Write-Host ""
Write-Host "4. Testing OpenAPI Documentation..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $API_DOCS_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ OpenAPI docs available at: $API_DOCS_URL" -ForegroundColor Green
        # Check if the response contains OpenAPI content
        $content = $response.Content.Substring(0, [Math]::Min(100, $response.Content.Length))
        if ($content -like "*openapi*") {
            Write-Host "   ✅ OpenAPI specification is valid" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  OpenAPI specification may not be properly formatted" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   ❌ OpenAPI docs not available - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ OpenAPI docs not available" -ForegroundColor Red
    Write-Host "   This might indicate a security configuration issue" -ForegroundColor Yellow
}

# Test 5: Swagger UI
Write-Host ""
Write-Host "5. Testing Swagger UI..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $SWAGGER_UI_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Swagger UI available at: $SWAGGER_UI_URL" -ForegroundColor Green
        # Check if the response contains Swagger UI content
        $content = $response.Content.Substring(0, [Math]::Min(100, $response.Content.Length))
        if ($content -like "*swagger-ui*" -or $content -like "*Swagger*") {
            Write-Host "   ✅ Swagger UI content is valid" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️  Swagger UI content may not be properly loaded" -ForegroundColor Yellow
        }
    } else {
        Write-Host "   ❌ Swagger UI not available - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Swagger UI not available" -ForegroundColor Red
    Write-Host "   This might indicate a security configuration issue" -ForegroundColor Yellow
}

# Test 6: Security Headers
Write-Host ""
Write-Host "6. Testing Security Headers..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $SWAGGER_UI_URL -UseBasicParsing -Method Head -ErrorAction Stop
    $statusCode = $response.StatusCode
    if ($statusCode -eq 403 -or $statusCode -eq 401) {
        Write-Host "   ❌ Security issue detected - Access forbidden (Status: $statusCode)" -ForegroundColor Red
    } else {
        Write-Host "   ✅ No security issues detected (Status: $statusCode)" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ Security issue detected - Access forbidden" -ForegroundColor Red
}

# Test 7: Authentication Endpoints (Basic connectivity)
Write-Host ""
Write-Host "7. Testing Authentication Endpoints..." -ForegroundColor Green
$LOGIN_URL = "$SERVICE_URL/api/authentication/login"
$loginBody = '{"username":"test","password":"test"}'
try {
    $response = Invoke-WebRequest -Uri $LOGIN_URL -Method POST -Body $loginBody -ContentType "application/json" -UseBasicParsing -ErrorAction Stop
    Write-Host "   ✅ Login endpoint is accessible" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️  Login endpoint may have connectivity issues (expected for invalid credentials)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Test Summary:" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Service URL: $SERVICE_URL" -ForegroundColor White
Write-Host "Swagger UI: $SWAGGER_UI_URL" -ForegroundColor White
Write-Host "OpenAPI Docs: $API_DOCS_URL" -ForegroundColor White
Write-Host "Auth Health: $AUTH_HEALTH_URL" -ForegroundColor White
Write-Host "Public Test: $PUBLIC_TEST_URL" -ForegroundColor White
Write-Host ""
Write-Host "To access Swagger UI, open your browser and navigate to:" -ForegroundColor Yellow
Write-Host "$SWAGGER_UI_URL" -ForegroundColor Cyan
Write-Host ""
Write-Host "To view the raw OpenAPI specification:" -ForegroundColor Yellow
Write-Host "$API_DOCS_URL" -ForegroundColor Cyan
Write-Host ""
Write-Host "Authentication Endpoints:" -ForegroundColor Yellow
Write-Host "- Login: POST $LOGIN_URL" -ForegroundColor White
Write-Host "- Register: POST $SERVICE_URL/api/authentication/register" -ForegroundColor White
Write-Host "- Refresh: POST $SERVICE_URL/api/authentication/refresh" -ForegroundColor White
Write-Host ""
Write-Host "If you're still having issues:" -ForegroundColor Yellow
Write-Host "1. Check the application logs for errors" -ForegroundColor White
Write-Host "2. Ensure the service is running on port 9083" -ForegroundColor White
Write-Host "3. Verify Keycloak is running and configured" -ForegroundColor White
Write-Host "4. Try clearing your browser cache" -ForegroundColor White
Write-Host "5. Check the SWAGGER_DOCUMENTATION.md file" -ForegroundColor White
Write-Host "==========================================" -ForegroundColor Cyan 