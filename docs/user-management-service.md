# User Management Service

## Overview
The User Management Service handles user operations, profile management, and user-related business logic. It provides comprehensive user management capabilities including user creation, updates, and profile operations.

## Build Order: 5th Service
This service should be built after Permission Registry Service and before API Gateway.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Eureka Server  │    │ User Management │    │ Permission      │
│   (Discovery)   │◄──►│    Service      │◄──►│ Registry        │
│   Port: 8761    │    │   Port: 9085    │    │   Port: 9084    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Configuration

### Application Properties
```yaml
server:
  port: 9085

spring:
  application:
    name: user-management-service
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
    name: ${LOG_DIR:logs}/user-management-service/application.log
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
COPY --from=build /app/target/user-management-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dependencies
- Spring Boot 3.5.0
- Spring Cloud Netflix Eureka Client
- Spring Data JPA
- PostgreSQL Driver
- Spring Boot Validation
- Lombok
- MapStruct (for object mapping)

## API Endpoints

### User Management

#### Get All Users
- **URL**: `GET /api/v1/users`
- **Description**: Retrieve all users with pagination
- **Query Parameters**: page, size, sort
- **Response**: Page<UserResponseDTO>

#### Get User by ID
- **URL**: `GET /api/v1/users/{id}`
- **Description**: Retrieve user by ID
- **Response**: UserResponseDTO

#### Create User
- **URL**: `POST /api/v1/users`
- **Description**: Create new user
- **Request Body**: UserCreateRequestDTO
- **Response**: UserResponseDTO

#### Update User
- **URL**: `PUT /api/v1/users/{id}`
- **Description**: Update existing user
- **Request Body**: UserUpdateRequestDTO
- **Response**: UserResponseDTO

#### Delete User
- **URL**: `DELETE /api/v1/users/{id}`
- **Description**: Delete user
- **Response**: 204 No Content

#### Search Users
- **URL**: `GET /api/v1/users/search`
- **Description**: Search users by criteria
- **Query Parameters**: username, email, firstName, lastName
- **Response**: List<UserResponseDTO>

### User Profile

#### Get User Profile
- **URL**: `GET /api/v1/users/{id}/profile`
- **Description**: Get detailed user profile
- **Response**: UserProfileResponseDTO

#### Update User Profile
- **URL**: `PUT /api/v1/users/{id}/profile`
- **Description**: Update user profile
- **Request Body**: UserProfileUpdateRequestDTO
- **Response**: UserProfileResponseDTO

#### Upload Profile Picture
- **URL**: `POST /api/v1/users/{id}/profile/picture`
- **Description**: Upload user profile picture
- **Request**: Multipart file
- **Response**: UserProfileResponseDTO

### User Status

#### Activate User
- **URL**: `POST /api/v1/users/{id}/activate`
- **Description**: Activate user account
- **Response**: UserResponseDTO

#### Deactivate User
- **URL**: `POST /api/v1/users/{id}/deactivate`
- **Description**: Deactivate user account
- **Response**: UserResponseDTO

#### Suspend User
- **URL**: `POST /api/v1/users/{id}/suspend`
- **Description**: Suspend user account
- **Request Body**: UserSuspensionRequestDTO
- **Response**: UserResponseDTO

### User Preferences

#### Get User Preferences
- **URL**: `GET /api/v1/users/{id}/preferences`
- **Description**: Get user preferences
- **Response**: UserPreferencesResponseDTO

#### Update User Preferences
- **URL**: `PUT /api/v1/users/{id}/preferences`
- **Description**: Update user preferences
- **Request Body**: UserPreferencesUpdateRequestDTO
- **Response**: UserPreferencesResponseDTO

### Health Check
- **URL**: `GET /actuator/health`
- **Description**: Service health status

## Data Models

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
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column
    private String phoneNumber;
    
    @Column
    private String profilePictureUrl;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column
    private LocalDateTime lastLoginAt;
    
    @Column
    private LocalDateTime suspendedUntil;
    
    @Column
    private String suspensionReason;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserPreferences preferences;
}
```

### UserProfile Entity
```java
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column
    private String bio;
    
    @Column
    private String location;
    
    @Column
    private String website;
    
    @Column
    private LocalDate dateOfBirth;
    
    @Column
    private String department;
    
    @Column
    private String jobTitle;
    
    @Column
    private String company;
}
```

