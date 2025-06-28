# Local Development Setup Guide

This guide will help you set up and run the Onified platform locally without Docker, with Eureka service discovery enabled.

## Prerequisites

### Required Software
- **Java 21** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Node.js 18+** (for Angular frontend)
- **Git**

### System Requirements
- **RAM:** Minimum 8GB (16GB recommended)
- **Storage:** At least 10GB free space
- **OS:** Windows, macOS, or Linux

## Database Setup

### 1. Install PostgreSQL

**macOS (using Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql-15 postgresql-contrib-15
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Windows:**
Download and install from [PostgreSQL official website](https://www.postgresql.org/download/windows/)

### 2. Create Databases

Connect to PostgreSQL and create the required databases:

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

# Verify databases
\l

# Exit PostgreSQL
\q
```

### 3. Database User Setup (Optional)

For better security, create a dedicated user:

```bash
psql -U postgres

CREATE USER onified_user WITH PASSWORD 'onified_password';
GRANT ALL PRIVILEGES ON DATABASE eureka_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE user_mgmt_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE permission_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE app_config_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE platform_mgmt_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE tenant_mgmt_db TO onified_user;
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO onified_user;

\q
```

## Environment Configuration

### 1. Create Environment File

Create a `.env` file in the root directory:

```bash
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root
POSTGRES_PORT=5432

# Eureka Server
EUREKA_PORT=8761
EUREKA_ENABLED=true
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# API Gateway
GATEWAY_PORT=9080

# Platform Management Service
PLATFORM_MGMT_PORT=9081
PLATFORM_MGMT_DB_URL=jdbc:postgresql://localhost:5432/platform_mgmt_db
PLATFORM_MGMT_DB_USERNAME=postgres
PLATFORM_MGMT_DB_PASSWORD=root

# Application Config Service
APP_CONFIG_PORT=9082
APP_CONFIG_DB_URL=jdbc:postgresql://localhost:5432/app_config_db
APP_CONFIG_DB_USERNAME=postgres
APP_CONFIG_DB_PASSWORD=root

# Authentication Service
AUTH_PORT=9083
AUTH_DB_URL=jdbc:postgresql://localhost:5432/auth_db
AUTH_DB_USERNAME=postgres
AUTH_DB_PASSWORD=root

# Permission Registry Service
PERMISSION_REGISTRY_PORT=9084
PERMISSION_DB_URL=jdbc:postgresql://localhost:5432/permission_db
PERMISSION_DB_USERNAME=postgres
PERMISSION_DB_PASSWORD=root

# User Management Service
USER_MGMT_PORT=9085
USER_MGMT_DB_URL=jdbc:postgresql://localhost:5432/user_mgmt_db
USER_MGMT_DB_USERNAME=postgres
USER_MGMT_DB_PASSWORD=root

# Tenant Management Service
TENANT_MGMT_PORT=9086
TENANT_MGMT_DB_URL=jdbc:postgresql://localhost:5432/tenant_mgmt_db
TENANT_MGMT_DB_USERNAME=postgres
TENANT_MGMT_DB_PASSWORD=root

# Keycloak Configuration
KEYCLOAK_PORT=9090
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123
KEYCLOAK_REALM=onified
KEYCLOAK_CLIENT_ID=onified-auth-service
KEYCLOAK_CLIENT_SECRET=your-client-secret-here

# Frontend
FRONTEND_PORT=4200

# JWT Configuration
JWT_SECRET=yourVerySecretKeyForJWTTokenGenerationThatShouldBeLongAndComplex12345!@#$%^&*()
JWT_EXPIRATION=3600000

# Logging
LOG_DIR=./logs
```

### 2. Load Environment Variables

**macOS/Linux:**
```bash
export $(cat .env | xargs)
```

**Windows (PowerShell):**
```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
    }
}
```

## Service Startup Order

Start services in the following order to ensure proper dependencies:

### 1. Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

**Verify:** http://localhost:8761

### 2. Start Keycloak (Optional for initial testing)

```bash
# Download Keycloak standalone server
wget https://github.com/keycloak/keycloak/releases/download/24.0.2/keycloak-24.0.2.tar.gz
tar -xzf keycloak-24.0.2.tar.gz
cd keycloak-24.0.2

# Start Keycloak
./bin/kc.sh start-dev --db=postgres --db-url=jdbc:postgresql://localhost:5432/keycloak_db --db-username=postgres --db-password=root
```

**Verify:** http://localhost:9090

### 3. Start Core Services

Start these services in parallel (in separate terminals):

```bash
# Terminal 1 - Platform Management Service
cd platform-management-service
mvn spring-boot:run

# Terminal 2 - Application Config Service
cd application-config-service
mvn spring-boot:run

# Terminal 3 - User Management Service
cd user-management-service
mvn spring-boot:run

# Terminal 4 - Permission Registry Service
cd permission-registry-service
mvn spring-boot:run

