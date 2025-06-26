# Local Deployment Guide

## Prerequisites
- Docker and Docker Compose
- Java 21
- Maven
- Node.js 18+ (for Angular frontend)
- Angular CLI (`npm install -g @angular/cli`)

## Quick Start

### 1. Clone and Setup
```bash
git clone <repository-url>
cd repository
```

### 2. Build and Start the Stack (Recommended)

Use the provided automation scripts to generate the .env file and start the stack for your environment:

#### Linux/macOS
```bash
./build-stack.sh local    # or dev, prod, etc.
```

#### Windows (PowerShell)
```powershell
./build-stack.ps1 local   # or dev, prod, etc.
```

This will:
- Generate the correct .env file for your chosen environment using the configs in configs/.
- Build and start the stack with docker-compose up --build.

> You can still use the setup scripts in configs/ directly if you want to only generate the .env file without starting the stack.

### 3. Access Services

All environment variables are managed via per-environment JSON config files in the `configs/` directory. Use the provided scripts to generate a `.env` file at the project root.

To generate a `.env` file for your local environment, use the setup script:

```bash
bash configs/setup-env.sh local
```

**How it works:**
- The generated `.env` file is automatically used by Docker Compose for all services.
- All Spring Boot services are configured (in their `application.yml`) to import environment variables from `.env` at startup.
- Dockerfiles do not need to reference `.env` directly; configuration is injected at runtime by Docker Compose.

> **Note:** No manual changes are needed in Dockerfiles or `application.yml` as long as `.env` is generated at the project root. The `configs/` directory is gitignored and should not be committed to version control.

**Required .env file contents:**
```bash
# Database Configuration
POSTGRES_USER=your_postgres_username
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=onified

# Keycloak Database Configuration
KEYCLOAK_DB_NAME=keycloak
KEYCLOAK_DB_USER=keycloak
KEYCLOAK_DB_PASSWORD=keycloak

# Keycloak Configuration
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123
KEYCLOAK_REALM=onified
KEYCLOAK_CLIENT_ID=onified-auth-service
KEYCLOAK_CLIENT_SECRET=your-client-secret

# Service Ports (optional - these are the defaults)
AUTH_SERVICE_PORT=9083
USER_MANAGEMENT_SERVICE_PORT=9083
APPLICATION_CONFIG_SERVICE_PORT=9082
PERMISSION_REGISTRY_SERVICE_PORT=9084
GATEWAY_PORT=9080
KEYCLOAK_PORT=9090
FRONTEND_PORT=4200

# Log Directory Configuration (optional - these are the defaults)
GATEWAY_LOG_DIR=./logs/gateway
APP_CONFIG_LOG_DIR=./logs/app-config
AUTH_LOG_DIR=./logs/auth-service
PERMISSION_LOG_DIR=./logs/permission-service
USER_MGMT_LOG_DIR=./logs/user-management
FRONTEND_LOG_DIR=./logs/frontend
```

### 4. Setup Log Directories
```bash
# Create log directories for all services
chmod +x setup-logs.sh
./setup-logs.sh

# Or manually create directories
mkdir -p logs/{gateway,app-config,auth-service,permission-service,user-management,frontend}
```

### 5. Start Keycloak (Identity Provider)
```bash
# Start Keycloak and its database
docker-compose up -d keycloak keycloak-db

# Wait for Keycloak to be ready (check logs)
docker-compose logs -f keycloak
```

### 6. Start All Services
```bash
# Start all microservices
docker-compose up -d

# Check service status
docker-compose ps
```

### 7. Start Angular Frontend (Development)
```bash
cd web
npm install
ng serve
```

## Service Architecture