### UserPreferences Entity
```java
@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column
    private String language;
    
    @Column
    private String timezone;
    
    @Column
    private String theme;
    
    @Column
    private Boolean emailNotifications;
    
    @Column
    private Boolean pushNotifications;
    
    @Column
    private Boolean smsNotifications;
}
```

## Business Logic

### User Management
- CRUD operations for users
- User status management
- Profile management
- Preference management

### User Validation
- Email format validation
- Username uniqueness
- Password strength validation
- Phone number validation

### User Status Workflow
```java
public enum UserStatus {
    ACTIVE,         // User can access the system
    INACTIVE,       // User account is disabled
    SUSPENDED,      // User is temporarily suspended
    PENDING,        // User account pending activation
    DELETED         // User account is deleted
}
```

### User Search
- Search by username
- Search by email
- Search by name
- Advanced filtering
- Pagination support

## Database Schema

### Tables
1. **users**
   - id (PK)
   - username (unique)
   - email (unique)
   - first_name
   - last_name
   - phone_number
   - profile_picture_url
   - status
   - last_login_at
   - suspended_until
   - suspension_reason
   - created_at
   - updated_at

2. **user_profiles**
   - id (PK)
   - user_id (FK)
   - bio
   - location
   - website
   - date_of_birth
   - department
   - job_title
   - company
   - created_at
   - updated_at

3. **user_preferences**
   - id (PK)
   - user_id (FK)
   - language
   - timezone
   - theme
   - email_notifications
   - push_notifications
   - sms_notifications
   - created_at
   - updated_at

## Error Handling

### Exception Types
- `UserNotFoundException`: User not found
- `UserAlreadyExistsException`: User already exists
- `InvalidUserDataException`: Invalid user data
- `UserStatusException`: Invalid user status operation
- `GlobalExceptionHandler`: Centralized error handling

### Error Response Format
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "User with email already exists",
  "path": "/api/v1/users"
}
```

## Monitoring

### Health Checks
- Database connectivity
- Service registration status
- File storage connectivity

### Metrics
- User operations
- Profile updates
- Search operations
- Response times

### Logging
- User creation events
- Profile update events
- Status change events
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
# Test user creation
curl -X POST http://localhost:9085/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","firstName":"Test","lastName":"User"}'

# Test user search
curl -X GET "http://localhost:9085/api/v1/users/search?username=testuser"
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d user-management-service

# Check status
docker-compose ps user-management-service

# View logs
docker-compose logs user-management-service
```

### Local Development
```bash
# Run locally
cd user-management-service
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting

### Common Issues

1. **User Creation Failures**
   ```bash
   # Check database constraints
   # Verify unique constraints
   # Check validation errors
   ```

2. **Profile Picture Upload Issues**
   ```bash
   # Check file storage permissions
   # Verify file size limits
   # Check supported formats
   ```

3. **Search Performance Issues**
   ```bash
   # Check database indexes
   # Verify query optimization
   # Monitor query execution time
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f user-management-service

# Search for user operations
docker-compose logs user-management-service | grep -i "user"

# Search for errors
docker-compose logs user-management-service | grep -i error
```

## Performance Optimization

### Database Optimization
- User queries optimization
- Search queries optimization
- Index optimization
- Connection pool tuning

### Caching Strategy
- User profile caching
- User preferences caching
- Search result caching
- Cache invalidation

### File Storage
- Profile picture optimization
- Image compression
- CDN integration
- Storage cleanup

## Integration Points

### Dependencies
- **Eureka Server**: Service discovery
- **PostgreSQL**: Data persistence
- **Permission Registry Service**: User permissions

### Dependents
- **API Gateway**: User operations
- **Authentication Service**: User context
- **Other Services**: User information

## Security Best Practices

### Data Protection
- Personal data encryption
- GDPR compliance
- Data retention policies
- Access control

### Input Validation
- Email validation
- Phone number validation
- File upload validation
- XSS prevention

### Audit Logging
- User creation events
- Profile update events
- Status change events
- Access logging

## Next Steps
After User Management Service is running:
1. Start API Gateway
2. Configure user management routes
3. Test user operations
4. Verify service integration 