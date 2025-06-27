# Docker Setup Guide

This guide provides detailed instructions for setting up and running the Onified platform using Docker and Docker Compose.

## 📋 Prerequisites

### Required Software
- **Docker Desktop**: 20.10+ (Windows/Mac) or Docker Engine 20.10+ (Linux)
- **Docker Compose**: 2.0+
- **Git**

### System Requirements
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: Minimum 50GB available space
- **CPU**: 4+ cores recommended

### Verify Installation
```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version

# Verify Docker is running
docker info
```

## 🚀 Quick Start

### 1. Clone Repository
```bash
git clone <repository-url>
cd onified
```

### 2. Environment Setup
```bash
# Copy environment template
cp env.example .env

# Edit .env file with your configuration
nano .env
```

### 3. Start All Services
```bash
# Build and start all services
docker-compose up --build

# Or start in background
docker-compose up -d --build
```

## ⚙️ Detailed Configuration

### Environment Variables

Create a `.env` file in the project root with the following variables:

```bash
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root
POSTGRES_PORT=5433

# Database names
PLATFORM_MGMT_DB=platform_mgmt_db
TENANT_MGMT_DB=tenant_management_db
USER_MGMT_DB=user_management_db
PERMISSION_DB=permission_registry_db
APP_CONFIG_DB=application_config_db
AUTH_DB=authentication_db

# Service URLs
PLATFORM_MGMT_DB_URL=jdbc:postgresql://postgres:5432/platform_mgmt_db
TENANT_MGMT_DB_URL=jdbc:postgresql://postgres:5432/tenant_management_db
USER_MGMT_DB_URL=jdbc:postgresql://postgres:5432/user_management_db
PERMISSION_DB_URL=jdbc:postgresql://postgres:5432/permission_registry_db
APP_CONFIG_DB_URL=jdbc:postgresql://postgres:5432/application_config_db
AUTH_DB_URL=jdbc:postgresql://postgres:5432/authentication_db

# Service Ports
GATEWAY_PORT=9080
PLATFORM_MGMT_PORT=9081
APP_CONFIG_PORT=9082
AUTH_PORT=9083
PERMISSION_REGISTRY_PORT=9084
USER_MGMT_PORT=9085
TENANT_MGMT_PORT=9086
FRONTEND_PORT=4200
KEYCLOAK_PORT=9090

# Logging Directories
GATEWAY_LOG_DIR=./logs/gateway
PLATFORM_MGMT_LOG_DIR=./logs/platform-management
APP_CONFIG_LOG_DIR=./logs/application-config
AUTH_LOG_DIR=./logs/authentication
PERMISSION_REGISTRY_LOG_DIR=./logs/permission-registry
USER_MGMT_LOG_DIR=./logs/user-management
TENANT_MGMT_LOG_DIR=./logs/tenant-management
FRONTEND_LOG_DIR=./logs/frontend

# Keycloak Configuration
KEYCLOAK_DB=keycloak_db
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123
KEYCLOAK_CLIENT_SECRET=your-client-secret-here

# Eureka Configuration (enabled for Docker)
EUREKA_ENABLED=true
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

## 🏗️ Service Architecture

### Service Dependencies
```
Eureka Server
    ↓
PostgreSQL + Keycloak
    ↓
Core Services (Auth, User Mgmt, Permission, App Config, Platform Mgmt, Tenant Mgmt)
    ↓
API Gateway
    ↓
Angular Frontend
```

### Port Mappings
- **Eureka Server**: 8761
- **API Gateway**: 9080
- **Platform Management Service**: 9081
- **Application Config Service**: 9082
- **Authentication Service**: 9083
- **Permission Registry Service**: 9084
- **User Management Service**: 9085
- **Tenant Management Service**: 9086
- **Keycloak**: 9090
- **Angular Frontend**: 4200
- **PostgreSQL**: 5433 (external), 5432 (internal)

## 🔧 Docker Compose Configuration

### Service Definitions

#### 1. PostgreSQL Database
```yaml
postgres:
  image: postgres:15
  container_name: postgres
  environment:
    POSTGRES_USER: ${POSTGRES_USER:-postgres}
    POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-root}
  ports:
    - "${POSTGRES_PORT:-5433}:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
    - ./configs/init.sql:/docker-entrypoint-initdb.d/init.sql
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-postgres}"]
    interval: 10s
    timeout: 5s
    retries: 5