### Port Mappings
- **API Gateway**: http://localhost:9080 (configurable via `GATEWAY_PORT`)
- **Eureka Server**: http://localhost:9081 (configurable via `EUREKA_SERVER_PORT`)
- **Application Config Service**: http://localhost:9082 (configurable via `APPLICATION_CONFIG_SERVICE_PORT`)
- **Authentication Service**: http://localhost:9083 (configurable via `AUTH_SERVICE_PORT`)
- **Permission Registry Service**: http://localhost:9084 (configurable via `PERMISSION_REGISTRY_SERVICE_PORT`)
- **User Management Service**: http://localhost:9085 (configurable via `USER_MANAGEMENT_SERVICE_PORT`)
- **Keycloak**: http://localhost:9086 (configurable via `KEYCLOAK_PORT`)
- **Angular Frontend**: http://localhost:4200 (configurable via `FRONTEND_PORT`)
- **PostgreSQL**: localhost:5432
- **Keycloak DB**: localhost:5433

### Service Dependencies
```
Frontend (4200) → API Gateway (9080) → Microservices
Keycloak (9090) ← Authentication Service (9083)
Eureka (8761) ← All Microservices
PostgreSQL (5432) ← All Microservices
```

## Configuration

### Environment Variables
All services now use environment variables for configuration. Key variables in your `.env` file:

```bash
# Database Configuration (used by all Spring Boot services)
POSTGRES_USER=your_postgres_username
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=onified

# Keycloak Configuration
KEYCLOAK_REALM=onified
KEYCLOAK_CLIENT_ID=onified-auth-service
KEYCLOAK_CLIENT_SECRET=your-client-secret

# Service Ports (all configurable)
AUTH_SERVICE_PORT=9083
USER_MANAGEMENT_SERVICE_PORT=9083
APPLICATION_CONFIG_SERVICE_PORT=9082
PERMISSION_REGISTRY_SERVICE_PORT=9084
GATEWAY_PORT=9080
KEYCLOAK_PORT=9090
FRONTEND_PORT=4200
```

**Security Note:** The `.env` file is automatically ignored by git and should never be committed to the repository.

### Keycloak Setup
Follow the detailed guide in `KEYCLOAK_SETUP.md` for:
- Realm creation
- Client configuration
- User management
- Role assignment

## Log Management

### Log Directory Structure
Each service has its own configurable log directory:

```
logs/
├── gateway/           # API Gateway logs
├── app-config/        # Application Config Service logs
├── auth-service/      # Authentication Service logs
├── permission-service/ # Permission Registry Service logs
├── user-management/   # User Management Service logs
└── frontend/          # Angular Frontend logs
```

### Log Configuration
- **Log Rotation**: 10MB max file size, 30 days retention
- **Total Size Cap**: 1GB per service
- **Log Format**: Timestamp with message
- **Log Levels**: Configurable per service

### Viewing Logs

#### Docker Compose Logs
```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f authentication-service
docker-compose logs -f user-management-service
```

#### File System Logs
```bash
# View log files directly
tail -f logs/auth-service/authentication-service.log
tail -f logs/user-management/user-management-service.log

# Search logs
grep "ERROR" logs/auth-service/authentication-service.log
grep "WARN" logs/*/*.log
```

#### Log Cleanup
```bash
# Clean old log files
find logs/ -name "*.log.*" -mtime +30 -delete

# Check log disk usage
du -sh logs/
```

## Development Workflow

### Single Service Updates

#### 1. Backend Service Updates (Java/Maven)

**Rebuild and Restart Single Service:**
```bash
# Stop the specific service
docker-compose stop authentication-service

# Rebuild the service with latest code
docker-compose build authentication-service

# Start the service
docker-compose up -d authentication-service

# Check logs
docker-compose logs -f authentication-service
```

**Quick Restart (No Code Changes):**
```bash
# Restart service without rebuilding
docker-compose restart authentication-service

# Check logs
docker-compose logs -f authentication-service
```

**Force Rebuild (Clean Build):**
```bash
# Remove old image and rebuild
docker-compose stop authentication-service
docker rmi repository_authentication-service
docker-compose build --no-cache authentication-service
docker-compose up -d authentication-service
```

