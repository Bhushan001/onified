# Permission Registry Service

## 📋 Overview

The Permission Registry Service is responsible for managing role-based access control (RBAC) and attribute-based access control (ABAC) in the Onified platform. It provides comprehensive permission management, role definitions, and access control policies for all microservices.

## 🎯 Purpose

- **Permission Management**: Define, create, update, and delete permissions
- **Role Management**: Manage roles and role hierarchies
- **Access Control**: Implement RBAC and ABAC policies
- **Permission Assignment**: Assign permissions to users and roles
- **Policy Enforcement**: Enforce access control policies
- **Audit Trail**: Maintain comprehensive audit logs for permission changes
- **Integration**: Provide permission validation for other services

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │ Permission      │    │ Application     │
│                 │◄──►│ Registry        │◄──►│ Config Service  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   PostgreSQL    │
                       │ (Permission DB) │
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
server:
  port: ${PERMISSION_REGISTRY_PORT:9084}

spring:
  config:
    import: optional:.env[.properties]
  application:
    name: permission-registry-service
  datasource:
    url: ${PERMISSION_DB_URL:jdbc:postgresql://localhost:5432/permission_db}
    username: ${PERMISSION_DB_USERNAME:postgres}
    password: ${PERMISSION_DB_PASSWORD:root}
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

feign:
  client:
    config:
      application-config-service:
        url: ${APPLICATION_CONFIG_URL:http://localhost:9082}

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
    org.springframework: INFO
    org.springframework.web: DEBUG
    org.springframework.jdbc.core: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    com.onified.permission: DEBUG
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
  file:
    name: ${LOG_DIR:-/app/logs}/permission-registry-service.log
    max-size: 10MB
    max-history: 30
    total-size-cap: 1GB
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PERMISSION_REGISTRY_PORT` | 9084 | Port on which service runs |
| `PERMISSION_DB_URL` | jdbc:postgresql://localhost:5432/permission_db | Database URL |
| `PERMISSION_DB_USERNAME` | postgres | Database username |
| `PERMISSION_DB_PASSWORD` | root | Database password |
| `APPLICATION_CONFIG_URL` | http://localhost:9082 | Application config service URL |
| `EUREKA_ENABLED` | false | Enable/disable Eureka client |
| `LOG_DIR` | /app/logs | Log directory |

## 🚀 Deployment

### Docker Deployment

```yaml
permission-registry-service:
  build: ./permission-registry-service
  container_name: permission-registry-service
  ports:
    - "${PERMISSION_REGISTRY_PORT:-9084}:9084"
  environment:
    - SPRING_APPLICATION_NAME=permission-registry-service
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    - EUREKA_ENABLED=true
    - LOG_DIR=${PERMISSION_REGISTRY_LOG_DIR:-./logs/permission-registry}
    - PERMISSION_DB_URL=${PERMISSION_DB_URL}
    - PERMISSION_DB_USERNAME=${PERMISSION_DB_USERNAME}
    - PERMISSION_DB_PASSWORD=${PERMISSION_DB_PASSWORD}
  volumes:
    - ${PERMISSION_REGISTRY_LOG_DIR:-./logs/permission-registry}:/app/logs
  depends_on:
    - eureka-server
    - postgres
```

### Local Development

```bash
cd permission-registry-service
mvn spring-boot:run
```

## 🔍 API Reference

### Permission Management Endpoints

#### Get All Permissions
```
GET /api/permissions
Authorization: Bearer <access_token>

Query Parameters:
- page: Page number (default: 0)
- size: Page size (default: 20)
- sort: Sort field (default: id)

Response:
{
  "content": [
    {
      "id": "1",
      "name": "USER_READ",
      "description": "Read user information",
      "resource": "USER",
      "action": "READ",
      "enabled": true,
      "createdAt": "2024-01-01T00:00:00Z"
    }
  ],
  "totalElements": 50,
  "totalPages": 3
}
```

#### Get Permission by ID
```
GET /api/permissions/{id}
Authorization: Bearer <access_token>

Response:
{
  "id": "1",
  "name": "USER_READ",
  "description": "Read user information",
  "resource": "USER",
  "action": "READ",
  "enabled": true,
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Create Permission
```
POST /api/permissions
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "USER_CREATE",
  "description": "Create new users",
  "resource": "USER",
  "action": "CREATE"
}

Response:
{
  "id": "2",
  "name": "USER_CREATE",
  "description": "Create new users",
  "resource": "USER",
  "action": "CREATE",
  "enabled": true,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### Update Permission
```
PUT /api/permissions/{id}
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "description": "Create and manage new users",
  "enabled": true
}

Response:
{
  "id": "2",
  "name": "USER_CREATE",
  "description": "Create and manage new users",
  "resource": "USER",
  "action": "CREATE",
  "enabled": true,
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

#### Delete Permission
```
DELETE /api/permissions/{id}
Authorization: Bearer <access_token>

Response:
{
  "message": "Permission deleted successfully"
}
```

### Role Management Endpoints

#### Get All Roles
```
GET /api/roles
Authorization: Bearer <access_token>

Response:
{
  "content": [
    {
      "id": "1",
      "name": "ADMIN",
      "description": "Administrator role",
      "enabled": true,
      "permissions": ["USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE"]
    }
  ],
  "totalElements": 10
}
```

#### Create Role
```
POST /api/roles
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "MODERATOR",
  "description": "Moderator role with limited permissions",
  "permissions": ["USER_READ", "USER_UPDATE"]
}

Response:
{
  "id": "2",
  "name": "MODERATOR",
  "description": "Moderator role with limited permissions",
  "enabled": true,
  "permissions": ["USER_READ", "USER_UPDATE"],
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### Assign Permissions to Role
```
POST /api/roles/{roleId}/permissions
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "permissionIds": ["1", "2", "3"]
}

Response:
{
  "message": "Permissions assigned successfully"
}
```

### User Permission Endpoints

#### Get User Permissions
```
GET /api/permissions/users/{userId}
Authorization: Bearer <access_token>

Response:
{
  "userId": "12345",
  "permissions": [
    {
      "id": "1",
      "name": "USER_READ",
      "resource": "USER",
      "action": "READ"
    },
    {
      "id": "2",
      "name": "USER_CREATE",
      "resource": "USER",
      "action": "CREATE"
    }
  ],
  "roles": ["ADMIN", "USER"]
}
```

#### Assign Roles to User
```
POST /api/permissions/users/{userId}/roles
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "roles": ["ADMIN", "MODERATOR"]
}

Response:
{
  "message": "Roles assigned successfully"
}
```

#### Remove Roles from User
```
DELETE /api/permissions/users/{userId}/roles
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "roles": ["MODERATOR"]
}

