# Tenant Management Service

## Overview

The Tenant Management Service is a specialized microservice in the Onified platform that handles tenant-specific configurations and management. It provides tenant isolation, configuration management, and integration with platform-wide services through Feign clients. This service enables multi-tenancy support with tenant-specific settings and configurations.

**Service Name:** `tenant-management-service`  
**Port:** `9086` (configurable via `TENANT_MGMT_PORT`)  
**Database:** PostgreSQL (`tenant_management_db`)

## Architecture

### Core Components

- **Tenant Configuration Management**: Tenant-specific settings and configurations
- **Password Policy Integration**: Feign client integration with Platform Management Service
- **Tenant Isolation**: Multi-tenant data separation and management
- **Service Discovery**: Eureka client integration (conditional)
- **Feign Client Integration**: HTTP client for inter-service communication

### Key Features

- Tenant-specific configuration management
- Integration with platform password policies
- Tenant isolation and data separation
- RESTful API with standardized responses
- Service discovery integration
- Feign client for external service communication

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
| `spring-boot-starter-actuator` | Health checks and monitoring |
| `spring-cloud-starter-openfeign` | HTTP client for service communication |
| `spring-cloud-starter-netflix-eureka-client` | Service discovery |
| `postgresql` | Database driver |
| `lombok` | Code generation |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `TENANT_MGMT_PORT` | `9086` | Service port |
| `TENANT_MGMT_DB_URL` | `jdbc:postgresql://localhost:5432/tenant_management_db` | Database URL |
| `TENANT_MGMT_DB_USERNAME` | `postgres` | Database username |
| `TENANT_MGMT_DB_PASSWORD` | `root` | Database password |
| `EUREKA_ENABLED` | `false` | Enable Eureka client |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://eureka-server:8761/eureka/` | Eureka server URL |

### Database Configuration

```yaml
spring:
  datasource:
    url: ${TENANT_MGMT_DB_URL:jdbc:postgresql://localhost:5432/tenant_management_db}
    username: ${TENANT_MGMT_DB_USERNAME:postgres}
    password: ${TENANT_MGMT_DB_PASSWORD:root}
    driver-class-name: org.postgresql.Driver
  application:
    name: tenant-management-service
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

### Eureka Configuration

```yaml
eureka:
  client:
    enabled: ${EUREKA_ENABLED:false}
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://eureka-server:8761/eureka/}
    register-with-eureka: ${EUREKA_ENABLED:false}
    fetch-registry: ${EUREKA_ENABLED:false}
  instance:
    prefer-ip-address: true
```

## API Reference

### Base URL
```
http://localhost:9086/api
```

### Tenant Configuration Endpoints

#### Get Tenant Configuration
```http
GET /tenants/{tenantId}/config
```

**Response:**
```json
{
  "tenantId": "tenant-uuid",
  "branding": "{\"logo\":\"logo.png\",\"colors\":{\"primary\":\"#007bff\"}}",
  "appSubscriptions": ["app1", "app2", "app3"]
}
```

#### Update Tenant Configuration
```http
PUT /tenants/{tenantId}/config
Content-Type: application/json

{
  "branding": "{\"logo\":\"new-logo.png\",\"colors\":{\"primary\":\"#28a745\"}}",
  "appSubscriptions": ["app1", "app2", "app4"]
}
```

### Password Policy Endpoints

#### Get All Password Policies
```http
GET /tenant/password-policies
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
GET /tenant/password-policies/{id}
```

#### Get Default Password Policy
```http
GET /tenant/password-policies/default
```

#### Create Password Policy
```http
POST /tenant/password-policies
Content-Type: application/json

{
  "policyName": "Tenant Specific Policy",
  "description": "Custom password policy for tenant",
  "minLength": 12,
  "maxPasswordAge": 60,
  "minPasswordAge": 1,
  "passwordHistory": 6,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "[Random][Random][Random][Random]",
  "bannedPatterns": "password,123456,qwerty,admin,tenant",
  "isDefault": false
}
```

#### Update Password Policy
```http
PUT /tenant/password-policies/{id}
Content-Type: application/json

