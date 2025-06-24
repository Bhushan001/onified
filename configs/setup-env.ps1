# setup-env.ps1
# Usage: .\setup-env.ps1 [env]
# Example: .\setup-env.ps1 local
# This script reads configs/config.<env>.json and writes all variables to a .env file

param(
    [string]$env = "local"
)

$ConfigFile = "configs/config.$env.json"
$EnvFile = ".env"

if (!(Test-Path $ConfigFile)) {
    Write-Host "Error: $ConfigFile not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Generating .env file from $ConfigFile ..."

# Read and parse JSON
$json = Get-Content $ConfigFile -Raw | ConvertFrom-Json

# Write header
"# Auto-generated .env file from $ConfigFile" | Out-File $EnvFile -Encoding utf8

# Flatten and write all key-value pairs
foreach ($section in $json.PSObject.Properties.Value) {
    foreach ($pair in $section.PSObject.Properties) {
        "$($pair.Name)=$($pair.Value)" | Out-File $EnvFile -Append -Encoding utf8
    }
}

Write-Host ".env file generated successfully from $ConfigFile." -ForegroundColor Green 