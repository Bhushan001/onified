# Platform Management Service

## Overview

The Platform Management Service is a core microservice in the Onified platform that handles platform-wide configurations, tenant management, and password policies. It provides centralized management of platform settings, multi-tenancy support, and security policies that apply across the entire platform.

**Service Name:** `platform-management-service`  
**Port:** `9081` (configurable via `PLATFORM_MGMT_PORT`)  
**Database:** PostgreSQL (`platform_mgmt_db`)

## Architecture

### Core Components

- **Tenant Management**: Multi-tenancy support with tenant lifecycle management
- **Password Policy Management**: Centralized password policy configuration
- **Platform Configuration**: Platform-wide settings and configurations
- **Audit Trail**: Comprehensive audit logging with user tracking
- **Service Discovery**: Eureka client integration (conditional)

### Key Features

- Multi-tenant platform support
- Configurable password policies with validation rules
- Platform-wide configuration management
- Audit trail with creation/update tracking
- RESTful API with standardized responses
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

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PLATFORM_MGMT_PORT` | `9081` | Service port |
| `PLATFORM_MGMT_DB_URL` | `jdbc:postgresql://localhost:5432/platform_mgmt_db` | Database URL |
| `PLATFORM_MGMT_DB_USERNAME` | `postgres` | Database username |
| `PLATFORM_MGMT_DB_PASSWORD` | `root` | Database password |
| `EUREKA_ENABLED` | `false` | Enable Eureka client |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://eureka-server:8761/eureka/` | Eureka server URL |

### Database Configuration