{
  "policyName": "Updated Tenant Policy",
  "description": "Updated password policy for tenant",
  "minLength": 12,
  "maxPasswordAge": 60,
  "minPasswordAge": 1,
  "passwordHistory": 6,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "[Random][Random][Random][Random]",
  "bannedPatterns": "password,123456,qwerty,admin,tenant",
  "isDefault": false
}
```

#### Delete Password Policy
```http
DELETE /tenant/password-policies/{id}
```

#### Set as Default Policy
```http
PUT /tenant/password-policies/{id}/default
```

## Data Models

### TenantConfig Entity
```java
@Entity
@Table(name = "tenant_config")
public class TenantConfig {
    @Id
    private String tenantId;

    @Column(columnDefinition = "text")
    private String branding; // JSON or simple string for now

    @ElementCollection
    @CollectionTable(name = "tenant_app_subscriptions", joinColumns = @JoinColumn(name = "tenant_id"))
    @Column(name = "app_id")
    private List<String> appSubscriptions;
}
```

### PasswordPolicyDto
```java
public class PasswordPolicyDto {
    private Long id;
    private String policyName;
    private String description;
    private int minLength = 10;
    private int maxPasswordAge = 90;
    private int minPasswordAge = 0;
    private int passwordHistory = 4;
    private boolean requireUppercase = true;
    private boolean requireLowercase = true;
    private boolean requireNumber = true;
    private boolean requireSpecial = true;
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    private String bannedPatterns = "password,123456,qwerty,admin";
    private boolean isActive = true;
    private boolean isDefault = false;
}
```

### CreatePasswordPolicyRequest
```java
public class CreatePasswordPolicyRequest {
    private String policyName;
    private String description;
    private int minLength = 10;
    private int maxPasswordAge = 90;
    private int minPasswordAge = 0;
    private int passwordHistory = 4;
    private boolean requireUppercase = true;
    private boolean requireLowercase = true;
    private boolean requireNumber = true;
    private boolean requireSpecial = true;
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    private String bannedPatterns = "password,123456,qwerty,admin";
    private boolean isDefault = false;
}
```

## Database Schema

### Tenant Config Table
```sql
CREATE TABLE tenant_config (
    tenant_id VARCHAR(255) PRIMARY KEY,
    branding TEXT
);
```

### Tenant App Subscriptions Table
```sql
CREATE TABLE tenant_app_subscriptions (
    tenant_id VARCHAR(255) NOT NULL,
    app_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (tenant_id, app_id),
    FOREIGN KEY (tenant_id) REFERENCES tenant_config(tenant_id)
);
```

## Feign Client Integration

### PlatformPasswordPolicyClient

The service integrates with the Platform Management Service using Feign client:

```java
@FeignClient(name = "platform-management-service")
public interface PlatformPasswordPolicyClient {
    
    @GetMapping("/api/password-policies")
    ResponseEntity<List<PasswordPolicyDto>> getAllPasswordPolicies();
    
    @GetMapping("/api/password-policies/{id}")
    ResponseEntity<PasswordPolicyDto> getPasswordPolicyById(@PathVariable("id") Long id);
    
    @GetMapping("/api/password-policies/default")
    ResponseEntity<PasswordPolicyDto> getDefaultPasswordPolicy();
    
    @PostMapping("/api/password-policies")
    ResponseEntity<PasswordPolicyDto> createPasswordPolicy(@RequestBody CreatePasswordPolicyRequest request);
    
    @PutMapping("/api/password-policies/{id}")
    ResponseEntity<PasswordPolicyDto> updatePasswordPolicy(
            @PathVariable("id") Long id, 
            @RequestBody CreatePasswordPolicyRequest request);
    
    @DeleteMapping("/api/password-policies/{id}")
    ResponseEntity<Void> deletePasswordPolicy(@PathVariable("id") Long id);
    
