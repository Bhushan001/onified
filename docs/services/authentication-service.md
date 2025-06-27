# Authentication Service

## Overview

The Authentication Service is a critical microservice in the Onified platform that handles user authentication, authorization, and session management. It provides JWT-based authentication, integrates with Keycloak for identity management, and supports multi-tenant authentication with configurable password policies.

**Service Name:** `authentication-service`  
**Port:** `9083` (configurable via `AUTH_PORT`)  
**Database:** PostgreSQL (`auth_db`)

## Architecture

### Core Components

- **JWT Authentication**: Token-based authentication with configurable expiration
- **Keycloak Integration**: External identity provider integration
- **Password Policy Validation**: Integration with platform password policies
- **Multi-tenant Support**: Tenant-aware authentication and authorization
- **Service Discovery**: Eureka client integration (conditional)
- **Feign Client Integration**: HTTP client for inter-service communication

### Key Features

- JWT token generation and validation
- Keycloak OAuth2 integration
- Password policy enforcement
- Multi-tenant authentication
- Session management
- User authentication and authorization
- Integration with User Management Service

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
| `spring-cloud-starter-openfeign` | HTTP client for service communication |
| `spring-cloud-starter-netflix-eureka-client` | Service discovery |
| `postgresql` | Database driver |
| `lombok` | Code generation |
| `keycloak-spring-boot-starter` | Keycloak integration |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `AUTH_PORT` | `9083` | Service port |
| `AUTH_DB_URL` | `jdbc:postgresql://localhost:5432/auth_db` | Database URL |
| `AUTH_DB_USERNAME` | `postgres` | Database username |
| `AUTH_DB_PASSWORD` | `root` | Database password |
| `EUREKA_ENABLED` | `false` | Enable Eureka client |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://eureka-server:8761/eureka/` | Eureka server URL |
| `USER_MANAGEMENT_URL` | `http://localhost:9085` | User Management Service URL |
| `USER_MGMT_PORT` | `9085` | User Management Service port |
| `KEYCLOAK_AUTH_SERVER_URL` | `http://localhost:9090` | Keycloak server URL |
| `KEYCLOAK_REALM` | `onified` | Keycloak realm |
| `KEYCLOAK_CLIENT_ID` | `onified-auth-service` | Keycloak client ID |
| `KEYCLOAK_CLIENT_SECRET` | `your-client-secret-here` | Keycloak client secret |

### Database Configuration

```yaml
spring:
  datasource:
    url: ${AUTH_DB_URL:jdbc:postgresql://localhost:5432/auth_db}
    username: ${AUTH_DB_USERNAME:postgres}
    password: ${AUTH_DB_PASSWORD:root}
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

### JWT Configuration

```yaml
jwt:
  secret: yourVerySecretKeyForJWTTokenGenerationThatShouldBeLongAndComplex12345!@#$%^&*()
  expiration: 3600000 # 1 hour in milliseconds
```

### Feign Client Configuration

```yaml
feign:
  client:
    config:
      user-management-service:
        url: ${USER_MANAGEMENT_URL:http://localhost:${USER_MGMT_PORT:9085}}
```

### Keycloak Configuration

```yaml
keycloak:
  auth-server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://localhost:9090}
  realm: ${KEYCLOAK_REALM:onified}
  client-id: ${KEYCLOAK_CLIENT_ID:onified-auth-service}
  client-secret: ${KEYCLOAK_CLIENT_SECRET:your-client-secret-here}
  admin:
    username: admin
    password: admin123
    realm: master
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
```

## API Reference

### Base URL
```
http://localhost:9083/api
```

### Authentication Endpoints

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "tenantId": "string"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": {
    "token": "jwt-token-string",
    "refreshToken": "refresh-token-string",
    "expiresIn": 3600,
    "user": {
      "userId": "uuid",
      "username": "string",
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "roles": ["ROLE_USER", "ROLE_ADMIN"]
    }
  }
}
```

#### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "refresh-token-string"
}
```

#### Logout
```http
POST /auth/logout
Authorization: Bearer jwt-token-string
```

#### Validate Token
```http
GET /auth/validate
Authorization: Bearer jwt-token-string
```

**Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": {
    "valid": true,
    "user": {
      "userId": "uuid",
      "username": "string",
      "email": "string",
      "firstName": "string",
      "lastName": "string",
      "roles": ["ROLE_USER", "ROLE_ADMIN"]
    }
  }
}
```

#### Change Password
```http
POST /auth/change-password
Authorization: Bearer jwt-token-string
Content-Type: application/json

{
  "currentPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

#### Forgot Password
```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "string",
  "tenantId": "string"
}
```

#### Reset Password
```http
POST /auth/reset-password
Content-Type: application/json

