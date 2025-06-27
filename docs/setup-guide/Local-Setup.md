# Local Development Setup Guide

This guide provides detailed instructions for setting up the Onified platform for local development without Docker (except for PostgreSQL and Keycloak).

## 📋 Prerequisites

### Required Software
- **Java 21** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **Node.js 18+**
- **Docker Desktop** (for PostgreSQL and Keycloak only)
- **Git**

### Verify Installation
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Node.js version
node --version

# Check Docker version
docker --version
```

## 🗄️ Database Setup (Docker)

### 1. Start PostgreSQL Container
```bash
# Create a Docker network for the services
docker network create onified-network

# Start PostgreSQL container
docker run -d \
  --name postgres-onified \
  --network onified-network \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=root \
  -e POSTGRES_DB=onified \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15

# Verify PostgreSQL is running
docker ps | grep postgres
```

### 2. Initialize Database Schemas
```bash
# Connect to PostgreSQL and create databases
docker exec -it postgres-onified psql -U postgres -d onified

# Create databases for each service
CREATE DATABASE auth_db;
CREATE DATABASE user_mgmt_db;
CREATE DATABASE permission_db;
CREATE DATABASE app_config_db;
CREATE DATABASE platform_mgmt_db;
CREATE DATABASE tenant_mgmt_db;
CREATE DATABASE keycloak_db;

# Exit PostgreSQL
\q
```

## 🔐 Keycloak Setup (Docker)

### 1. Start Keycloak Container
```bash
# Start Keycloak container
docker run -d \
  --name keycloak-onified \
  --network onified-network \
  -e KC_DB=postgres \
  -e KC_DB_URL=jdbc:postgresql://postgres-onified:5432/keycloak_db \
  -e KC_DB_USERNAME=postgres \
  -e KC_DB_PASSWORD=root \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  -e KC_HOSTNAME_STRICT=false \
  -e KC_HOSTNAME_STRICT_HTTPS=false \
  -e KC_HTTP_ENABLED=true \
  -p 9090:8080 \
  quay.io/keycloak/keycloak:24.0.2 start-dev

# Wait for Keycloak to start (check logs)
docker logs -f keycloak-onified
```

### 2. Configure Keycloak Realm
1. Access Keycloak Admin Console: http://localhost:9090
2. Login with `admin/admin123`
3. Create a new realm called `onified`
4. Create the following clients:
   - **authentication-service** (confidential)
   - **web-app** (public)
5. Configure redirect URIs and CORS settings

## ⚙️ Environment Configuration

### 1. Create Environment File
Create a `.env` file in the project root:

```bash
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=root

# Database names
AUTH_DB=auth_db
USER_MGMT_DB=user_mgmt_db
PERMISSION_DB=permission_db
APP_CONFIG_DB=app_config_db
PLATFORM_MGMT_DB=platform_mgmt_db
TENANT_MGMT_DB=tenant_mgmt_db

# Service URLs
AUTH_DB_URL=jdbc:postgresql://localhost:5432/auth_db
USER_MGMT_DB_URL=jdbc:postgresql://localhost:5432/user_mgmt_db
PERMISSION_DB_URL=jdbc:postgresql://localhost:5432/permission_db
APP_CONFIG_DB_URL=jdbc:postgresql://localhost:5432/app_config_db
PLATFORM_MGMT_DB_URL=jdbc:postgresql://localhost:5432/platform_mgmt_db
TENANT_MGMT_DB_URL=jdbc:postgresql://localhost:5432/tenant_mgmt_db

# Service Ports
EUREKA_PORT=8761
GATEWAY_PORT=9080
AUTH_PORT=9083
USER_MGMT_PORT=9085
PERMISSION_REGISTRY_PORT=9084
APP_CONFIG_PORT=9082
PLATFORM_MGMT_PORT=9081
TENANT_MGMT_PORT=9086

# Keycloak Configuration
KEYCLOAK_PORT=9090
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin123
KEYCLOAK_CLIENT_SECRET=your-client-secret-here

# Eureka Configuration (disabled for local development)
EUREKA_ENABLED=false
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# Logging
LOG_DIR=./logs
```

## 🚀 Service Startup Order

### 1. Start Eureka Server (Optional for Local Development)
```bash
cd eureka-server
mvn spring-boot:run
```

### 2. Start Core Services
```bash
# Terminal 1: Application Config Service
cd application-config-service
mvn spring-boot:run

# Terminal 2: Authentication Service
cd authentication-service
mvn spring-boot:run

# Terminal 3: Permission Registry Service
cd permission-registry-service
mvn spring-boot:run

# Terminal 4: User Management Service
cd user-management-service
mvn spring-boot:run

# Terminal 5: Platform Management Service
cd platform-management-service
mvn spring-boot:run

# Terminal 6: Tenant Management Service
cd tenant-management-service
mvn spring-boot:run
```

### 3. Start API Gateway
```bash
# Terminal 7: API Gateway
cd onified-gateway
mvn spring-boot:run
```

### 4. Start Angular Application
```bash
# Terminal 8: Angular Frontend
cd web
npm install
ng serve
```

## 🔧 Development Workflow

### Building Services
```bash
# Build a specific service
cd service-name
mvn clean install

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=local
```

### Database Migrations
```bash
# Run Flyway migrations (if configured)
mvn flyway:migrate

# Check migration status
mvn flyway:info
```

### Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run specific test class
mvn test -Dtest=TestClassName
```

## 📊 Monitoring and Debugging

### Health Checks
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
# View service logs
tail -f logs/service-name/application.log

# View Docker container logs
docker logs -f postgres-onified
docker logs -f keycloak-onified
```

### Debugging
```bash
# Run service in debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using a port
lsof -i :9080

# Kill process using port
kill -9 <PID>
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL status
docker ps | grep postgres

# Check PostgreSQL logs
docker logs postgres-onified

# Test database connection
docker exec -it postgres-onified psql -U postgres -d onified -c "SELECT 1;"
```

#### 3. Keycloak Connection Issues
```bash
# Check Keycloak status
docker ps | grep keycloak

# Check Keycloak logs
docker logs keycloak-onified

# Restart Keycloak if needed
docker restart keycloak-onified
```

#### 4. Service Discovery Issues
- Ensure Eureka Server is running (if using service discovery)
- Check service registration in Eureka dashboard
- Verify service URLs in application.yml files

### Performance Optimization

#### 1. JVM Settings
```bash
# Add to mvn spring-boot:run
-Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC"
```

#### 2. Database Optimization
```bash
# PostgreSQL settings for development
docker run -d \
  --name postgres-onified \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=root \
  -e POSTGRES_DB=onified \
  -e POSTGRES_INITDB_ARGS="--shared-preload-libraries=pg_stat_statements" \
  -p 5432:5432 \
  postgres:15
```

## 🔄 Cleanup

### Stop All Services
```bash
# Stop Spring Boot services (Ctrl+C in each terminal)
# Stop Angular application (Ctrl+C)
# Stop Docker containers
docker stop keycloak-onified postgres-onified
```

### Clean Docker Resources
```bash
# Remove containers
docker rm keycloak-onified postgres-onified

# Remove volumes (WARNING: This will delete all data)
docker volume rm postgres_data

# Remove network
docker network rm onified-network
```

## 📚 Next Steps

1. **API Testing**: Use Postman or similar tool to test API endpoints
2. **Frontend Development**: Start developing Angular components
3. **Database Schema**: Review and modify database schemas as needed
4. **Security**: Configure additional security measures
5. **Monitoring**: Set up application monitoring and logging

For more detailed information about each service, refer to the individual service documentation in the `docs/services/` directory. 