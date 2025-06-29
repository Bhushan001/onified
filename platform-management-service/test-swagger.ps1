# Test Swagger/OpenAPI Documentation for Platform Management Service
# This script tests the Swagger endpoints to ensure they are working correctly

param(
    [string]$ServiceUrl = "http://localhost:9081",
    [switch]$Verbose
)

Write-Host "🔍 Testing Platform Management Service Swagger/OpenAPI Documentation" -ForegroundColor Cyan
Write-Host "Service URL: $ServiceUrl" -ForegroundColor Yellow
Write-Host ""

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Url,
        [string]$Description,
        [string]$Method = "GET"
    )
    
    try {
        Write-Host "Testing: $Description" -ForegroundColor White
        Write-Host "  URL: $Method $Url" -ForegroundColor Gray
        
        $response = Invoke-WebRequest -Uri $Url -Method $Method -UseBasicParsing -TimeoutSec 10
        
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✅ Success (Status: $($response.StatusCode))" -ForegroundColor Green
            if ($Verbose) {
                Write-Host "  Response Length: $($response.Content.Length) characters" -ForegroundColor Gray
            }
        } else {
            Write-Host "  ⚠️  Unexpected Status: $($response.StatusCode)" -ForegroundColor Yellow
        }
    }
    catch {
        Write-Host "  ❌ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Test public endpoint first
Write-Host "📋 Testing Public Endpoints" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/api/public/test" -Description "Public Test Endpoint"

# Test Swagger UI
Write-Host "📋 Testing Swagger UI" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/swagger-ui.html" -Description "Swagger UI Interface"

# Test OpenAPI JSON specification
Write-Host "📋 Testing OpenAPI Specifications" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/api-docs" -Description "OpenAPI JSON Specification"
Test-Endpoint -Url "$ServiceUrl/api-docs.yaml" -Description "OpenAPI YAML Specification"

# Test API endpoints (these might require authentication)
Write-Host "📋 Testing API Endpoints (may require authentication)" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/api/password-policies" -Description "Get All Password Policies"
Test-Endpoint -Url "$ServiceUrl/api/tenants" -Description "Get All Tenants"

# Test health endpoint
Write-Host "📋 Testing Health Endpoint" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/actuator/health" -Description "Service Health Check"

# Test security configuration
Write-Host "🔐 Testing Security Configuration" -ForegroundColor Magenta
Test-Endpoint -Url "$ServiceUrl/actuator/info" -Description "Actuator Info Endpoint"

Write-Host "🎯 Swagger Documentation Test Complete!" -ForegroundColor Cyan
Write-Host ""
Write-Host "📖 To access the interactive Swagger UI:" -ForegroundColor Yellow
Write-Host "   Open your browser and navigate to: $ServiceUrl/swagger-ui.html" -ForegroundColor White
Write-Host ""
Write-Host "📄 To get the OpenAPI specification:" -ForegroundColor Yellow
Write-Host "   JSON: $ServiceUrl/api-docs" -ForegroundColor White
Write-Host "   YAML: $ServiceUrl/api-docs.yaml" -ForegroundColor White
Write-Host ""
Write-Host "🔧 For more information, see: platform-management-service/SWAGGER_README.md" -ForegroundColor Yellow
Write-Host "🔐 Security configuration: platform-management-service/SWAGGER_SECURITY_CONFIG.md" -ForegroundColor Yellow 