```yaml
spring:
  datasource:
    url: ${PLATFORM_MGMT_DB_URL:jdbc:postgresql://localhost:5432/platform_mgmt_db}
    username: ${PLATFORM_MGMT_DB_USERNAME:postgres}
    password: ${PLATFORM_MGMT_DB_PASSWORD:root}
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
- Permit-all access to `/api/password-policies/**`
- Configurable authentication for other endpoints

## API Reference

### Base URL
```
http://localhost:9081/api
```

### Tenant Management Endpoints

#### Get All Tenants
```http
GET /tenants
```

**Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": [
    {
      "tenantId": "uuid",
      "name": "string",
      "status": "ACTIVE",
      "extraConfig": "json",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00",
      "createdBy": "string",
      "updatedBy": "string"
    }
  ]
}
```

#### Get Tenant by ID
```http
GET /tenants/{tenantId}
```

#### Create Tenant
```http
POST /tenants
Content-Type: application/json

{
  "name": "string",
  "status": "ACTIVE",
  "extraConfig": "json"
}
```

#### Update Tenant
```http
PUT /tenants/{tenantId}
Content-Type: application/json

{
  "name": "string",
  "status": "ACTIVE",
  "extraConfig": "json"
}
```

#### Delete Tenant
```http
DELETE /tenants/{tenantId}
```

### Password Policy Endpoints

#### Get All Password Policies
```http
GET /password-policies
```

**Response:**
```json
[
  {
    "id": 1,
    "policyName": "Default Password Policy",
    "description": "Default password policy for the platform",
    "minLength": 10,
    "maxPasswordAge": 90,
    "minPasswordAge": 0,
    "passwordHistory": 4,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSpecial": true,
    "initialPasswordFormat": "[FirstInitial][LastInitial][Random]",
    "bannedPatterns": "password,123456,qwerty,admin",
    "isActive": true,
    "isDefault": true
  }
]
```

#### Get Password Policy by ID
```http
GET /password-policies/{id}
```

#### Get Default Password Policy
```http
GET /password-policies/default
```

#### Create Password Policy
```http
POST /password-policies
Content-Type: application/json

{
  "policyName": "string",
  "description": "string",
  "minLength": 10,
  "maxPasswordAge": 90,
  "minPasswordAge": 0,
  "passwordHistory": 4,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "string",
  "bannedPatterns": "string",
  "isActive": true,
  "isDefault": false
}
```

#### Update Password Policy
```http
PUT /password-policies/{id}
Content-Type: application/json

{
  "policyName": "string",
  "description": "string",
  "minLength": 10,
  "maxPasswordAge": 90,
  "minPasswordAge": 0,
  "passwordHistory": 4,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "string",
  "bannedPatterns": "string",
  "isActive": true,
  "isDefault": false
}
```

#### Delete Password Policy
```http
DELETE /password-policies/{id}
```

#### Set as Default Policy
```http
PUT /password-policies/{id}/default
```

#### Ensure Default Policy Exists
```http
GET /password-policies/default/ensure
```

### Platform Password Policy Endpoint

#### Get Platform Password Policy
```http
GET /password-policy/platform
```

**Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": {
    "id": 1,
    "policyName": "Default Password Policy",
    "description": "Default password policy for the platform",
    "minLength": 10,
    "maxPasswordAge": 90,
    "minPasswordAge": 0,
    "passwordHistory": 4,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSpecial": true,
    "initialPasswordFormat": "[FirstInitial][LastInitial][Random]",
    "bannedPatterns": "password,123456,qwerty,admin",
    "isActive": true,
    "isDefault": true
  }
}
```

## Data Models

### Tenant Entity
```java
@Entity
@Table(name = "tenants")
public class Tenant extends Auditable {
    @Id
    private String tenantId;
    private String name;
    private String status;
    @Column(columnDefinition = "text")
    private String extraConfig; // JSON for extensibility
}
```

### PasswordPolicy Entity
```java
@Entity
@Table(name = "password_policy")
public class PasswordPolicy extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_name", nullable = false, unique = true)
    private String policyName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "min_length", nullable = false)
    private int minLength = 10;
    
    @Column(name = "max_password_age")
    private int maxPasswordAge = 90;
    
    @Column(name = "min_password_age")
    private int minPasswordAge = 0;
    
    @Column(name = "password_history")
    private int passwordHistory = 4;
    
    @Column(name = "require_uppercase", nullable = false)
    private boolean requireUppercase = true;
    
    @Column(name = "require_lowercase", nullable = false)
    private boolean requireLowercase = true;
    
    @Column(name = "require_number", nullable = false)
    private boolean requireNumber = true;
    
    @Column(name = "require_special", nullable = false)
    private boolean requireSpecial = true;
    
    @Column(name = "initial_password_format")
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    
    @Column(name = "banned_patterns", columnDefinition = "TEXT")
    private String bannedPatterns = "password,123456,qwerty,admin";
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
}
```

### Auditable Base Class
```java
@MappedSuperclass
public abstract class Auditable {
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

## Database Schema

### Tenants Table
```sql
CREATE TABLE tenants (
    tenant_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    status VARCHAR(50),
    extra_config TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
```

### Password Policy Table
```sql
CREATE TABLE password_policy (
    id BIGSERIAL PRIMARY KEY,
    policy_name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    min_length INTEGER NOT NULL DEFAULT 10,
    max_password_age INTEGER DEFAULT 90,
    min_password_age INTEGER DEFAULT 0,
    password_history INTEGER DEFAULT 4,
    require_uppercase BOOLEAN NOT NULL DEFAULT true,
    require_lowercase BOOLEAN NOT NULL DEFAULT true,
    require_number BOOLEAN NOT NULL DEFAULT true,
    require_special BOOLEAN NOT NULL DEFAULT true,
    initial_password_format VARCHAR(255) DEFAULT '[FirstInitial][LastInitial][Random]',
    banned_patterns TEXT DEFAULT 'password,123456,qwerty,admin',
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_default BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
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
   CREATE DATABASE platform_mgmt_db;
   ```

3. **Environment Setup:**
   ```bash
   export PLATFORM_MGMT_DB_URL=jdbc:postgresql://localhost:5432/platform_mgmt_db
   export PLATFORM_MGMT_DB_USERNAME=postgres
   export PLATFORM_MGMT_DB_PASSWORD=your_password
   ```

4. **Run Service:**
   ```bash
   cd platform-management-service
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build Image:**
   ```bash
   docker build -t onified/platform-management-service .
   ```

2. **Run Container:**
   ```bash
   docker run -d \
     --name platform-management-service \
     -p 9081:9081 \
     -e PLATFORM_MGMT_DB_URL=jdbc:postgresql://db:5432/platform_mgmt_db \
     -e PLATFORM_MGMT_DB_USERNAME=postgres \
     -e PLATFORM_MGMT_DB_PASSWORD=root \
     -e EUREKA_ENABLED=true \
     onified/platform-management-service
   ```

### Docker Compose

The service is included in the main `docker-compose.yml`:

```yaml
platform-management-service:
  build: ./platform-management-service
  ports:
    - "9081:9081"
  environment:
    - PLATFORM_MGMT_DB_URL=jdbc:postgresql://postgres:5432/platform_mgmt_db
    - PLATFORM_MGMT_DB_USERNAME=postgres
    - PLATFORM_MGMT_DB_PASSWORD=root
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
    com.onified.ai.platform_management: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### Log Files

- **Location:** `/app/logs/platform-management-service.log` (Docker)
- **Rotation:** 10MB max size, 30 days retention
- **Total Size Cap:** 1GB

## Error Handling

### Exception Types

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `TenantNotFoundException` | 404 | Tenant not found |
| `IllegalArgumentException` | 400 | Invalid request data |
| `GlobalException` | 500 | Unexpected server error |

### Error Response Format

```json
{
  "statusCode": 404,
  "status": "ERROR",
  "message": "The specified tenant does not exist.",
  "details": "uri=/api/tenants/invalid-id"
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
psql -h localhost -U postgres -d platform_mgmt_db

# Verify environment variables
echo $PLATFORM_MGMT_DB_URL
echo $PLATFORM_MGMT_DB_USERNAME
echo $PLATFORM_MGMT_DB_PASSWORD
```

#### 2. Port Already in Use

**Symptoms:**
- `Port 9081 is already in use` error

**Solutions:**
```bash
# Find process using port
lsof -i :9081

# Kill process or change port
export PLATFORM_MGMT_PORT=9083
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

#### 4. Password Policy Issues

**Symptoms:**
- Default policy not found
- Policy validation errors

**Solutions:**
```bash
# Check if default policy exists
curl http://localhost:9081/api/password-policies/default

# Ensure default policy exists
curl http://localhost:9081/api/password-policies/default/ensure

# Check all policies
curl http://localhost:9081/api/password-policies
```

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.onified.ai.platform_management: DEBUG
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

### Creating a Tenant

```bash
curl -X POST http://localhost:9081/api/tenants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acme Corporation",
    "status": "ACTIVE",
    "extraConfig": "{\"timezone\":\"UTC\",\"locale\":\"en_US\"}"
  }'
```

### Creating a Password Policy

```bash
curl -X POST http://localhost:9081/api/password-policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyName": "Strict Password Policy",
    "description": "Enhanced security password policy",
    "minLength": 12,
    "maxPasswordAge": 60,
    "minPasswordAge": 1,
    "passwordHistory": 6,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSpecial": true,
    "initialPasswordFormat": "[Random][Random][Random][Random]",
    "bannedPatterns": "password,123456,qwerty,admin,company",
    "isActive": true,
    "isDefault": false
  }'
```

### Retrieving Platform Configuration

```bash
# Get all tenants
curl http://localhost:9081/api/tenants

# Get specific tenant
curl http://localhost:9081/api/tenants/tenant-uuid

# Get platform password policy
curl http://localhost:9081/api/password-policy/platform

# Get all password policies
curl http://localhost:9081/api/password-policies
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
- **Authentication Service:** User authentication
- **User Management Service:** User management
- **Tenant Management Service:** Multi-tenancy support
- **Application Config Service:** Application configuration
- **Permission Registry Service:** Permission management 