{
  "token": "reset-token-string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

### OAuth2 Endpoints (Keycloak Integration)

#### OAuth2 Login
```http
GET /oauth2/authorization/keycloak
```

#### OAuth2 Callback
```http
GET /login/oauth2/code/keycloak
```

#### OAuth2 Logout
```http
POST /oauth2/logout
Authorization: Bearer jwt-token-string
```

## Data Models

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String tenantId;
    private boolean enabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;
}
```

### AuthenticationRequest
```java
public class AuthenticationRequest {
    private String username;
    private String password;
    private String tenantId;
}
```

### AuthenticationResponse
```java
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
    private long expiresIn;
    private UserDto user;
}
```

### UserDto
```java
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private String tenantId;
}
```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Refresh Tokens Table
```sql
CREATE TABLE refresh_tokens (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

## Feign Client Integration

### UserManagementServiceClient

The service integrates with the User Management Service using Feign client:

```java
@FeignClient(name = "user-management-service")
public interface UserManagementServiceClient {
    
    @GetMapping("/api/users/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userId") String userId);
    
    @GetMapping("/api/users/username/{username}")
    ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username);
    
    @PostMapping("/api/users")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request);
    
    @PutMapping("/api/users/{userId}")
    ResponseEntity<UserDto> updateUser(@PathVariable("userId") String userId, @RequestBody UpdateUserRequest request);
    
    @DeleteMapping("/api/users/{userId}")
    ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId);
}
```

## Deployment

### Local Development

1. **Prerequisites:**
   - Java 21
   - PostgreSQL
   - Keycloak (optional)
   - Maven

2. **Database Setup:**
   ```sql
   CREATE DATABASE auth_db;
   ```

3. **Environment Setup:**
   ```bash
   export AUTH_DB_URL=jdbc:postgresql://localhost:5432/auth_db
   export AUTH_DB_USERNAME=postgres
   export AUTH_DB_PASSWORD=your_password
   export USER_MANAGEMENT_URL=http://localhost:9085
   ```

4. **Run Service:**
   ```bash
   cd authentication-service
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build Image:**
   ```bash
   docker build -t onified/authentication-service .
   ```

2. **Run Container:**
   ```bash
   docker run -d \
     --name authentication-service \
     -p 9083:9083 \
     -e AUTH_DB_URL=jdbc:postgresql://db:5432/auth_db \
     -e AUTH_DB_USERNAME=postgres \
     -e AUTH_DB_PASSWORD=root \
     -e USER_MANAGEMENT_URL=http://user-management-service:9085 \
     -e EUREKA_ENABLED=true \
     -e KEYCLOAK_AUTH_SERVER_URL=http://keycloak:9090 \
     onified/authentication-service
   ```

### Docker Compose

The service is included in the main `docker-compose.yml`:

```yaml
authentication-service:
  build: ./authentication-service
  ports:
    - "9083:9083"
  environment:
    - AUTH_DB_URL=jdbc:postgresql://postgres:5432/auth_db
    - AUTH_DB_USERNAME=postgres
    - AUTH_DB_PASSWORD=root
    - USER_MANAGEMENT_URL=http://user-management-service:9085
    - EUREKA_ENABLED=true
    - KEYCLOAK_AUTH_SERVER_URL=http://keycloak:9090
    - KEYCLOAK_REALM=onified
    - KEYCLOAK_CLIENT_ID=onified-auth-service
    - KEYCLOAK_CLIENT_SECRET=your-client-secret-here
  depends_on:
    - postgres
    - eureka-server
    - user-management-service
    - keycloak
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
    com.onified.ai.authentication_service: DEBUG
    org.springframework.security: DEBUG
    org.keycloak: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: ${LOG_DIR:-/app/logs}/authentication-service.log
    max-size: 10MB
    max-history: 30
    total-size-cap: 1GB
```

### Log Files

- **Location:** `/app/logs/authentication-service.log` (Docker)
- **Rotation:** 10MB max size, 30 days retention
- **Total Size Cap:** 1GB

## Error Handling

### Exception Types

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `AuthenticationException` | 401 | Authentication failed |
| `BadCredentialsException` | 401 | Invalid credentials |
| `UserNotFoundException` | 404 | User not found |
| `TokenExpiredException` | 401 | JWT token expired |
| `InvalidTokenException` | 401 | Invalid JWT token |
| `PasswordPolicyViolationException` | 400 | Password policy violation |

### Error Response Format

```json
{
  "statusCode": 401,
  "status": "ERROR",
  "message": "Authentication failed",
  "details": "Invalid username or password"
}
```

## Security Considerations

### Current Security Model

- **JWT Authentication:** Token-based authentication
- **Password Hashing:** BCrypt password hashing
- **Token Expiration:** Configurable JWT expiration
- **Refresh Tokens:** Secure token refresh mechanism
- **Keycloak Integration:** OAuth2/OIDC support

### Security Recommendations

1. **Production Security:**
   - Use strong JWT secrets
   - Implement token blacklisting
   - Add rate limiting
   - Use HTTPS
   - Implement proper CORS

2. **Database Security:**
   - Use strong passwords
   - Limit database user permissions
   - Enable SSL connections
   - Encrypt sensitive data

3. **Network Security:**
   - Use internal networks for service communication
   - Implement API gateway authentication
   - Monitor access logs
   - Validate all inputs

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues

**Symptoms:**
- Service fails to start
- Database connection errors in logs

**Solutions:**
```bash
# Check database connectivity
psql -h localhost -U postgres -d auth_db

# Verify environment variables
echo $AUTH_DB_URL
echo $AUTH_DB_USERNAME
echo $AUTH_DB_PASSWORD
```

#### 2. JWT Token Issues

**Symptoms:**
- Token validation failures
- Token expiration errors

**Solutions:**
```bash
# Check JWT configuration
echo $JWT_SECRET
echo $JWT_EXPIRATION

# Verify token format
# Use JWT debugger to decode tokens
```

#### 3. Keycloak Integration Issues

**Symptoms:**
- OAuth2 authentication failures
- Keycloak connection errors

**Solutions:**
```bash
# Check Keycloak configuration
echo $KEYCLOAK_AUTH_SERVER_URL
echo $KEYCLOAK_REALM
echo $KEYCLOAK_CLIENT_ID

# Verify Keycloak is running
curl http://keycloak:9090/health
```

#### 4. User Management Service Issues

**Symptoms:**
- User lookup failures
- User creation/update errors

**Solutions:**
```bash
# Check User Management Service
curl http://user-management-service:9085/actuator/health

# Verify Feign client configuration
echo $USER_MANAGEMENT_URL
```

### Debug Mode

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.onified.ai.authentication_service: DEBUG
    org.springframework.security: DEBUG
    org.keycloak: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

### Performance Monitoring

#### Key Metrics to Monitor

1. **Authentication Performance:**
   - Login response times
   - Token validation times
   - Password hashing performance

2. **Security Metrics:**
   - Failed login attempts
   - Token expiration rates
   - Password policy violations

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

### User Authentication

```bash
# Login
curl -X POST http://localhost:9083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePassword123!",
    "tenantId": "tenant-123"
  }'

