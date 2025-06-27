# User Management Service

## 📋 Overview

The User Management Service is responsible for managing user accounts, profiles, and user-related operations in the Onified platform. It provides comprehensive user lifecycle management including user creation, updates, deletion, and profile management with integration to the permission system.

## 🎯 Purpose

- **User CRUD Operations**: Create, read, update, and delete user accounts
- **Profile Management**: Manage user profiles and personal information
- **User Lifecycle**: Handle user registration, activation, deactivation, and deletion
- **Password Management**: Handle password policies, resets, and validation
- **User Search**: Provide advanced user search and filtering capabilities
- **Audit Trail**: Maintain comprehensive audit logs for user operations
- **Integration**: Integrate with permission registry and authentication services

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │ User Management │    │ Permission      │
│                 │◄──►│    Service      │◄──►│ Registry        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   PostgreSQL    │
                       │   (User Data)   │
                       └─────────────────┘
```

## 📦 Spring Boot Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-parent` | 3.2.5 | Spring Boot parent POM |
| `spring-boot-starter-web` | 3.2.5 | Web application support |
| `spring-boot-starter-data-jpa` | 3.2.5 | JPA/Hibernate support |
| `spring-boot-starter-security` | 3.2.5 | Security framework |
| `spring-boot-starter-actuator` | 3.2.5 | Health checks and metrics |
| `spring-boot-starter-validation` | 3.2.5 | Input validation |
| `spring-cloud-starter-netflix-eureka-client` | 2023.0.1 | Service discovery |
| `spring-cloud-starter-openfeign` | 2023.0.1 | HTTP client |
| `postgresql` | 42.7.0 | PostgreSQL driver |
| `lombok` | 1.18.32 | Code generation |
| `jackson-databind` | 2.15.0 | JSON processing |
| `spring-boot-starter-test` | 3.2.5 | Testing support |

## ⚙️ Configuration

### Application Properties

```yaml
spring:
  config:
    import: optional:.env[.properties]
  application:
    name: user-management-service
  datasource:
    url: ${USER_MGMT_DB_URL:jdbc:postgresql://localhost:5432/user_mgmt_db}
    username: ${USER_MGMT_DB_USERNAME:postgres}
    password: ${USER_MGMT_DB_PASSWORD:root}
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

server:
  port: ${USER_MGMT_PORT:9085}

feign:
  client:
    config:
      permission-registry-service:
        url: ${PERMISSION_REGISTRY_URL:http://localhost:9084}

eureka:
  client:
    enabled: ${EUREKA_ENABLED:false}
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://eureka-server:8761/eureka/}
    register-with-eureka: ${EUREKA_ENABLED:false}
    fetch-registry: ${EUREKA_ENABLED:false}

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    root: INFO
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
    com.onified.ai.ums: DEBUG
    com.onified.ai.ums.client.PermissionRegistryFeignClient: DEBUG
    org.springframework.cloud.openfeign: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: ${LOG_DIR:-/app/logs}/user-management-service.log
    max-size: 10MB
    max-history: 30
    total-size-cap: 1GB
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `USER_MGMT_PORT` | 9085 | Port on which service runs |
| `USER_MGMT_DB_URL` | jdbc:postgresql://localhost:5432/user_mgmt_db | Database URL |
| `USER_MGMT_DB_USERNAME` | postgres | Database username |
| `USER_MGMT_DB_PASSWORD` | root | Database password |
| `PERMISSION_REGISTRY_URL` | http://localhost:9084 | Permission registry service URL |
| `EUREKA_ENABLED` | false | Enable/disable Eureka client |
| `LOG_DIR` | /app/logs | Log directory |

## 🚀 Deployment

### Docker Deployment

```yaml
user-management-service:
  build: ./user-management-service
  container_name: user-management-service
  ports:
    - "${USER_MGMT_PORT:-9085}:9085"
  environment:
    - SPRING_APPLICATION_NAME=user-management-service
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    - EUREKA_ENABLED=true
    - LOG_DIR=${USER_MGMT_LOG_DIR:-./logs/user-management}
    - USER_MGMT_DB_URL=${USER_MGMT_DB_URL}
    - USER_MGMT_DB_USERNAME=${USER_MGMT_DB_USERNAME}
    - USER_MGMT_DB_PASSWORD=${USER_MGMT_DB_PASSWORD}
  volumes:
    - ${USER_MGMT_LOG_DIR:-./logs/user-management}:/app/logs
  depends_on:
    - eureka-server
    - postgres
```

### Local Development

```bash
cd user-management-service
mvn spring-boot:run
```

## 🔍 API Reference

### User Management Endpoints

#### Get All Users
```
GET /api/users
Authorization: Bearer <access_token>

Query Parameters:
- page: Page number (default: 0)
- size: Page size (default: 20)
- sort: Sort field (default: id)
- direction: Sort direction (ASC/DESC)