    @PutMapping("/api/password-policies/{id}/default")
    ResponseEntity<PasswordPolicyDto> setAsDefault(@PathVariable("id") Long id);
}
```

## Deployment

### Local Development

1. **Prerequisites:**
   - Java 21
   - PostgreSQL
   - Maven

2. **Database Setup:**
   ```sql
   CREATE DATABASE tenant_management_db;
   ```

3. **Environment Setup:**
   ```bash
   export TENANT_MGMT_DB_URL=jdbc:postgresql://localhost:5432/tenant_management_db
   export TENANT_MGMT_DB_USERNAME=postgres
   export TENANT_MGMT_DB_PASSWORD=your_password
   ```

4. **Run Service:**
   ```bash
   cd tenant-management-service
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build Image:**
   ```bash
   docker build -t onified/tenant-management-service .
   ```

2. **Run Container:**
   ```bash
   docker run -d \
     --name tenant-management-service \
     -p 9086:9086 \
     -e TENANT_MGMT_DB_URL=jdbc:postgresql://db:5432/tenant_management_db \
     -e TENANT_MGMT_DB_USERNAME=postgres \
     -e TENANT_MGMT_DB_PASSWORD=root \
     -e EUREKA_ENABLED=true \
     onified/tenant-management-service
   ```

### Docker Compose

The service is included in the main `docker-compose.yml`:

```yaml
tenant-management-service:
  build: ./tenant-management-service
  ports:
    - "9086:9086"
  environment:
    - TENANT_MGMT_DB_URL=jdbc:postgresql://postgres:5432/tenant_management_db
    - TENANT_MGMT_DB_USERNAME=postgres
    - TENANT_MGMT_DB_PASSWORD=root
    - EUREKA_ENABLED=true
  depends_on:
    - postgres
    - eureka-server
    - platform-management-service
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

The service uses default Spring Boot logging with:
- **Level:** INFO for root and Spring framework
- **Pattern:** Standard timestamp and message format
- **File:** Console output by default

### Log Files

- **Location:** `/app/logs/tenant-management-service.log` (Docker)
- **Rotation:** 10MB max size, 30 days retention
- **Total Size Cap:** 1GB

## Error Handling

### Exception Types

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `FeignException` | Various | External service communication errors |
| `GlobalException` | 500 | Unexpected server error |

### Error Response Format

```json
{
  "statusCode": 404,
  "status": "ERROR",
  "message": "Resource not found",
  "details": "uri=/api/tenants/invalid-id/config"
}
```

## Security Considerations

### Current Security Model

- **CSRF Protection:** Not configured (API-only service)
- **Session Management:** Stateless
- **Authentication:** Not implemented (delegated to API Gateway)
- **Authorization:** Not implemented (delegated to API Gateway)

### Security Recommendations

1. **Production Security:**
   - Implement JWT authentication
   - Add rate limiting
   - Use HTTPS
   - Implement tenant isolation

2. **Database Security:**
   - Use strong passwords
   - Limit database user permissions
   - Enable SSL connections
   - Implement row-level security

3. **Network Security:**
   - Use internal networks for service communication
   - Implement API gateway authentication
   - Monitor access logs
   - Validate tenant context

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

**Symptoms:**
- Service fails to start
- Database connection errors in logs

**Solutions:**
```bash
# Check database connectivity
psql -h localhost -U postgres -d tenant_management_db

# Verify environment variables
echo $TENANT_MGMT_DB_URL
echo $TENANT_MGMT_DB_USERNAME
echo $TENANT_MGMT_DB_PASSWORD
```

#### 2. Port Already in Use

**Symptoms:**
- `Port 9086 is already in use` error

**Solutions:**
```bash
# Find process using port
lsof -i :9086

# Kill process or change port
export TENANT_MGMT_PORT=9087
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

#### 4. Feign Client Issues

**Symptoms:**
- Password policy operations failing
- External service communication errors

**Solutions:**
```bash
# Check Platform Management Service availability
curl http://platform-management-service:9081/actuator/health

# Verify service discovery
curl http://eureka-server:8761/eureka/apps/platform-management-service

# Check Feign client configuration
# Ensure proper service name mapping
```

#### 5. Tenant Configuration Issues

**Symptoms:**
- Tenant config not found
- Configuration update failures