# Validate token
curl -X GET http://localhost:9083/api/auth/validate \
  -H "Authorization: Bearer jwt-token-here"

# Refresh token
curl -X POST http://localhost:9083/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "refresh-token-here"
  }'
```

### Password Management

```bash
# Change password
curl -X POST http://localhost:9083/api/auth/change-password \
  -H "Authorization: Bearer jwt-token-here" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "OldPassword123!",
    "newPassword": "NewSecurePassword456!",
    "confirmPassword": "NewSecurePassword456!"
  }'

# Forgot password
curl -X POST http://localhost:9083/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "tenantId": "tenant-123"
  }'
```

### OAuth2 Authentication

```bash
# OAuth2 login redirect
curl -L http://localhost:9083/oauth2/authorization/keycloak

# OAuth2 logout
curl -X POST http://localhost:9083/oauth2/logout \
  -H "Authorization: Bearer jwt-token-here"
```

## Best Practices

### Development

1. **Code Organization:**
   - Use DTOs for API requests/responses
   - Implement proper exception handling
   - Follow security best practices
   - Use Feign clients for external service communication

2. **Security Implementation:**
   - Use strong password hashing (BCrypt)
   - Implement proper JWT token management
   - Add comprehensive input validation
   - Follow OWASP security guidelines

3. **API Design:**
   - Use consistent response formats
   - Implement proper HTTP status codes
   - Add comprehensive error messages
   - Include security headers

### Operations

1. **Deployment:**
   - Use environment-specific configurations
   - Implement health checks
   - Set up proper logging
   - Ensure service dependencies are available

2. **Monitoring:**
   - Monitor authentication metrics
   - Set up alerting for security events
   - Regular log analysis
   - Monitor external service dependencies

3. **Security:**
   - Regular security updates
   - Access control implementation
   - Data encryption in transit and at rest
   - Regular security audits

### Authentication

1. **Token Management:**
   - Implement proper token expiration
   - Use refresh tokens for long sessions
   - Implement token blacklisting
   - Secure token storage

2. **Password Security:**
   - Enforce strong password policies
   - Implement password history
   - Add password complexity validation
   - Secure password reset process

3. **Multi-tenancy:**
   - Ensure tenant isolation
   - Validate tenant context
   - Implement tenant-specific policies
   - Secure cross-tenant access

## Related Services

- **Eureka Server:** Service discovery
- **API Gateway:** Request routing and security
- **User Management Service:** User data management
- **Platform Management Service:** Password policies
- **Keycloak:** Identity provider
- **Permission Registry Service:** Permission management 