Response:
{
  "content": [
    {
      "id": "12345",
      "username": "john.doe",
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "enabled": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0
}
```

#### Get User by ID
```
GET /api/users/{id}
Authorization: Bearer <access_token>

Response:
{
  "id": "12345",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "enabled": true,
  "roles": ["USER", "ADMIN"],
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Create User
```
POST /api/users
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "username": "jane.smith",
  "email": "jane.smith@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+1234567890",
  "password": "SecurePassword123!",
  "roles": ["USER"]
}

Response:
{
  "id": "12346",
  "username": "jane.smith",
  "email": "jane.smith@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "enabled": true,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### Update User
```
PUT /api/users/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith-Jones",
  "phone": "+1234567891",
  "enabled": true
}

Response:
{
  "id": "12346",
  "username": "jane.smith",
  "email": "jane.smith@example.com",
  "firstName": "Jane",
  "lastName": "Smith-Jones",
  "phone": "+1234567891",
  "enabled": true,
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Delete User
```
DELETE /api/users/{id}
Authorization: Bearer <access_token>

Response:
{
  "message": "User deleted successfully"
}
```

#### Search Users
```
GET /api/users/search
Authorization: Bearer <access_token>

Query Parameters:
- q: Search query
- email: Email filter
- enabled: Enabled status filter
- role: Role filter

Response:
{
  "content": [
    {
      "id": "12345",
      "username": "john.doe",
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "enabled": true
    }
  ],
  "totalElements": 1
}
```

#### Change Password
```
POST /api/users/{id}/change-password
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword123!"
}

Response:
{
  "message": "Password changed successfully"
}
```

#### Reset Password
```
POST /api/users/{id}/reset-password
Authorization: Bearer <access_token>

Response:
{
  "message": "Password reset email sent",
  "resetToken": "reset-token-here"
}
```

## 🛡️ Security Configuration

### User Entity Security
```java
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    private String phone;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Password Policy Configuration
```java
@Component
public class PasswordPolicyService {
    
    public boolean validatePassword(String password) {
        // Minimum 8 characters
        if (password.length() < 8) {
            return false;
        }
        
        // At least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // At least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // At least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // At least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        
        return true;
    }
}
```

## 📊 Monitoring and Health

### Health Check Endpoint
- **URL**: http://localhost:9085/actuator/health
- **Method**: GET
- **Response**: JSON with health status

### Metrics Endpoints
- **Health**: http://localhost:9085/actuator/health
- **Info**: http://localhost:9085/actuator/info
- **Metrics**: http://localhost:9085/actuator/metrics

### Database Health Check
```bash
# Check database connection
curl http://localhost:9085/actuator/health

# Check specific health indicators
curl http://localhost:9085/actuator/health/db
```

## 🔄 Integration with Permission Registry

### Feign Client Configuration
```java
@FeignClient(name = "permission-registry-service", url = "${feign.client.config.permission-registry-service.url}")
public interface PermissionRegistryFeignClient {
    
    @GetMapping("/api/permissions/users/{userId}")
    List<Permission> getUserPermissions(@PathVariable("userId") String userId);
    
    @PostMapping("/api/permissions/users/{userId}/roles")
    void assignRolesToUser(@PathVariable("userId") String userId, @RequestBody List<String> roles);
    
    @DeleteMapping("/api/permissions/users/{userId}/roles")
    void removeRolesFromUser(@PathVariable("userId") String userId, @RequestBody List<String> roles);
}
```

### Permission Integration
```java
@Service
public class UserService {
    
    @Autowired
    private PermissionRegistryFeignClient permissionClient;
    
    public UserDTO createUser(CreateUserRequest request) {
        // Create user
        User user = userRepository.save(mapToUser(request));
        
        // Assign permissions
        if (request.getRoles() != null) {
            permissionClient.assignRolesToUser(user.getId().toString(), request.getRoles());
        }
        
        return mapToDTO(user);
    }
    
    public UserDTO getUserWithPermissions(String userId) {
        User user = userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Get user permissions
        List<Permission> permissions = permissionClient.getUserPermissions(userId);
        
        UserDTO dto = mapToDTO(user);
        dto.setPermissions(permissions);
        
        return dto;
    }
}
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check database connection
docker-compose logs postgres

# Test database connection
docker-compose exec postgres psql -U postgres -d user_mgmt_db -c "SELECT 1;"
```

#### 2. Permission Registry Integration Issues
```bash
# Check permission registry service
docker-compose logs permission-registry-service

# Test permission registry connection
curl http://localhost:9084/actuator/health
```

#### 3. User Creation Issues
```bash
# Check user management service logs
docker-compose logs -f user-management-service

# Test user creation endpoint
curl -X POST http://localhost:9085/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"username":"test","email":"test@example.com","password":"Password123!"}'
```

### Log Analysis
```bash
# View user management service logs
docker-compose logs -f user-management-service

# Filter for user operations
docker-compose logs user-management-service | grep "User"
```

## 📈 Performance Tuning

### JVM Settings
```bash
-Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC"
```

### Database Optimization
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        connection:
          pool_size: 10
```

### Caching Configuration
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "user-permissions");
    }
}
```

## 📚 Best Practices

### 1. Security
- Implement proper input validation
- Use password hashing (BCrypt)
- Implement role-based access control
- Audit all user operations

### 2. Performance
- Use pagination for large datasets
- Implement caching for frequently accessed data
- Optimize database queries
- Use connection pooling

### 3. Data Integrity
- Implement proper validation
- Use database constraints
- Handle concurrent updates
- Maintain audit trails

### 4. Monitoring
- Monitor user creation/deletion rates
- Track authentication failures
- Monitor database performance
- Alert on security events

## 🔗 Related Services

- **Authentication Service**: User authentication
- **Permission Registry Service**: User permissions and roles
- **API Gateway**: Request routing
- **Eureka Server**: Service discovery

## 📄 Configuration Files

### Main Configuration
- `application.yml`: Main application configuration
- `pom.xml`: Maven dependencies and build configuration
- `Dockerfile`: Container configuration

### Service Configuration
- `UserController.java`: REST endpoints
- `UserService.java`: Business logic
- `UserRepository.java`: Data access layer
- `PermissionRegistryFeignClient.java`: Permission service integration

### Security Configuration
- `SecurityConfig.java`: Security configuration
- `PasswordPolicyService.java`: Password validation
- `UserDetailsService.java`: User details service 