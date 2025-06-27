# Application Config Service

## Overview

The Application Config Service is a core microservice in the Onified platform that manages application and module configurations. It provides centralized management of application definitions, modules, and their relationships, enabling dynamic configuration management across the platform.

**Service Name:** `application-config-service`  
**Port:** `9082` (configurable via `APP_CONFIG_PORT`)  
**Database:** PostgreSQL (`app_config_db`)

## Architecture

### Core Components

- **Application Management**: CRUD operations for application definitions
- **Module Management**: CRUD operations for application modules
- **Configuration Storage**: PostgreSQL-based persistent storage
- **Service Discovery**: Eureka client integration (conditional)
- **Security**: Spring Security with configurable authentication

### Key Features

- Application lifecycle management
- Module configuration and activation
- Audit trail with creation/update timestamps
- RESTful API with standardized responses
- Database-driven configuration management
- Service discovery integration

## Dependencies

### Spring Boot & Cloud Versions

| Component | Version |
|-----------|---------|
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.1 |
| Java | 21 |

### Core Dependencies

| Dependency | Purpose |
|------------|---------|
| `spring-boot-starter-web` | Web application framework |
| `spring-boot-starter-data-jpa` | JPA and Hibernate integration |
| `spring-boot-starter-security` | Security framework |
| `spring-boot-starter-actuator` | Health checks and monitoring |
| `spring-cloud-starter-netflix-eureka-client` | Service discovery |
| `postgresql` | Database driver |
| `lombok` | Code generation |
| `jakarta.validation-api` | Bean validation |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `APP_CONFIG_PORT` | `9082` | Service port |
| `APP_CONFIG_DB_URL` | `jdbc:postgresql://localhost:5432/app_config_db` | Database URL |
| `APP_CONFIG_DB_USERNAME` | `postgres` | Database username |
| `APP_CONFIG_DB_PASSWORD` | `root` | Database password |
| `EUREKA_ENABLED` | `false` | Enable Eureka client |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://eureka-server:8761/eureka/` | Eureka server URL |

### Database Configuration

```yaml
spring:
  datasource:
    url: ${APP_CONFIG_DB_URL:jdbc:postgresql://localhost:5432/app_config_db}
    username: ${APP_CONFIG_DB_USERNAME:postgres}
    password: ${APP_CONFIG_DB_PASSWORD:root}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    open-in-view: false
```

### Security Configuration

The service uses Spring Security with:
- CSRF disabled for API endpoints
- Stateless session management
- Configurable authentication for endpoints
- Permit-all access to `/api/applications/**` and `/api/modules/**`

## API Reference

### Base URL
```
http://localhost:9082/api
```

### Applications Endpoints

#### Create Application
```http
POST /applications
Content-Type: application/json

{
  "appCode": "string",
  "displayName": "string",
  "isActive": boolean
}
```

**Response:**
```json
{
  "statusCode": 201,
  "status": "SUCCESS",
  "body": {
    "appCode": "string",
    "displayName": "string",
    "isActive": boolean
  }
}
```

#### Get Application by Code
```http
GET /applications/{appCode}
```

#### Get All Applications
```http
GET /applications
```

#### Update Application
```http
PUT /applications/{appCode}
Content-Type: application/json

{
  "appCode": "string",
  "displayName": "string",
  "isActive": boolean
}
```

#### Delete Application
```http
DELETE /applications/{appCode}
```

### Modules Endpoints

#### Create Module
```http
POST /modules
Content-Type: application/json

{
  "appCode": "string",
  "moduleCode": "string",
  "isActive": boolean
}
```

**Response:**
```json
{
  "statusCode": 201,
  "status": "SUCCESS",
  "body": {
    "moduleId": integer,
    "appCode": "string",
    "moduleCode": "string",
    "isActive": boolean
  }
}
```

#### Get Module by ID
```http
GET /modules/{moduleId}
```

#### Get Modules by Application
```http
GET /modules/app/{appCode}
```