Response:
{
  "message": "Roles removed successfully"
}
```

#### Check User Permission
```
POST /api/permissions/check
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "userId": "12345",
  "resource": "USER",
  "action": "CREATE"
}

Response:
{
  "hasPermission": true,
  "permission": {
    "id": "2",
    "name": "USER_CREATE",
    "resource": "USER",
    "action": "CREATE"
  }
}
```

## 🛡️ Security Configuration

### Permission Entity
```java
@Entity
@Table(name = "permissions")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String resource;
    
    @Column(nullable = false)
    private String action;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Role Entity
```java
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Permission Service
```java
@Service
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public boolean hasPermission(String userId, String resource, String action) {
        // Get user roles
        Set<Role> userRoles = getUserRoles(userId);
        
        // Check if any role has the required permission
        return userRoles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(permission -> 
                permission.getResource().equals(resource) &&
                permission.getAction().equals(action) &&
                permission.isEnabled()
            );
    }
    
    public Set<Permission> getUserPermissions(String userId) {
        Set<Role> userRoles = getUserRoles(userId);
        return userRoles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .filter(Permission::isEnabled)
            .collect(Collectors.toSet());
    }
}
```

## 📊 Monitoring and Health

### Health Check Endpoint
- **URL**: http://localhost:9084/actuator/health
- **Method**: GET
- **Response**: JSON with health status

### Metrics Endpoints
- **Health**: http://localhost:9084/actuator/health
- **Info**: http://localhost:9084/actuator/info
- **Metrics**: http://localhost:9084/actuator/metrics

### Database Health Check
```bash
# Check database connection
curl http://localhost:9084/actuator/health

