# build-stack.ps1
# Usage: ./build-stack.ps1 [env]
# Example: ./build-stack.ps1 dev
# This script generates .env for the specified environment and builds the stack

param([string]$env = "local")
Write-Host "Generating .env for environment: $env"
Push-Location configs
./setup-env.ps1 $env
Pop-Location
Write-Host "Bringing up the stack with docker-compose..."
docker-compose up --build 