# Onified Platform - Database Setup Script (PowerShell)
# This script creates all required databases in PostgreSQL container

# Set error action preference to stop on any error
$ErrorActionPreference = "Stop"

# Colors for output
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"
$Blue = "Blue"
$White = "White"

# Function to print colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor $Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor $Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor $Red
}

# Function to check if command exists
function Test-Command {
    param([string]$Command)
    try {
        Get-Command $Command -ErrorAction Stop | Out-Null
        return $true
    }
    catch {
        return $false
    }
}

# Function to check if PostgreSQL container is running
function Test-PostgresContainer {
    Write-Status "Checking if PostgreSQL container is running..."
    
    if (-not (Test-Command "docker")) {
        Write-Error "Docker is not installed or not in PATH"
        exit 1
    }
    
    try {
        $containers = docker ps --format "{{.Names}}" 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Docker command failed"
        }
        
        if (-not ($containers -split "`n" | Where-Object { $_ -eq "postgres" })) {
            Write-Error "PostgreSQL container is not running. Please start it first with: docker-compose up -d postgres"
            exit 1
        }
        
        Write-Success "PostgreSQL container is running"
    }
    catch {
        Write-Error "Failed to check PostgreSQL container: $($_.Exception.Message)"
        exit 1
    }
}

# Function to wait for PostgreSQL to be ready
function Wait-ForPostgres {
    Write-Status "Waiting for PostgreSQL to be ready..."
    
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            $result = docker exec postgres pg_isready -U postgres 2>&1
            if ($LASTEXITCODE -eq 0) {
                Write-Success "PostgreSQL is ready"
                return $true
            }
        }
        catch {
            # PostgreSQL not ready yet
        }
        
        Write-Status "Attempt $attempt/$maxAttempts : PostgreSQL not ready yet..."
        Start-Sleep -Seconds 2
        $attempt++
    }
    
    Write-Error "PostgreSQL failed to start within expected time"
    exit 1
}

# Function to create database if it doesn't exist
function New-Database {
    param(
        [string]$DatabaseName,
        [string]$Description
    )
    
    Write-Status "Creating database: $DatabaseName ($Description)"
    
    try {
        # Check if database already exists
        $existingDbs = docker exec postgres psql -U postgres -lqt 2>&1
        if ($LASTEXITCODE -eq 0 -and $existingDbs -match $DatabaseName) {
            Write-Warning "Database '$DatabaseName' already exists, skipping..."
            return $true
        }
        
        # Create database
        $result = docker exec postgres createdb -U postgres $DatabaseName 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Database '$DatabaseName' created successfully"
            return $true
        } else {
            Write-Error "Failed to create database '$DatabaseName': $result"
            return $false
        }
    }
    catch {
        Write-Error "Failed to create database '$DatabaseName': $($_.Exception.Message)"
        return $false
    }
}

# Function to create all databases
function New-AllDatabases {
    Write-Status "Creating all required databases..."
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    
    # Define databases to create
    $databases = @{
        "platform_mgmt_db" = "Platform Management Service"
        "tenant_management_db" = "Tenant Management Service"
        "user_management_db" = "User Management Service"
        "permission_registry_db" = "Permission Registry Service"
        "application_config_db" = "Application Config Service"
        "authentication_db" = "Authentication Service"
        "keycloak_db" = "Keycloak Identity Provider"
    }
    
    # Create each database
    foreach ($dbName in $databases.Keys) {
        $description = $databases[$dbName]
        if (-not (New-Database -DatabaseName $dbName -Description $description)) {
            Write-Error "Failed to create database: $dbName"
            exit 1
        }
    }
    
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    Write-Success "All databases created successfully!"
}

# Function to list all databases
function Get-Databases {
    Write-Status "Listing all databases:"
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    
    try {
        $result = docker exec postgres psql -U postgres -c "\l" 2>&1
        if ($LASTEXITCODE -eq 0) {
            $result | Select-String -Pattern "(platform_mgmt_db|tenant_management_db|user_management_db|permission_registry_db|application_config_db|authentication_db|keycloak_db)" | ForEach-Object { Write-Host $_.Line }
        }
    }
    catch {
        Write-Warning "Failed to list databases: $($_.Exception.Message)"
    }
    
    Write-Host "--------------------------------------------------" -ForegroundColor $White
}

# Function to test database connections
function Test-DatabaseConnections {
    Write-Status "Testing database connections..."
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    
    $databases = @("platform_mgmt_db", "tenant_management_db", "user_management_db", "permission_registry_db", "application_config_db", "authentication_db", "keycloak_db")
    
    foreach ($dbName in $databases) {
        try {
            $result = docker exec postgres psql -U postgres -d $dbName -c "SELECT 1;" 2>&1
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Connection to '$dbName' successful"
            } else {
                Write-Error "Connection to '$dbName' failed"
            }
        }
        catch {
            Write-Error "Connection to '$dbName' failed: $($_.Exception.Message)"
        }
    }
    
    Write-Host "--------------------------------------------------" -ForegroundColor $White
}

# Function to show usage
function Show-Usage {
    Write-Host "Usage: .\setup-databases.ps1 [COMMAND]"
    Write-Host ""
    Write-Host "Commands:"
    Write-Host "  create     Create all required databases (default)"
    Write-Host "  list       List all databases"
    Write-Host "  test       Test database connections"
    Write-Host "  all        Create, list, and test databases"
    Write-Host "  help       Show this help message"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\setup-databases.ps1 create  # Create all databases"
    Write-Host "  .\setup-databases.ps1 list    # List existing databases"
    Write-Host "  .\setup-databases.ps1 test    # Test database connections"
    Write-Host "  .\setup-databases.ps1 all     # Full setup and verification"
}

# Main function
function Main {
    param(
        [string]$Command = "create"
    )
    
    Write-Status "Starting Onified Platform database setup..."
    Write-Host "==================================================" -ForegroundColor $White
    
    switch ($Command.ToLower()) {
        "create" {
            Test-PostgresContainer
            Wait-ForPostgres
            New-AllDatabases
        }
        "list" {
            Test-PostgresContainer
            Get-Databases
        }
        "test" {
            Test-PostgresContainer
            Test-DatabaseConnections
        }
        "all" {
            Test-PostgresContainer
            Wait-ForPostgres
            New-AllDatabases
            Write-Host ""
            Get-Databases
            Write-Host ""
            Test-DatabaseConnections
        }
        "help" {
            Show-Usage
            return
        }
        default {
            Write-Error "Unknown command: $Command"
            Show-Usage
            exit 1
        }
    }
    
    Write-Host "==================================================" -ForegroundColor $White
    Write-Success "Database setup completed!"
}

# Parse command line arguments
$command = $args[0]
if (-not $command) {
    $command = "create"
}

# Run main function
Main -Command $command 