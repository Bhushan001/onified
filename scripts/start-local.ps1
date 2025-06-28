# Onified Platform Local Startup Script for Windows
# This script starts all services locally with Eureka enabled

param(
    [Parameter(Position=0)]
    [ValidateSet("start", "stop", "status", "restart")]
    [string]$Command = "start",
    
    [Parameter(Position=1)]
    [switch]$WithFrontend
)

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

# Function to check if port is available
function Test-Port {
    param([int]$Port, [string]$Service)
    
    try {
        $connection = Test-NetConnection -ComputerName localhost -Port $Port -InformationLevel Quiet
        if ($connection) {
            Write-Warning "Port $Port is already in use. $Service may not start properly."
            return $false
        } else {
            Write-Success "Port $Port is available for $Service"
            return $true
        }
    } catch {
        Write-Success "Port $Port is available for $Service"
        return $true
    }
}

# Function to wait for service to be ready
function Wait-ForService {
    param([string]$Url, [string]$Service)
    
    Write-Status "Waiting for $Service to be ready..."
    $maxAttempts = 30
    $attempt = 1
    
    while ($attempt -le $maxAttempts) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                Write-Success "$Service is ready!"
                return $true
            }
        } catch {
            # Service not ready yet
        }
        
        Write-Status "Attempt $attempt/$maxAttempts : $Service not ready yet..."
        Start-Sleep -Seconds 2
        $attempt++
    }
    
    Write-Error "$Service failed to start within expected time"
    return $false
}

# Function to start service in background
function Start-ServiceBackground {
    param([string]$ServiceDir, [string]$ServiceName, [int]$Port, [string]$HealthUrl)
    
    Write-Status "Starting $ServiceName..."
    
    # Check if port is available
    Test-Port -Port $Port -Service $ServiceName | Out-Null
    
    # Start service in background
    Push-Location $ServiceDir
    $job = Start-Job -ScriptBlock {
        param($ServiceDir, $ServiceName)
        Set-Location $ServiceDir
        mvn spring-boot:run
    } -ArgumentList $ServiceDir, $ServiceName
    
    # Save job info
    $job | Export-Clixml "../logs/$ServiceName-job.xml"
    
    Write-Success "$ServiceName started with Job ID $($job.Id)"
    
    # Wait for service to be ready
    if ($HealthUrl) {
        Wait-ForService -Url $HealthUrl -Service $ServiceName
    }
    
    Pop-Location
}

# Function to stop all services
function Stop-AllServices {
    Write-Status "Stopping all services..."
    
    if (Test-Path "logs") {
        $jobFiles = Get-ChildItem "logs" -Filter "*-job.xml"
        foreach ($jobFile in $jobFiles) {
            $job = Import-Clixml $jobFile.FullName
            $serviceName = $jobFile.BaseName -replace "-job", ""
            
            if ($job.State -eq "Running") {
                Write-Status "Stopping $serviceName (Job ID: $($job.Id))..."
                Stop-Job $job
                Remove-Job $job
            } else {
                Write-Warning "$serviceName is not running"
            }
            
            Remove-Item $jobFile.FullName
        }
    }
    
    Write-Success "All services stopped"
}

# Function to show status
function Show-ServiceStatus {
    Write-Status "Service Status:"
    
    if (Test-Path "logs") {
        $jobFiles = Get-ChildItem "logs" -Filter "*-job.xml"
        foreach ($jobFile in $jobFiles) {
            $job = Import-Clixml $jobFile.FullName
            $serviceName = $jobFile.BaseName -replace "-job", ""
            
            if ($job.State -eq "Running") {
                Write-Success "$serviceName : RUNNING (Job ID: $($job.Id))"
            } else {
                Write-Error "$serviceName : NOT RUNNING"
                Remove-Item $jobFile.FullName
            }
        }
    } else {
        Write-Warning "No service jobs found"
    }
}