#### Update Module
```http
PUT /modules/{moduleId}
Content-Type: application/json

{
  "appCode": "string",
  "moduleCode": "string",
  "isActive": boolean
}
```

#### Delete Module
```http
DELETE /modules/{moduleId}
```

## Data Models

### Application Entity
```java
@Entity
@Table(name = "applications")
public class Application extends Auditable {
    @Id
    private String appCode;
    private String displayName;
    private Boolean isActive;
}
```

### AppModule Entity
```java
@Entity
@Table(name = "modules")
public class AppModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer moduleId;
    private String appCode;
    private String moduleCode;
    private Boolean isActive;
}
```

### Auditable Base Class
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

## Database Schema

### Applications Table
```sql
CREATE TABLE applications (
    app_code VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255),
    is_active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Modules Table
```sql
CREATE TABLE modules (
    module_id SERIAL PRIMARY KEY,
    app_code VARCHAR(255),
    module_code VARCHAR(255),
    is_active BOOLEAN,
    FOREIGN KEY (app_code) REFERENCES applications(app_code)
);
```

## Deployment

### Local Development

1. **Prerequisites:**
   - Java 21
   - PostgreSQL
   - Maven

2. **Database Setup:**
   ```sql
   CREATE DATABASE app_config_db;
   ```

3. **Environment Setup:**
   ```bash
   export APP_CONFIG_DB_URL=jdbc:postgresql://localhost:5432/app_config_db
   export APP_CONFIG_DB_USERNAME=postgres
   export APP_CONFIG_DB_PASSWORD=your_password
   ```

4. **Run Service:**
   ```bash
   cd application-config-service
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build Image:**
   ```bash
   docker build -t onified/app-config-service .
   ```

2. **Run Container:**
   ```bash
   docker run -d \
     --name app-config-service \
     -p 9082:9082 \
     -e APP_CONFIG_DB_URL=jdbc:postgresql://db:5432/app_config_db \
     -e APP_CONFIG_DB_USERNAME=postgres \
     -e APP_CONFIG_DB_PASSWORD=root \
     -e EUREKA_ENABLED=true \
     onified/app-config-service
   ```

### Docker Compose

The service is included in the main `docker-compose.yml`:

```yaml
application-config-service:
  build: ./application-config-service
  ports:
    - "9082:9082"
  environment:
    - APP_CONFIG_DB_URL=jdbc:postgresql://postgres:5432/app_config_db
    - APP_CONFIG_DB_USERNAME=postgres
    - APP_CONFIG_DB_PASSWORD=root
    - EUREKA_ENABLED=true
  depends_on:
    - postgres
    - eureka-server
```

## Health Checks

### Actuator Endpoints

- **Health Check:** `GET /actuator/health`
- **Info:** `GET /actuator/info`

### Health Check Response
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 419430400000,
        "threshold": 10485760
      }
    }
  }
}
```

## Monitoring & Logging

### Logging Configuration

```yaml
logging:
  level:
    root: INFO
    org.springframework: INFO
    org.springframework.web: DEBUG
    org.springframework.jdbc.core: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    com.onified.appconfig: DEBUG
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

### Log Files

- **Location:** `/app/logs/application-config-service.log` (Docker)
- **Rotation:** 10MB max size, 30 days retention
- **Total Size Cap:** 1GB

## Error Handling

### Exception Types

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `BadRequestException` | 400 | Invalid request data |
| `ConflictException` | 409 | Resource conflict (duplicate) |
| `GlobalException` | 500 | Unexpected server error |

### Error Response Format

```json
{
  "statusCode": 404,
  "status": "ERROR",
  "body": {
    "errorCode": "RESOURCE_NOT_FOUND",
    "errorMessage": "Application with appCode APP001 not found."
  }
}
```

## Security Considerations

### Current Security Model

- **CSRF Protection:** Disabled for API endpoints
- **Session Management:** Stateless
- **Authentication:** Configurable per endpoint
- **Authorization:** Role-based (configurable)

