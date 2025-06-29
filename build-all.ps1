# Onified Platform - Build All Services Script (PowerShell)
# This script builds all Docker images for the Onified platform

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

# Check prerequisites
function Test-Prerequisites {
    Write-Status "Checking prerequisites..."
    
    if (-not (Test-Command "docker")) {
        Write-Error "Docker is not installed or not in PATH"
        exit 1
    }
    
    if (-not (Test-Command "mvn")) {
        Write-Error "Maven is not installed or not in PATH"
        exit 1
    }
    
    Write-Success "Prerequisites check passed"
}

# Function to build Java service
function Build-JavaService {
    param(
        [string]$ServiceName,
        [string]$ServiceDir
    )
    
    Write-Status "Building Java service: $ServiceName"
    
    if (-not (Test-Path "$ServiceDir\pom.xml")) {
        Write-Warning "No pom.xml found in $ServiceDir, skipping Maven build"
        return $true
    }
    
    # Build JAR file
    Write-Status "Running Maven build for $ServiceName..."
    try {
        Push-Location $ServiceDir
        mvn clean package -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Maven build completed for $ServiceName"
            return $true
        } else {
            Write-Error "Maven build failed for $ServiceName"
            return $false
        }
    }
    catch {
        Write-Error "Maven build failed for $ServiceName`: $($_.Exception.Message)"
        return $false
    }
    finally {
        Pop-Location
    }
}

# Function to build Docker image
function Build-DockerImage {
    param(
        [string]$ServiceName,
        [string]$ServiceDir
    )
    
    Write-Status "Building Docker image for: $ServiceName"
    
    if (-not (Test-Path "$ServiceDir\Dockerfile")) {
        Write-Warning "No Dockerfile found in $ServiceDir, skipping Docker build"
        return $true
    }
    
    # Build Docker image
    try {
        docker build -t $ServiceName $ServiceDir
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Docker image built successfully: $ServiceName"
            return $true
        } else {
            Write-Error "Docker build failed for $ServiceName"
            return $false
        }
    }
    catch {
        Write-Error "Docker build failed for $ServiceName`: $($_.Exception.Message)"
        return $false
    }
}

# Main build process
function Main {
    Write-Status "Starting Onified Platform build process..."
    Write-Host "==================================================" -ForegroundColor $White
    
    # Check prerequisites
    Test-Prerequisites
    
    # Define services to build
    $ServiceNames = @(
        "onified-gateway",
        "platform-management-service",
        "authentication-service",
        "user-management-service",
        "permission-registry-service",
        "application-config-service",
        "tenant-management-service",
        "onified-frontend"
    )
    $ServiceDirs = @(
        ".\onified-gateway",
        ".\platform-management-service",
        ".\authentication-service",
        ".\user-management-service",
        ".\permission-registry-service",
        ".\application-config-service",
        ".\tenant-management-service",
        ".\web"
    )
    
    # Build Java services first (JAR files)
    Write-Status "Building Java services (JAR files)..."
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    
    for ($i = 0; $i -lt $ServiceNames.Length; $i++) {
        $serviceName = $ServiceNames[$i]
        $serviceDir = $ServiceDirs[$i]
        
        # Skip frontend for Maven build
        if ($serviceName -ne "onified-frontend") {
            if (-not (Build-JavaService -ServiceName $serviceName -ServiceDir $serviceDir)) {
                Write-Error "Failed to build Java service: $serviceName"
                exit 1
            }
        }
    }
    
    # Build Docker images
    Write-Status "Building Docker images..."
    Write-Host "--------------------------------------------------" -ForegroundColor $White
    
    for ($i = 0; $i -lt $ServiceNames.Length; $i++) {
        $serviceName = $ServiceNames[$i]
        $serviceDir = $ServiceDirs[$i]
        
        if (-not (Build-DockerImage -ServiceName $serviceName -ServiceDir $serviceDir)) {
            Write-Error "Failed to build Docker image: $serviceName"
            exit 1
        }
    }
    
    Write-Host "==================================================" -ForegroundColor $White
    Write-Success "All builds completed successfully!"
    Write-Status "You can now run: docker-compose up -d"
}

# Run main function
Main 