#### 2. Frontend Updates (Angular)

**Development Mode (Recommended for Development):**
```bash
# Stop the containerized frontend
docker-compose stop onified-frontend

# Start Angular dev server
cd web
ng serve

# Access at http://localhost:4200 (with hot reload)
```

**Production Build and Deploy:**
```bash
# Build the Angular app
cd web
ng build --configuration production

# Rebuild and restart the container
docker-compose stop onified-frontend
docker-compose build onified-frontend
docker-compose up -d onified-frontend
```

**Quick Frontend Rebuild:**
```bash
# Rebuild frontend container
docker-compose build onified-frontend
docker-compose up -d onified-frontend
```

#### 3. Database Changes

**Reset Database:**
```bash
# Stop all services
docker-compose down

# Remove volumes (WARNING: This deletes all data)
docker-compose down -v

# Start fresh
docker-compose up -d postgres keycloak-db
docker-compose up -d
```

**Database Migrations:**
```bash
# Restart services to apply schema changes
docker-compose restart authentication-service
docker-compose restart application-config-service
docker-compose restart user-management-service
docker-compose restart permission-registry-service
```

### Service-Specific Commands

#### Authentication Service
```bash
# Update authentication service
docker-compose build authentication-service
docker-compose up -d authentication-service

# Check authentication logs
docker-compose logs -f authentication-service

# Test authentication endpoint
curl http://localhost:9083/api/auth/health
```

#### Application Config Service
```bash
# Update application config service
docker-compose build application-config-service
docker-compose up -d application-config-service

# Check config service logs
docker-compose logs -f application-config-service
```

#### User Management Service
```bash
# Update user management service
docker-compose build user-management-service
docker-compose up -d user-management-service

# Check user management logs
docker-compose logs -f user-management-service
```

#### Permission Registry Service
```bash
# Update permission registry service
docker-compose build permission-registry-service
docker-compose up -d permission-registry-service

# Check permission registry logs
docker-compose logs -f permission-registry-service
```

#### API Gateway
```bash
# Update gateway
docker-compose build onified-gateway
docker-compose up -d onified-gateway

# Check gateway logs
docker-compose logs -f onified-gateway

# Test gateway health
curl http://localhost:9080/actuator/health
```

#### Frontend (Angular)
```bash
# Development mode (with hot reload)
cd web
ng serve

# Production build and deploy
cd web
ng build --configuration production
docker-compose build onified-frontend
docker-compose up -d onified-frontend
```

### Complete System Updates

#### Full System Rebuild
```bash
# Stop all services
docker-compose down

# Rebuild all services
docker-compose build --no-cache

# Start all services
docker-compose up -d

# Check all services
docker-compose ps
```

#### Selective Rebuild
```bash
# Rebuild specific services
docker-compose build authentication-service onified-gateway onified-frontend

# Restart rebuilt services
docker-compose up -d authentication-service onified-gateway onified-frontend
```

## Testing

### 1. Health Checks
```bash
# API Gateway
curl http://localhost:9080/actuator/health

# Authentication Service
curl http://localhost:9083/api/auth/health

# Eureka Server
curl http://localhost:8761/actuator/health

# Frontend
curl http://localhost:4200/health
```

### 2. Authentication Test
```bash
# Login with Keycloak user
curl -X POST http://localhost:9083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 3. Service Discovery
- Eureka Dashboard: http://localhost:8761
- Check registered services

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   - Check if ports are already in use
   - Modify ports in `docker-compose.yml` if needed

2. **Keycloak Connection Issues**
   - Ensure Keycloak is fully started
   - Check client secret configuration
   - Verify realm name matches

3. **Database Connection Issues**
   - Check PostgreSQL is running
   - Verify database credentials
   - Check network connectivity

4. **Service Discovery Issues**
   - Ensure Eureka server is running
   - Check service registration
   - Verify network configuration

5. **Build Failures**
   - Clear Docker cache: `docker system prune -a`