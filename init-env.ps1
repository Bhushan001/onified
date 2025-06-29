# PowerShell script to create .env from env.example

# Stop on first error
$ErrorActionPreference = "Stop"

# Check if env.example exists
if (-not (Test-Path "env.example")) {
    Write-Host "[ERROR] env.example not found!" -ForegroundColor Red
    exit 1
}

# Check if .env already exists
if (Test-Path ".env") {
    $confirm = Read-Host ".env already exists. Overwrite? (y/N)"
    if ($confirm -notmatch "^[Yy]$") {
        Write-Host "Aborted. .env not overwritten." -ForegroundColor Yellow
        exit 0
    }
}

# Copy env.example to .env
Copy-Item "env.example" ".env"

Write-Host "[SUCCESS] .env file created from env.example." -ForegroundColor Green 