# Permission Registry Service - Swagger Test Script (PowerShell)
# This script tests the Swagger endpoints to verify the documentation is working correctly

param(
    [string]$ServiceUrl = "http://localhost:9084"
)

# Configuration
$SERVICE_NAME = "Permission Registry Service"

# Function to print colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Function to test endpoint
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Endpoint,
        [string]$Data = $null,
        [int]$ExpectedStatus,
        [string]$Description
    )
    
    Write-Status "Testing $Description..."
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
        }
        
        if ($Data) {
            $response = Invoke-RestMethod -Uri "$ServiceUrl$Endpoint" -Method $Method -Headers $headers -Body $Data -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri "$ServiceUrl$Endpoint" -Method $Method -Headers $headers -ErrorAction Stop
        }
        
        Write-Success "$Description - Status: 200"
        $responseJson = $response | ConvertTo-Json -Depth 3
        if ($responseJson.Length -gt 200) {
            Write-Host "Response: $($responseJson.Substring(0, 200))..." -ForegroundColor Gray
        } else {
            Write-Host "Response: $responseJson" -ForegroundColor Gray
        }
        Write-Host ""
        
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq $ExpectedStatus) {
            Write-Success "$Description - Status: $statusCode (Expected error)"
        } else {
            Write-Error "$Description - Expected: $ExpectedStatus, Got: $statusCode"
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        }
        Write-Host ""
    }
}

# Function to check if service is running
function Test-ServiceRunning {
    Write-Status "Checking if $SERVICE_NAME is running..."
    
    try {
        $response = Invoke-RestMethod -Uri "$ServiceUrl/actuator/health" -Method GET -ErrorAction Stop
        Write-Success "$SERVICE_NAME is running"
        return $true
    } catch {
        Write-Error "$SERVICE_NAME is not running or not accessible at $ServiceUrl"
        Write-Warning "Please start the service and try again"
        return $false
    }
}

# Function to test Swagger UI accessibility
function Test-SwaggerUI {
    Write-Status "Testing Swagger UI accessibility..."
    
    try {
        $response = Invoke-WebRequest -Uri "$ServiceUrl/swagger-ui.html" -Method GET -ErrorAction Stop
        if ($response.Content -match "Swagger UI") {
            Write-Success "Swagger UI is accessible"
            Write-Host "URL: $ServiceUrl/swagger-ui.html" -ForegroundColor Cyan
        } else {
            Write-Error "Swagger UI is not accessible"
        }
    } catch {
        Write-Error "Swagger UI is not accessible: $($_.Exception.Message)"
    }
    Write-Host ""
}

# Function to test OpenAPI documentation
function Test-OpenAPIDocs {
    Write-Status "Testing OpenAPI documentation..."
    
    try {
        $response = Invoke-WebRequest -Uri "$ServiceUrl/api-docs" -Method GET -ErrorAction Stop
        if ($response.Content -match "openapi") {
            Write-Success "OpenAPI JSON documentation is accessible"
            Write-Host "URL: $ServiceUrl/api-docs" -ForegroundColor Cyan
        } else {
            Write-Error "OpenAPI JSON documentation is not accessible"
        }
    } catch {
        Write-Error "OpenAPI JSON documentation is not accessible: $($_.Exception.Message)"
    }
    
    try {
        $response = Invoke-WebRequest -Uri "$ServiceUrl/api-docs.yaml" -Method GET -ErrorAction Stop
        if ($response.Content -match "openapi") {
            Write-Success "OpenAPI YAML documentation is accessible"
            Write-Host "URL: $ServiceUrl/api-docs.yaml" -ForegroundColor Cyan
        } else {
            Write-Error "OpenAPI YAML documentation is not accessible"
        }
    } catch {
        Write-Error "OpenAPI YAML documentation is not accessible: $($_.Exception.Message)"
    }
    Write-Host ""
}