# Terminal 5 - Tenant Management Service
cd tenant-management-service
mvn spring-boot:run
```

### 4. Start Authentication Service

```bash
cd authentication-service
mvn spring-boot:run
```

### 5. Start API Gateway

```bash
cd onified-gateway
mvn spring-boot:run
```

### 6. Start Angular Frontend

```bash
cd web
npm install
ng serve --port 4200
```

## Service URLs and Health Checks

### Service Endpoints

| Service | URL | Health Check |
|---------|-----|--------------|
| Eureka Server | http://localhost:8761 | http://localhost:8761/actuator/health |
| API Gateway | http://localhost:9080 | http://localhost:9080/actuator/health |
| Platform Management | http://localhost:9081 | http://localhost:9081/actuator/health |
| Application Config | http://localhost:9082 | http://localhost:9082/actuator/health |
| Authentication | http://localhost:9083 | http://localhost:9083/actuator/health |
| Permission Registry | http://localhost:9084 | http://localhost:9084/actuator/health |
| User Management | http://localhost:9085 | http://localhost:9085/actuator/health |
| Tenant Management | http://localhost:9086 | http://localhost:9086/actuator/health |
| Keycloak | http://localhost:9090 | http://localhost:9090/health |
| Frontend | http://localhost:4200 | http://localhost:4200 |

### Verify Service Registration

Check Eureka dashboard to ensure all services are registered:
http://localhost:8761

You should see all services listed with status "UP".

## Development Workflow

### 1. Hot Reload

For development, you can use Spring Boot DevTools for hot reload:

```bash
# Add to application.yml for each service
spring:
  devtools:
    restart:
      enabled: true
```

### 2. Debug Mode

Run services in debug mode:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 3. Logging

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    root: INFO
    com.onified: DEBUG
    org.springframework.web: DEBUG
    org.springframework.cloud: DEBUG
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

```bash
# Find process using port
lsof -i :8761

# Kill process
kill -9 <PID>

# Or change port in application.yml
server:
  port: 8762
```

#### 2. Database Connection Issues

```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check connection
psql -h localhost -U postgres -d auth_db

# Verify environment variables
echo $AUTH_DB_URL
```

#### 3. Eureka Registration Issues

```bash
# Check Eureka server
curl http://localhost:8761/eureka/apps

# Check service health
curl http://localhost:9081/actuator/health

# Verify Eureka configuration
grep -r "eureka" */src/main/resources/application.yml
```

#### 4. Service Discovery Issues

```bash
# Check if services can reach Eureka
curl http://localhost:8761/eureka/apps

# Check service logs for registration errors
tail -f logs/*.log
```

### Debug Commands

#### Check Service Status

```bash
# Check all services
for port in 8761 9080 9081 9082 9083 9084 9085 9086; do
    echo "Checking port $port..."
    curl -s http://localhost:$port/actuator/health || echo "Service on port $port is down"
done
```

#### Check Database Connections

```bash
# Test database connectivity
for db in eureka_db auth_db user_mgmt_db permission_db app_config_db platform_mgmt_db tenant_mgmt_db; do
    echo "Testing $db..."
    psql -h localhost -U postgres -d $db -c "SELECT 1;" || echo "Failed to connect to $db"
done
```

#### Monitor Logs

```bash
# Monitor all service logs
tail -f logs/*.log

# Monitor specific service
tail -f logs/authentication-service.log
```

## Performance Optimization

### 1. JVM Settings

For better performance, add JVM arguments:

```bash
export MAVEN_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

### 2. Database Optimization

```sql
-- Increase connection pool
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';

-- Reload configuration
SELECT pg_reload_conf();
```

### 3. Service Configuration

Optimize each service's `application.yml`:

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
```

## Security Considerations

### 1. Production Settings

For production deployment:

```bash
# Use strong passwords
export POSTGRES_PASSWORD=your-strong-password
export JWT_SECRET=your-very-long-and-complex-jwt-secret

# Enable HTTPS
# Configure proper CORS
# Set up monitoring and alerting
```

### 2. Network Security

```bash
# Restrict database access
sudo ufw allow from 127.0.0.1 to any port 5432

# Use internal networks for service communication
# Implement proper firewall rules
```

## Monitoring and Logging

### 1. Centralized Logging

Consider using ELK stack or similar:

```bash
# Example log aggregation
# Elasticsearch + Logstash + Kibana
```

### 2. Health Monitoring

```bash
# Create health check script
#!/bin/bash
for service in eureka gateway platform auth user permission app-config tenant; do
    echo "Checking $service..."
    curl -s http://localhost:9080/actuator/health | jq '.status'
done
```

## Next Steps

1. **Test API Endpoints**: Use the API documentation to test all services
2. **Configure Keycloak**: Set up realms, clients, and users
3. **Frontend Integration**: Connect Angular frontend to backend services
4. **Add Monitoring**: Implement comprehensive monitoring and alerting
5. **Security Hardening**: Implement production security measures

## Support

If you encounter issues:

1. Check the service logs in `./logs/` directory
2. Verify all environment variables are set correctly
3. Ensure all required ports are available
4. Check the troubleshooting section above
5. Review the service-specific documentation in `docs/services/` 