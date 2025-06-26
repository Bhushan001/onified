# setup-logs.ps1
# Usage: .\setup-logs.ps1
# This script creates log directories for all services (Windows PowerShell version)

$dirs = @(
    "../logs/gateway",
    "../logs/app-config",
    "../logs/auth-service",
    "../logs/permission-service",
    "../logs/user-management",
    "../logs/frontend"
)

Write-Host "Setting up log directories for Onified Platform..."

foreach ($dir in $dirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
        Write-Host "✓ Created: $dir"
    } else {
        Write-Host "Exists: $dir"
    }
}

Write-Host ""
Write-Host "Log directories setup complete!"
Write-Host ""
Write-Host "You can customize log directories by editing your .env file:"
Write-Host "  GATEWAY_LOG_DIR=./logs/gateway"
Write-Host "  APP_CONFIG_LOG_DIR=./logs/app-config"
Write-Host "  AUTH_LOG_DIR=./logs/auth-service"
Write-Host "  PERMISSION_LOG_DIR=./logs/permission-service"
Write-Host "  USER_MGMT_LOG_DIR=./logs/user-management"
Write-Host "  FRONTEND_LOG_DIR=./logs/frontend"
Write-Host ""
Write-Host "Log files will be available at:"
Write-Host "  - Gateway: ./logs/gateway/gateway.log"
Write-Host "  - App Config: ./logs/app-config/application-config-service.log"
Write-Host "  - Auth Service: ./logs/auth-service/authentication-service.log"
Write-Host "  - Permission Service: ./logs/permission-service/permission-registry-service.log"
Write-Host "  - User Management: ./logs/user-management/user-management-service.log"
Write-Host "  - Frontend: ./logs/frontend/frontend.log" 