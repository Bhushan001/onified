# Permission Registry Service

## Overview
The Permission Registry Service manages permissions, roles, and access control across the Onified platform. It provides fine-grained permission management and role-based access control (RBAC).

## Build Order: 4th Service
This service should be built after Authentication Service and before User Management Service.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Eureka Server  │    │ Permission      │    │ Authentication  │
│   (Discovery)   │◄──►│ Registry        │◄──►│    Service      │
│   Port: 8761    │    │   Port: 9084    │    │   Port: 9083    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Configuration

### Application Properties
```yaml
server:
  port: 9084

spring:
  application:
    name: permission-registry-service
  datasource:
    url: jdbc:postgresql://postgres:5432/onified
    username: ${DB_USERNAME:onified_user}
    password: ${DB_PASSWORD:onified_password}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

logging:
  file:
    name: ${LOG_DIR:logs}/permission-registry-service/application.log
  level:
    com.onified: DEBUG
    org.springframework.web: DEBUG
```

### Docker Configuration
```dockerfile
# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/permission-registry-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9084
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dependencies
- Spring Boot 3.5.0
- Spring Cloud Netflix Eureka Client
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot Validation
- Lombok

## API Endpoints

### Permissions

#### Get All Permissions
- **URL**: `GET /api/v1/permissions`
- **Description**: Retrieve all permissions
- **Response**: List of PermissionResponseDTO

#### Get Permission by ID
- **URL**: `GET /api/v1/permissions/{id}`
- **Description**: Retrieve permission by ID
- **Response**: PermissionResponseDTO

#### Create Permission
- **URL**: `POST /api/v1/permissions`
- **Description**: Create new permission
- **Request Body**: PermissionRequestDTO
- **Response**: PermissionResponseDTO

#### Update Permission
- **URL**: `PUT /api/v1/permissions/{id}`
- **Description**: Update existing permission
- **Request Body**: PermissionRequestDTO
- **Response**: PermissionResponseDTO

#### Delete Permission
- **URL**: `DELETE /api/v1/permissions/{id}`
- **Description**: Delete permission
- **Response**: 204 No Content

### Roles

#### Get All Roles
- **URL**: `GET /api/v1/roles`
- **Description**: Retrieve all roles
- **Response**: List of RoleResponseDTO

#### Get Role by ID
- **URL**: `GET /api/v1/roles/{id}`
- **Description**: Retrieve role by ID
- **Response**: RoleResponseDTO

#### Create Role
- **URL**: `POST /api/v1/roles`
- **Description**: Create new role
- **Request Body**: RoleRequestDTO
- **Response**: RoleResponseDTO

#### Update Role
- **URL**: `PUT /api/v1/roles/{id}`
- **Description**: Update existing role
- **Request Body**: RoleRequestDTO
- **Response**: RoleResponseDTO

#### Delete Role
- **URL**: `DELETE /api/v1/roles/{id}`
- **Description**: Delete role
- **Response**: 204 No Content

### Role Permissions

#### Assign Permission to Role
- **URL**: `POST /api/v1/roles/{roleId}/permissions`
- **Description**: Assign permission to role
- **Request Body**: PermissionAssignmentDTO
- **Response**: RoleResponseDTO

#### Remove Permission from Role
- **URL**: `DELETE /api/v1/roles/{roleId}/permissions/{permissionId}`
- **Description**: Remove permission from role
- **Response**: 204 No Content

#### Get Role Permissions
- **URL**: `GET /api/v1/roles/{roleId}/permissions`
- **Description**: Get all permissions for a role
- **Response**: List of PermissionResponseDTO

### User Roles

#### Assign Role to User
- **URL**: `POST /api/v1/users/{userId}/roles`
- **Description**: Assign role to user
- **Request Body**: RoleAssignmentDTO
- **Response**: UserRoleResponseDTO

#### Remove Role from User
- **URL**: `DELETE /api/v1/users/{userId}/roles/{roleId}`
- **Description**: Remove role from user
- **Response**: 204 No Content

#### Get User Roles
- **URL**: `GET /api/v1/users/{userId}/roles`
- **Description**: Get all roles for a user
- **Response**: List of RoleResponseDTO

### Permission Checks

#### Check User Permission
- **URL**: `POST /api/v1/permissions/check`
- **Description**: Check if user has specific permission
- **Request Body**: PermissionCheckDTO
- **Response**: PermissionCheckResponseDTO

#### Get User Permissions
- **URL**: `GET /api/v1/users/{userId}/permissions`
- **Description**: Get all permissions for a user
- **Response**: List of PermissionResponseDTO

### Health Check
- **URL**: `GET /actuator/health`
- **Description**: Service health status

## Data Models

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
    
    @Enumerated(EnumType.STRING)
    private PermissionStatus status;
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
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
    
    @Enumerated(EnumType.STRING)
    private RoleStatus status;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions")
    private Set<Permission> permissions;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
```

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private Set<Role> roles;
}
```

## Business Logic

### Permission Management
- CRUD operations for permissions
- Resource-action based permissions
- Permission status management

### Role Management
- CRUD operations for roles
- Role-permission assignments
- Role hierarchy support

### User Role Management
- User-role assignments
- Role inheritance
- Dynamic permission calculation

### Permission Checking
- Real-time permission validation
- Cached permission results
- Bulk permission checks

## Database Schema

### Tables
1. **permissions**
   - id (PK)
   - name (unique)
   - description
   - resource
   - action
   - status
   - created_at
   - updated_at

2. **roles**
   - id (PK)
   - name (unique)
   - description
   - status
   - created_at
   - updated_at

3. **users**
   - id (PK)
   - username (unique)
   - email
   - status
   - created_at
   - updated_at

4. **role_permissions**
   - role_id (FK)
   - permission_id (FK)

5. **user_roles**
   - user_id (FK)
   - role_id (FK)

## Permission System

### Permission Format
- **Resource**: The object being accessed (e.g., "user", "application", "module")
- **Action**: The operation being performed (e.g., "read", "write", "delete", "admin")

### Default Permissions
```java
// User Management
"user:read"
"user:write"
"user:delete"
"user:admin"