# Main function
function Start-AllServices {
    Write-Status "Starting Onified Platform locally..."
    
    # Create logs directory
    if (!(Test-Path "logs")) {
        New-Item -ItemType Directory -Path "logs" | Out-Null
    }
    
    # Load environment variables
    if (Test-Path ".env") {
        Write-Status "Loading environment variables from .env file..."
        Get-Content .env | ForEach-Object {
            if ($_ -match '^([^#][^=]+)=(.*)$') {
                [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
            }
        }
    } else {
        Write-Warning ".env file not found. Using default values."
    }
    
    # Check prerequisites
    Write-Status "Checking prerequisites..."
    
    # Check Java
    try {
        $javaVersion = java -version 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Java not found"
        }
    } catch {
        Write-Error "Java is not installed or not in PATH"
        exit 1
    }
    
    # Check Maven
    try {
        $mvnVersion = mvn -version 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Maven not found"
        }
    } catch {
        Write-Error "Maven is not installed or not in PATH"
        exit 1
    }
    
    # Check PostgreSQL (optional check)
    try {
        $pgVersion = psql --version 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Warning "PostgreSQL client not found in PATH"
        }
    } catch {
        Write-Warning "PostgreSQL client not found in PATH"
    }
    
    Write-Success "Prerequisites check passed"
    
    # Check database connectivity (optional)
    try {
        $dbTest = psql -h localhost -U postgres -d auth_db -c "SELECT 1;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Database connectivity check passed"
        } else {
            Write-Warning "Cannot connect to PostgreSQL. Please ensure PostgreSQL is running and databases are created."
        }
    } catch {
        Write-Warning "Database connectivity check skipped"
    }
    
    # Start services in order
    
    # 1. Start Eureka Server
    Write-Status "=== Starting Eureka Server ==="
    Start-ServiceBackground -ServiceDir "eureka-server" -ServiceName "Eureka Server" -Port 8761 -HealthUrl "http://localhost:8761/actuator/health"
    
    # Wait a bit for Eureka to fully start
    Start-Sleep -Seconds 5
    
    # 2. Start Core Services (in parallel)
    Write-Status "=== Starting Core Services ==="
    
    # Platform Management Service
    Start-ServiceBackground -ServiceDir "platform-management-service" -ServiceName "Platform Management Service" -Port 9081 -HealthUrl "http://localhost:9081/actuator/health"
    
    # Application Config Service
    Start-ServiceBackground -ServiceDir "application-config-service" -ServiceName "Application Config Service" -Port 9082 -HealthUrl "http://localhost:9082/actuator/health"
    
    # User Management Service
    Start-ServiceBackground -ServiceDir "user-management-service" -ServiceName "User Management Service" -Port 9085 -HealthUrl "http://localhost:9085/actuator/health"
    
    # Permission Registry Service
    Start-ServiceBackground -ServiceDir "permission-registry-service" -ServiceName "Permission Registry Service" -Port 9084 -HealthUrl "http://localhost:9084/actuator/health"
    
    # Tenant Management Service
    Start-ServiceBackground -ServiceDir "tenant-management-service" -ServiceName "Tenant Management Service" -Port 9086 -HealthUrl "http://localhost:9086/actuator/health"
    
    # 3. Start Authentication Service
    Write-Status "=== Starting Authentication Service ==="
    Start-ServiceBackground -ServiceDir "authentication-service" -ServiceName "Authentication Service" -Port 9083 -HealthUrl "http://localhost:9083/actuator/health"
    
    # 4. Start API Gateway
    Write-Status "=== Starting API Gateway ==="
    Start-ServiceBackground -ServiceDir "onified-gateway" -ServiceName "API Gateway" -Port 9080 -HealthUrl "http://localhost:9080/actuator/health"
    
    # 5. Start Frontend (optional)
    if ($WithFrontend) {
        Write-Status "=== Starting Angular Frontend ==="
        if (Test-Path "web") {
            Push-Location "web"
            
            if (!(Test-Path "node_modules")) {
                Write-Status "Installing npm dependencies..."
                npm install
            }
            
            Write-Status "Starting Angular development server..."
            $frontendJob = Start-Job -ScriptBlock {
                Set-Location $args[0]
                ng serve --port 4200
            } -ArgumentList (Get-Location)
            
            $frontendJob | Export-Clixml "../logs/frontend-job.xml"
            Write-Success "Frontend started with Job ID $($frontendJob.Id)"
            
            Pop-Location
        } else {
            Write-Warning "Frontend directory not found. Skipping frontend startup."
        }
    }
    
    # Final status
    Write-Success "=== All services started successfully! ==="
    Write-Status "Service URLs:"
    Write-Host "  Eureka Server: http://localhost:8761"
    Write-Host "  API Gateway: http://localhost:9080"
    Write-Host "  Platform Management: http://localhost:9081"
    Write-Host "  Application Config: http://localhost:9082"
    Write-Host "  Authentication: http://localhost:9083"
    Write-Host "  Permission Registry: http://localhost:9084"
    Write-Host "  User Management: http://localhost:9085"
    Write-Host "  Tenant Management: http://localhost:9086"
    
    if ($WithFrontend) {
        Write-Host "  Frontend: http://localhost:4200"
    }
    
    Write-Status "Check Eureka dashboard to verify all services are registered: http://localhost:8761"
    Write-Status "Logs are available in the logs/ directory"
    
    # Create a simple health check script
    $healthCheckScript = @"
# Health check script for Windows
Write-Host "Checking service health..."
`$ports = @(8761, 9080, 9081, 9082, 9083, 9084, 9085, 9086)
foreach (`$port in `$ports) {
    Write-Host -NoNewline "Port `$port : "
    try {
        `$response = Invoke-WebRequest -Uri "http://localhost:`$port/actuator/health" -UseBasicParsing -TimeoutSec 5
        if (`$response.StatusCode -eq 200) {
            Write-Host "UP" -ForegroundColor Green
        } else {
            Write-Host "DOWN" -ForegroundColor Red
        }
    } catch {
        Write-Host "DOWN" -ForegroundColor Red
    }
}
"@
    
    $healthCheckScript | Out-File -FilePath "scripts/health-check.ps1" -Encoding UTF8
    Write-Success "Health check script created: scripts/health-check.ps1"
}

# Main script execution
switch ($Command) {
    "start" {
        Start-AllServices
    }
    "stop" {
        Stop-AllServices
    }
    "status" {
        Show-ServiceStatus
    }
    "restart" {
        Stop-AllServices
        Start-Sleep -Seconds 2
        Start-AllServices
    }
    default {
        Write-Host "Usage: .\start-local.ps1 {start|stop|status|restart} [-WithFrontend]"
        Write-Host ""
        Write-Host "Commands:"
        Write-Host "  start [-WithFrontend]     Start all services (optionally with frontend)"
        Write-Host "  stop                      Stop all services"
        Write-Host "  status                    Show status of all services"
        Write-Host "  restart [-WithFrontend]   Restart all services"
        Write-Host ""
        Write-Host "Examples:"
        Write-Host "  .\start-local.ps1 start              Start backend services only"
        Write-Host "  .\start-local.ps1 start -WithFrontend Start all services including frontend"
        Write-Host "  .\start-local.ps1 stop               Stop all services"
        Write-Host "  .\start-local.ps1 status             Check service status"
        exit 1
    }
} 