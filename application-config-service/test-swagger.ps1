# Test script for Application Config Service Swagger Documentation
# This script tests the availability of Swagger endpoints

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Application Config Service - Swagger Tests" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Configuration
$SERVICE_URL = "http://localhost:9082"
$SWAGGER_UI_URL = "$SERVICE_URL/swagger-ui.html"
$API_DOCS_URL = "$SERVICE_URL/api-docs"
$HEALTH_URL = "$SERVICE_URL/actuator/health"
$PUBLIC_TEST_URL = "$SERVICE_URL/api/public/test"
$PUBLIC_HEALTH_URL = "$SERVICE_URL/api/public/health"

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
    Write-Host "   Please ensure the service is started on port 9082" -ForegroundColor Yellow
    exit 1
}

# Test 2: Public Test Endpoint
Write-Host ""
Write-Host "2. Testing Public Test Endpoint..." -ForegroundColor Green
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

# Test 3: Public Health Endpoint
Write-Host ""
Write-Host "3. Testing Public Health Endpoint..." -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri $PUBLIC_HEALTH_URL -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "   ✅ Public health endpoint available at: $PUBLIC_HEALTH_URL" -ForegroundColor Green
        Write-Host "   Response: $($response.Content)" -ForegroundColor Gray
    } else {
        Write-Host "   ❌ Public health endpoint not available - Status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "   ❌ Public health endpoint not available" -ForegroundColor Red
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

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Test Summary:" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Service URL: $SERVICE_URL" -ForegroundColor White
Write-Host "Swagger UI: $SWAGGER_UI_URL" -ForegroundColor White
Write-Host "OpenAPI Docs: $API_DOCS_URL" -ForegroundColor White
Write-Host "Public Test: $PUBLIC_TEST_URL" -ForegroundColor White
Write-Host "Public Health: $PUBLIC_HEALTH_URL" -ForegroundColor White
Write-Host ""
Write-Host "To access Swagger UI, open your browser and navigate to:" -ForegroundColor Yellow
Write-Host "$SWAGGER_UI_URL" -ForegroundColor Cyan
Write-Host ""
Write-Host "To view the raw OpenAPI specification:" -ForegroundColor Yellow
Write-Host "$API_DOCS_URL" -ForegroundColor Cyan
Write-Host ""
Write-Host "If you're still having issues:" -ForegroundColor Yellow
Write-Host "1. Check the application logs for errors" -ForegroundColor White
Write-Host "2. Ensure the service is running on port 9082" -ForegroundColor White
Write-Host "3. Try clearing your browser cache" -ForegroundColor White
Write-Host "4. Check the SWAGGER_TROUBLESHOOTING.md file" -ForegroundColor White
Write-Host "==========================================" -ForegroundColor Cyan 