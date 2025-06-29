# Database Setup for Onified Platform

This directory contains scripts to set up all required databases for the Onified Platform.

## Required Databases

The following databases are created for each service:

| Database Name | Service | Description |
|---------------|---------|-------------|
| `platform_mgmt_db` | Platform Management Service | Core platform configuration and management |
| `tenant_management_db` | Tenant Management Service | Multi-tenancy support and tenant data |
| `user_management_db` | User Management Service | User accounts and profiles |
| `permission_registry_db` | Permission Registry Service | Role-based access control and permissions |
| `application_config_db` | Application Config Service | Application configuration and settings |
| `authentication_db` | Authentication Service | Authentication and session management |
| `keycloak_db` | Keycloak Identity Provider | Identity and access management |

## Setup Methods

### Method 1: Automatic Setup (Recommended)

The databases are automatically created when the PostgreSQL container starts using the SQL init script:

```bash
# Start PostgreSQL container
docker-compose up -d postgres
```

The `configs/init.sql/01-create-databases.sql` script will be automatically executed.

### Method 2: Manual Setup Scripts

#### For Linux/macOS (Bash)

```bash
# Make script executable (if not already)
chmod +x configs/setup-databases.sh

# Create all databases
./configs/setup-databases.sh create

# List existing databases
./configs/setup-databases.sh list

# Test database connections
./configs/setup-databases.sh test

# Full setup and verification
./configs/setup-databases.sh all

# Show help
./configs/setup-databases.sh help
```

#### For Windows (PowerShell)

```powershell
# Create all databases
.\configs\setup-databases.ps1 create

# List existing databases
.\configs\setup-databases.ps1 list

# Test database connections
.\configs\setup-databases.ps1 test

# Full setup and verification
.\configs\setup-databases.ps1 all

# Show help
.\configs\setup-databases.ps1 help
```

## Prerequisites

1. **Docker and Docker Compose** must be installed
2. **PostgreSQL container** must be running
3. **Environment variables** should be configured (see `env.example`)

## Environment Configuration

Make sure your `.env` file contains the correct database configuration:

```bash
# PostgreSQL base credentials
POSTGRES_HOST=postgres
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root

# Database names for each service
PLATFORM_MGMT_DB=platform_mgmt_db
TENANT_MGMT_DB=tenant_management_db
USER_MGMT_DB=user_management_db
PERMISSION_DB=permission_registry_db
APP_CONFIG_DB=application_config_db
AUTH_DB=authentication_db
KEYCLOAK_DB=keycloak_db
```

## Verification

After setup, you can verify the databases are created correctly:

### Using Docker

```bash
# Connect to PostgreSQL container
docker exec -it postgres psql -U postgres

# List all databases
\l

# Test connection to a specific database
\c platform_mgmt_db
SELECT 1;
```

### Using the Setup Scripts

```bash
# Test all database connections
./configs/setup-databases.sh test
```

## Troubleshooting

### PostgreSQL Container Not Running

```bash
# Start PostgreSQL container
docker-compose up -d postgres

# Check container status
docker ps | grep postgres
```

### Permission Issues

```bash
# Make script executable
chmod +x configs/setup-databases.sh
```

### Database Already Exists

The scripts are designed to handle existing databases gracefully. If a database already exists, it will be skipped with a warning message.

### Connection Issues

1. Ensure PostgreSQL container is running and healthy
2. Check that the container name is `postgres`
3. Verify environment variables are correct
4. Wait for PostgreSQL to be fully ready (scripts include automatic waiting)

## Manual Database Creation

If you prefer to create databases manually:

```bash
# Connect to PostgreSQL
docker exec -it postgres psql -U postgres

# Create each database
CREATE DATABASE platform_mgmt_db;
CREATE DATABASE tenant_management_db;
CREATE DATABASE user_management_db;
CREATE DATABASE permission_registry_db;
CREATE DATABASE application_config_db;
CREATE DATABASE authentication_db;
CREATE DATABASE keycloak_db;

# Verify creation
\l
```

## Files

- `setup-databases.sh` - Bash script for Linux/macOS
- `setup-databases.ps1` - PowerShell script for Windows
- `init.sql/01-create-databases.sql` - SQL script for automatic setup
- `README-DATABASE-SETUP.md` - This documentation file 