// Application Management
"application:read"
"application:write"
"application:delete"
"application:admin"

// Module Management
"module:read"
"module:write"
"module:delete"
"module:admin"
```

### Default Roles
```java
// System Roles
"SUPER_ADMIN"     // Full system access
"ADMIN"           // Administrative access
"USER"            // Standard user access
"GUEST"           // Limited access
```

## Error Handling

### Exception Types
- `PermissionNotFoundException`: Permission not found
- `RoleNotFoundException`: Role not found
- `UserNotFoundException`: User not found
- `PermissionDeniedException`: Access denied
- `GlobalExceptionHandler`: Centralized error handling

### Error Response Format
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient permissions",
  "path": "/api/v1/permissions/check"
}
```

## Monitoring

### Health Checks
- Database connectivity
- Service registration status
- Permission cache status

### Metrics
- Permission checks
- Role assignments
- Cache hit rates
- Response times

### Logging
- Permission check events
- Role assignment events
- Security events
- Performance logging

## Testing

### Unit Tests
```bash
# Run unit tests
mvn test

# Run with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests
mvn test -Dtest=*IntegrationTest
```

### API Tests
```bash
# Test permission creation
curl -X POST http://localhost:9084/api/v1/permissions \
  -H "Content-Type: application/json" \
  -d '{"name":"user:read","description":"Read user data","resource":"user","action":"read"}'

# Test permission check
curl -X POST http://localhost:9084/api/v1/permissions/check \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"permission":"user:read"}'
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d permission-registry-service

# Check status
docker-compose ps permission-registry-service

# View logs
docker-compose logs permission-registry-service
```

### Local Development
```bash
# Run locally
cd permission-registry-service
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting

### Common Issues

1. **Permission Check Failures**
   ```bash
   # Check user roles
   curl http://localhost:9084/api/v1/users/{userId}/roles
   
   # Check role permissions
   curl http://localhost:9084/api/v1/roles/{roleId}/permissions
   ```

2. **Database Connection Issues**
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   
   # Verify database credentials
   ```

3. **Cache Issues**
   ```bash
   # Clear permission cache
   # Restart service
   docker-compose restart permission-registry-service
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f permission-registry-service

# Search for permission checks
docker-compose logs permission-registry-service | grep -i "permission"

# Search for errors
docker-compose logs permission-registry-service | grep -i error
```

## Performance Optimization

### Caching Strategy
- Permission cache
- Role cache
- User permission cache
- Cache invalidation

### Database Optimization
- Permission queries optimization
- Role queries optimization
- User queries optimization
- Index optimization

## Integration Points

### Dependencies
- **Eureka Server**: Service discovery
- **PostgreSQL**: Data persistence

### Dependents
- **API Gateway**: Permission validation
- **Authentication Service**: User context
- **Other Services**: Permission checks

## Security Best Practices

### Permission Design
- Principle of least privilege
- Role-based access control
- Resource-based permissions
- Action-based permissions

### Data Protection
- Permission data encryption
- Audit logging
- Access control
- Data validation

## Next Steps
After Permission Registry Service is running:
1. Start User Management Service
2. Configure default permissions and roles
3. Test permission system
4. Verify role assignments 