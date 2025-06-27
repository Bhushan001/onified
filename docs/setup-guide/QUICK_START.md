# Quick Start Guide - Local Setup with Eureka

This guide provides a quick way to get the Onified platform running locally with Eureka service discovery enabled.

## Prerequisites

- **Java 21**
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Node.js 18+** (for frontend)

## Quick Setup

### 1. Database Setup

```bash
# Connect to PostgreSQL
psql -U postgres

# Create databases
CREATE DATABASE eureka_db;
CREATE DATABASE auth_db;
CREATE DATABASE user_mgmt_db;
CREATE DATABASE permission_db;
CREATE DATABASE app_config_db;
CREATE DATABASE platform_mgmt_db;
CREATE DATABASE tenant_mgmt_db;
CREATE DATABASE keycloak_db;

# Exit
\q
```

### 2. Environment Configuration

Create `.env` file in the root directory:

```bash
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root
POSTGRES_PORT=5432

# Eureka Configuration
EUREKA_ENABLED=true
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# Service Ports
EUREKA_PORT=8761
GATEWAY_PORT=9080
PLATFORM_MGMT_PORT=9081
APP_CONFIG_PORT=9082
AUTH_PORT=9083
PERMISSION_REGISTRY_PORT=9084
USER_MGMT_PORT=9085
TENANT_MGMT_PORT=9086

# Database URLs
PLATFORM_MGMT_DB_URL=jdbc:postgresql://localhost:5432/platform_mgmt_db
APP_CONFIG_DB_URL=jdbc:postgresql://localhost:5432/app_config_db
AUTH_DB_URL=jdbc:postgresql://localhost:5432/auth_db
PERMISSION_DB_URL=jdbc:postgresql://localhost:5432/permission_db
USER_MGMT_DB_URL=jdbc:postgresql://localhost:5432/user_mgmt_db
TENANT_MGMT_DB_URL=jdbc:postgresql://localhost:5432/tenant_mgmt_db

# Database Credentials
PLATFORM_MGMT_DB_USERNAME=postgres
APP_CONFIG_DB_USERNAME=postgres
AUTH_DB_USERNAME=postgres
PERMISSION_DB_USERNAME=postgres
USER_MGMT_DB_USERNAME=postgres
TENANT_MGMT_DB_USERNAME=postgres

PLATFORM_MGMT_DB_PASSWORD=root
APP_CONFIG_DB_PASSWORD=root
AUTH_DB_PASSWORD=root
PERMISSION_DB_PASSWORD=root
USER_MGMT_DB_PASSWORD=root
TENANT_MGMT_DB_PASSWORD=root
```

### 3. Start Services

#### Option A: Using Startup Scripts

**macOS/Linux:**
```bash
# Make script executable
chmod +x scripts/start-local.sh

# Start all services
./scripts/start-local.sh start

# Start with frontend
./scripts/start-local.sh start --with-frontend

# Check status
./scripts/start-local.sh status

# Stop all services
./scripts/start-local.sh stop
```

**Windows:**
```powershell
# Start all services
.\scripts\start-local.ps1 start

# Start with frontend
.\scripts\start-local.ps1 start -WithFrontend

# Check status
.\scripts\start-local.ps1 status

# Stop all services
.\scripts\start-local.ps1 stop
```

#### Option B: Manual Startup

Start services in this order:

```bash
# 1. Eureka Server
cd eureka-server
mvn spring-boot:run

# 2. Core Services (in separate terminals)
cd platform-management-service && mvn spring-boot:run
cd application-config-service && mvn spring-boot:run
cd user-management-service && mvn spring-boot:run
cd permission-registry-service && mvn spring-boot:run
cd tenant-management-service && mvn spring-boot:run

# 3. Authentication Service
cd authentication-service && mvn spring-boot:run

# 4. API Gateway
cd onified-gateway && mvn spring-boot:run

# 5. Frontend (optional)
cd web && npm install && ng serve --port 4200
```

## Verify Setup

### 1. Check Eureka Dashboard
Visit: http://localhost:8761

You should see all services registered with status "UP".

### 2. Check Service Health
```bash
# Health check script (created automatically)
./scripts/health-check.sh

# Or check manually
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:9080/actuator/health  # Gateway
curl http://localhost:9081/actuator/health  # Platform Management
curl http://localhost:9082/actuator/health  # Application Config
curl http://localhost:9083/actuator/health  # Authentication
curl http://localhost:9084/actuator/health  # Permission Registry
curl http://localhost:9085/actuator/health  # User Management
curl http://localhost:9086/actuator/health  # Tenant Management
```

### 3. Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Eureka Server | http://localhost:8761 | Service discovery dashboard |
| API Gateway | http://localhost:9080 | Main API entry point |
| Platform Management | http://localhost:9081 | Platform configuration |
| Application Config | http://localhost:9082 | Application management |
| Authentication | http://localhost:9083 | User authentication |
| Permission Registry | http://localhost:9084 | Permission management |
| User Management | http://localhost:9085 | User management |
| Tenant Management | http://localhost:9086 | Tenant configuration |
| Frontend | http://localhost:4200 | Angular application |

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Find process using port
   lsof -i :8761
   
   # Kill process
   kill -9 <PID>
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL
   sudo systemctl status postgresql
   
   # Test connection
   psql -h localhost -U postgres -d auth_db
   ```

3. **Eureka Registration Issues**
   ```bash
   # Check Eureka server
   curl http://localhost:8761/eureka/apps
   
   # Check service logs
   tail -f logs/*.log
   ```

### Debug Mode

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    com.onified: DEBUG
    org.springframework.cloud: DEBUG
    org.springframework.web: DEBUG
```

## Next Steps

1. **Test APIs**: Use the service documentation to test endpoints
2. **Configure Keycloak**: Set up identity provider (optional)
3. **Frontend Integration**: Connect Angular to backend services
4. **Add Monitoring**: Implement health checks and monitoring

## Support

- **Documentation**: Check `docs/services/` for detailed service documentation
- **Logs**: Check `logs/` directory for service logs
- **Health Checks**: Use the health check scripts for troubleshooting 