```

#### 2. Eureka Server
```yaml
eureka-server:
  build: ./eureka-server
  container_name: eureka-server
  ports:
    - "8761:8761"
  healthcheck:
    test: ["CMD-SHELL", "curl -sf http://localhost:8761/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 10
    start_period: 20s
```

#### 3. Keycloak
```yaml
keycloak:
  image: quay.io/keycloak/keycloak:24.0.2
  container_name: keycloak
  environment:
    KC_DB: postgres
    KC_DB_URL: jdbc:postgresql://postgres:5432/${KEYCLOAK_DB:-keycloak_db}
    KC_DB_USERNAME: ${POSTGRES_USER:-postgres}
    KC_DB_PASSWORD: ${POSTGRES_PASSWORD:-root}
    KEYCLOAK_ADMIN: ${KEYCLOAK_ADMIN:-admin}
    KEYCLOAK_ADMIN_PASSWORD: ${KEYCLOAK_ADMIN_PASSWORD:-admin123}
    KC_HOSTNAME_STRICT: false
    KC_HOSTNAME_STRICT_HTTPS: false
    KC_HTTP_ENABLED: true
  ports:
    - "${KEYCLOAK_PORT:-9090}:8080"
  command: start-dev
  depends_on:
    postgres:
      condition: service_healthy
```

## 🚀 Deployment Commands

### Basic Operations

#### Start Services
```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# Start specific services
docker-compose up postgres keycloak eureka-server
```

#### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop specific services
docker-compose stop service-name
```

#### Rebuild Services
```bash
# Rebuild all services
docker-compose build

# Rebuild specific service
docker-compose build service-name

# Rebuild and start
docker-compose up --build
```

### Advanced Operations

#### Service Management
```bash
# View running services
docker-compose ps

# View service logs
docker-compose logs -f service-name

# Execute command in container
docker-compose exec service-name bash

# Restart service
docker-compose restart service-name
```

#### Resource Management
```bash
# View resource usage
docker stats

# Clean up unused resources
docker system prune

# Remove all containers and images
docker system prune -a
```

## 📊 Monitoring and Health Checks

### Health Check Endpoints
- **Eureka Server**: http://localhost:8761
- **API Gateway**: http://localhost:9080/actuator/health
- **Authentication Service**: http://localhost:9083/actuator/health
- **User Management Service**: http://localhost:9085/actuator/health
- **Permission Registry Service**: http://localhost:9084/actuator/health
- **Application Config Service**: http://localhost:9082/actuator/health
- **Platform Management Service**: http://localhost:9081/actuator/health
- **Tenant Management Service**: http://localhost:9086/actuator/health

### Logs
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f service-name

# View logs with timestamps
docker-compose logs -f -t service-name

# View last 100 lines
docker-compose logs --tail=100 service-name
```

### Metrics
```bash
# View container metrics
docker stats

# View service metrics
curl http://localhost:9080/actuator/metrics
```

## 🔐 Security Configuration

### Keycloak Setup
1. Access Keycloak Admin Console: http://localhost:9090
2. Login with admin/admin123
3. Create realm: `onified`
4. Create clients:
   - `authentication-service` (confidential)
   - `web-app` (public)
5. Configure redirect URIs and CORS

### Network Security
```bash
# Create custom network
docker network create onified-network

# Inspect network
docker network inspect onified-network

# Connect container to network
docker network connect onified-network container-name
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check port usage
netstat -ano | findstr :9080

# Kill process using port
taskkill /PID <PID> /F
```

#### 2. Container Startup Issues
```bash
# Check container status
docker-compose ps

# View container logs
docker-compose logs service-name

# Check container health
docker inspect container-name | grep Health
```

#### 3. Database Connection Issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Test database connection
docker-compose exec postgres psql -U postgres -d onified -c "SELECT 1;"

# Check database initialization
docker-compose exec postgres psql -U postgres -l
```

#### 4. Memory Issues
```bash
# Check memory usage
docker stats

# Increase Docker memory limit
# (In Docker Desktop settings)
```

### Performance Optimization

#### 1. Resource Limits
```yaml
services:
  service-name:
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
        reservations:
          memory: 512M
          cpus: '0.25'
```

#### 2. Volume Optimization
```yaml
volumes:
  postgres_data:
    driver: local
    driver_opts:
      type: none
      o: bind
      device: /path/to/data
```

## 🔄 Backup and Recovery

### Database Backup
```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres onified > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres onified < backup.sql
```

### Volume Backup
```bash
# Backup volumes
docker run --rm -v onified_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .

# Restore volumes
docker run --rm -v onified_postgres_data:/data -v $(pwd):/backup alpine tar xzf /backup/postgres_backup.tar.gz -C /data
```

## 📈 Scaling

### Horizontal Scaling
```bash
# Scale specific service
docker-compose up --scale service-name=3

# Scale with load balancer
docker-compose up --scale user-management-service=3 --scale authentication-service=2
```

### Load Balancing
```yaml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - api-gateway
```

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Deploy to Docker
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build and deploy
        run: |
          docker-compose build
          docker-compose up -d
```

## 📚 Next Steps

1. **Production Deployment**: Configure production environment variables
2. **Monitoring**: Set up Prometheus and Grafana
3. **Logging**: Configure centralized logging with ELK stack
4. **Security**: Implement additional security measures
5. **Backup**: Set up automated backup procedures

For more detailed information about each service, refer to the individual service documentation in the `docs/services/` directory. 