# Check specific health indicators
curl http://localhost:9084/actuator/health/db
```

## 🔄 Integration with Application Config Service

### Feign Client Configuration
```java
@FeignClient(name = "application-config-service", url = "${feign.client.config.application-config-service.url}")
public interface ApplicationConfigFeignClient {
    
    @GetMapping("/api/config/permissions")
    List<PermissionConfig> getPermissionConfigs();
    
    @PostMapping("/api/config/permissions")
    void updatePermissionConfig(@RequestBody PermissionConfig config);
}
```

### Configuration Integration
```java
@Service
public class PermissionConfigService {
    
    @Autowired
    private ApplicationConfigFeignClient configClient;
    
    @PostConstruct
    public void initializePermissions() {
        // Load permission configurations from application config service
        List<PermissionConfig> configs = configClient.getPermissionConfigs();
        
        for (PermissionConfig config : configs) {
            createPermissionIfNotExists(config);
        }
    }
    
    private void createPermissionIfNotExists(PermissionConfig config) {
        if (!permissionRepository.existsByName(config.getName())) {
            Permission permission = new Permission();
            permission.setName(config.getName());
            permission.setDescription(config.getDescription());
            permission.setResource(config.getResource());
            permission.setAction(config.getAction());
            permissionRepository.save(permission);
        }
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
docker-compose exec postgres psql -U postgres -d permission_db -c "SELECT 1;"
```

#### 2. Permission Check Issues
```bash
# Check permission registry service logs
docker-compose logs -f permission-registry-service

# Test permission check endpoint
curl -X POST http://localhost:9084/api/permissions/check \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"userId":"12345","resource":"USER","action":"READ"}'
```

#### 3. Role Assignment Issues
```bash
# Check role assignment
curl -X POST http://localhost:9084/api/permissions/users/12345/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"roles":["ADMIN"]}'
```

### Log Analysis
```bash
# View permission registry service logs
docker-compose logs -f permission-registry-service

# Filter for permission operations
docker-compose logs permission-registry-service | grep "Permission"
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
        return new ConcurrentMapCacheManager("permissions", "roles", "user-permissions");
    }
}
```

## 📚 Best Practices

### 1. Security
- Implement proper permission validation
- Use role-based access control
- Audit all permission changes
- Implement least privilege principle

### 2. Performance
- Cache frequently accessed permissions
- Use efficient database queries
- Implement permission preloading
- Optimize role hierarchies

### 3. Data Integrity
- Validate permission assignments
- Use database constraints
- Handle concurrent updates
- Maintain audit trails

### 4. Monitoring
- Monitor permission check performance
- Track permission usage patterns
- Alert on security events
- Monitor role assignments

## 🔗 Related Services

- **User Management Service**: User data and role assignments
- **Application Config Service**: Permission configurations
- **API Gateway**: Request routing and security
- **Eureka Server**: Service discovery

## 📄 Configuration Files

### Main Configuration
- `application.yml`: Main application configuration
- `pom.xml`: Maven dependencies and build configuration
- `Dockerfile`: Container configuration

### Service Configuration
- `PermissionController.java`: REST endpoints
- `PermissionService.java`: Business logic
- `PermissionRepository.java`: Data access layer
- `ApplicationConfigFeignClient.java`: Config service integration

### Security Configuration
- `SecurityConfig.java`: Security configuration
- `PermissionEvaluator.java`: Permission evaluation logic
- `RoleService.java`: Role management service 