# Main test execution
function Main {
    Write-Host "==========================================" -ForegroundColor White
    Write-Host "  $SERVICE_NAME - Swagger Test Suite" -ForegroundColor White
    Write-Host "==========================================" -ForegroundColor White
    Write-Host ""
    
    # Check if service is running
    if (-not (Test-ServiceRunning)) {
        exit 1
    }
    
    # Test Swagger UI and OpenAPI docs
    Test-SwaggerUI
    Test-OpenAPIDocs
    
    # Test public endpoints
    Write-Status "Testing public endpoints..."
    Write-Host ""
    
    Test-Endpoint -Method "GET" -Endpoint "/api/public/health" -ExpectedStatus 200 -Description "Health check endpoint"
    Test-Endpoint -Method "GET" -Endpoint "/api/public/info" -ExpectedStatus 200 -Description "Service information endpoint"
    Test-Endpoint -Method "POST" -Endpoint "/api/public/echo" -Data '{"test": "data"}' -ExpectedStatus 200 -Description "Echo endpoint"
    
    # Test role management endpoints
    Write-Status "Testing role management endpoints..."
    Write-Host ""
    
    # Create a test role
    $roleData = @{
        roleId = "TEST_ROLE"
        displayName = "Test Role"
        appCode = "TEST_APP"
        moduleCode = "TEST_MODULE"
        roleFunction = "TEST_FUNCTION"
        isActive = $true
        tenantCustomizable = $false
    } | ConvertTo-Json
    
    Test-Endpoint -Method "POST" -Endpoint "/api/roles" -Data $roleData -ExpectedStatus 201 -Description "Create role"
    
    # Get all roles
    Test-Endpoint -Method "GET" -Endpoint "/api/roles" -ExpectedStatus 200 -Description "Get all roles"
    
    # Get specific role
    Test-Endpoint -Method "GET" -Endpoint "/api/roles/TEST_ROLE" -ExpectedStatus 200 -Description "Get role by ID"
    
    # Update role
    $updateRoleData = @{
        roleId = "TEST_ROLE"
        displayName = "Updated Test Role"
        appCode = "TEST_APP"
        moduleCode = "TEST_MODULE"
        roleFunction = "TEST_FUNCTION"
        isActive = $true
        tenantCustomizable = $true
    } | ConvertTo-Json
    
    Test-Endpoint -Method "PUT" -Endpoint "/api/roles/TEST_ROLE" -Data $updateRoleData -ExpectedStatus 200 -Description "Update role"
    
    # Test PBU endpoints
    Write-Status "Testing Permission Bundle Unit endpoints..."
    Write-Host ""
    
    # Create a test PBU
    $pbuData = @{
        pbuId = "TEST_PBU"
        displayName = "Test Permission"
        apiEndpoint = "/api/test"
        actionCode = "TEST"
        scopeCode = "TEST_SCOPE"
        isActive = $true
        version = "1.0"
    } | ConvertTo-Json
    
    Test-Endpoint -Method "POST" -Endpoint "/api/pbus" -Data $pbuData -ExpectedStatus 201 -Description "Create PBU"
    
    # Get all PBUs
    Test-Endpoint -Method "GET" -Endpoint "/api/pbus" -ExpectedStatus 200 -Description "Get all PBUs"
    
    # Get specific PBU
    Test-Endpoint -Method "GET" -Endpoint "/api/pbus/TEST_PBU" -ExpectedStatus 200 -Description "Get PBU by ID"
    
    # Test error cases
    Write-Status "Testing error cases..."
    Write-Host ""
    
    Test-Endpoint -Method "GET" -Endpoint "/api/roles/NONEXISTENT_ROLE" -ExpectedStatus 404 -Description "Get non-existent role"
    Test-Endpoint -Method "GET" -Endpoint "/api/pbus/NONEXISTENT_PBU" -ExpectedStatus 404 -Description "Get non-existent PBU"
    
    # Cleanup test data
    Write-Status "Cleaning up test data..."
    Write-Host ""
    
    Test-Endpoint -Method "DELETE" -Endpoint "/api/roles/TEST_ROLE" -ExpectedStatus 204 -Description "Delete test role"
    Test-Endpoint -Method "DELETE" -Endpoint "/api/pbus/TEST_PBU" -ExpectedStatus 204 -Description "Delete test PBU"
    
    Write-Host "==========================================" -ForegroundColor White
    Write-Success "All tests completed!"
    Write-Host "==========================================" -ForegroundColor White
    Write-Host ""
    Write-Status "Swagger UI is available at: $ServiceUrl/swagger-ui.html"
    Write-Status "OpenAPI docs are available at: $ServiceUrl/api-docs"
    Write-Host ""
}

# Run main function
Main 