### Security Recommendations

1. **Production Security:**
   - Enable CSRF protection
   - Implement JWT authentication
   - Add rate limiting
   - Use HTTPS

2. **Database Security:**
   - Use strong passwords
   - Limit database user permissions
   - Enable SSL connections

3. **Network Security:**
   - Use internal networks for service communication
   - Implement API gateway authentication
   - Monitor access logs

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

**Symptoms:**
- Service fails to start
- Database connection errors in logs

**Solutions:**
```bash
# Check database connectivity
psql -h localhost -U postgres -d app_config_db

# Verify environment variables
echo $APP_CONFIG_DB_URL
echo $APP_CONFIG_DB_USERNAME
echo $APP_CONFIG_DB_PASSWORD
```

#### 2. Port Already in Use

**Symptoms:**
- `Port 9082 is already in use` error

**Solutions:**
```bash
# Find process using port
lsof -i :9082

# Kill process or change port
export APP_CONFIG_PORT=9083
```

#### 3. Eureka Registration Issues

**Symptoms:**
- Service not visible in Eureka dashboard
- Service discovery failures

**Solutions:**
```bash
# Check Eureka configuration
echo $EUREKA_ENABLED
echo $EUREKA_CLIENT_SERVICEURL_DEFAULTZONE

# Verify Eureka server is running
curl http://eureka-server:8761/eureka/apps
```

#### 4. JPA/Hibernate Issues

**Symptoms:**
- Database schema errors
- Entity mapping issues

**Solutions:**
```bash
# Check database schema
psql -h localhost -U postgres -d app_config_db -c "\dt"

# Verify entity mappings
# Check for @Entity annotations and table names
```

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.onified.appconfig: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Performance Monitoring

#### Key Metrics to Monitor

1. **Database Performance:**
   - Query execution time
   - Connection pool usage
   - Transaction duration

2. **Application Performance:**
   - Response times
   - Memory usage
   - CPU utilization

3. **Service Health:**
   - Uptime
   - Error rates
   - Request volume

#### Monitoring Tools

- **Spring Boot Actuator:** Built-in health checks
- **Micrometer:** Metrics collection
- **Prometheus:** Metrics storage
- **Grafana:** Visualization

## Integration Examples

### Creating an Application

```bash
curl -X POST http://localhost:9082/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "appCode": "CRM",
    "displayName": "Customer Relationship Management",
    "isActive": true
  }'
```

### Adding a Module

```bash
curl -X POST http://localhost:9082/api/modules \
  -H "Content-Type: application/json" \
  -d '{
    "appCode": "CRM",
    "moduleCode": "CUSTOMERS",
    "isActive": true
  }'
```

### Retrieving Application Configuration

```bash
# Get all applications
curl http://localhost:9082/api/applications

# Get specific application
curl http://localhost:9082/api/applications/CRM

# Get modules for application
curl http://localhost:9082/api/modules/app/CRM
```

## Best Practices

### Development

1. **Code Organization:**
   - Use DTOs for API requests/responses
   - Implement proper exception handling
   - Follow REST conventions

2. **Database Design:**
   - Use meaningful table and column names
   - Implement proper foreign key constraints
   - Add indexes for frequently queried columns

3. **API Design:**
   - Use consistent response formats
   - Implement proper HTTP status codes
   - Add comprehensive error messages

### Operations

1. **Deployment:**
   - Use environment-specific configurations
   - Implement health checks
   - Set up proper logging

2. **Monitoring:**
   - Monitor application metrics
   - Set up alerting for critical issues
   - Regular log analysis

3. **Security:**
   - Regular security updates
   - Access control implementation
   - Data encryption in transit and at rest

## Related Services

- **Eureka Server:** Service discovery
- **API Gateway:** Request routing and security
- **User Management Service:** User authentication and authorization
- **Permission Registry Service:** Permission management
- **Platform Management Service:** Platform configuration
- **Tenant Management Service:** Multi-tenancy support 