**Solutions:**
```bash
# Check tenant configuration
curl http://localhost:9086/api/tenants/tenant-id/config

# Verify database schema
psql -h localhost -U postgres -d tenant_management_db -c "\dt"

# Check for tenant isolation issues
# Ensure proper tenant context
```

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.onified.ai.tenant_management: DEBUG
    org.springframework.web: DEBUG
    org.springframework.cloud.openfeign: DEBUG
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

4. **Feign Client Performance:**
   - External service response times
   - Circuit breaker status
   - Retry attempts

#### Monitoring Tools

- **Spring Boot Actuator:** Built-in health checks
- **Micrometer:** Metrics collection
- **Prometheus:** Metrics storage
- **Grafana:** Visualization

## Integration Examples

### Managing Tenant Configuration

```bash
# Get tenant configuration
curl http://localhost:9086/api/tenants/tenant-123/config

# Update tenant configuration
curl -X PUT http://localhost:9086/api/tenants/tenant-123/config \
  -H "Content-Type: application/json" \
  -d '{
    "branding": "{\"logo\":\"custom-logo.png\",\"colors\":{\"primary\":\"#ff6b35\"}}",
    "appSubscriptions": ["crm", "hr", "finance"]
  }'
```

### Managing Password Policies

```bash
# Get all password policies
curl http://localhost:9086/api/tenant/password-policies

# Get default password policy
curl http://localhost:9086/api/tenant/password-policies/default

# Create custom password policy
curl -X POST http://localhost:9086/api/tenant/password-policies \
  -H "Content-Type: application/json" \
  -d '{
    "policyName": "Tenant Custom Policy",
    "description": "Custom password policy for tenant",
    "minLength": 12,
    "maxPasswordAge": 60,
    "minPasswordAge": 1,
    "passwordHistory": 6,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSpecial": true,
    "initialPasswordFormat": "[Random][Random][Random][Random]",
    "bannedPatterns": "password,123456,qwerty,admin,tenant",
    "isDefault": false
  }'
```

### Tenant-Specific Operations

```bash
# Get tenant app subscriptions
curl http://localhost:9086/api/tenants/tenant-123/config | jq '.appSubscriptions'

# Update tenant branding
curl -X PUT http://localhost:9086/api/tenants/tenant-123/config \
  -H "Content-Type: application/json" \
  -d '{
    "branding": "{\"logo\":\"new-logo.png\",\"colors\":{\"primary\":\"#28a745\",\"secondary\":\"#6c757d\"}}",
    "appSubscriptions": ["crm", "hr", "finance", "analytics"]
  }'
```

## Best Practices

### Development

1. **Code Organization:**
   - Use DTOs for API requests/responses
   - Implement proper exception handling
   - Follow REST conventions
   - Use Feign clients for external service communication

2. **Database Design:**
   - Use meaningful table and column names
   - Implement proper foreign key constraints
   - Add indexes for frequently queried columns
   - Ensure tenant isolation

3. **API Design:**
   - Use consistent response formats
   - Implement proper HTTP status codes
   - Add comprehensive error messages
   - Include tenant context in URLs

### Operations

1. **Deployment:**
   - Use environment-specific configurations
   - Implement health checks
   - Set up proper logging
   - Ensure service dependencies are available

2. **Monitoring:**
   - Monitor application metrics
   - Set up alerting for critical issues
   - Regular log analysis
   - Monitor external service dependencies

3. **Security:**
   - Regular security updates
   - Access control implementation
   - Data encryption in transit and at rest
   - Tenant isolation enforcement

### Multi-Tenancy

1. **Tenant Isolation:**
   - Ensure data separation between tenants
   - Implement proper tenant context validation
   - Use tenant-specific database schemas or row-level security

2. **Configuration Management:**
   - Provide tenant-specific configuration defaults
   - Allow tenant customization where appropriate
   - Maintain configuration versioning

3. **Service Integration:**
   - Use Feign clients for external service communication
   - Implement proper error handling for external services
   - Consider circuit breakers for external service calls

## Related Services

- **Eureka Server:** Service discovery
- **API Gateway:** Request routing and security
- **Platform Management Service:** Platform-wide configuration and password policies
- **Authentication Service:** User authentication
- **User Management Service:** User management
- **Application Config Service:** Application configuration
- **Permission Registry Service:** Permission management 