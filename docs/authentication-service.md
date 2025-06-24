# Authentication Service

## Overview
The Authentication Service handles user authentication and authorization using Keycloak as the identity provider. It provides OAuth2/OIDC integration and manages user sessions.

## Build Order: 3rd Service
This service should be built after Application Config Service and before Permission Registry Service.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Eureka Server  │    │ Authentication  │    │    Keycloak     │
│   (Discovery)   │◄──►│    Service      │◄──►│  (Identity)     │
│   Port: 8761    │    │   Port: 9083    │    │   Port: 8080    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Configuration

### Application Properties
```yaml
server:
  port: 9083

spring:
  application:
    name: authentication-service
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
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/onified
          jwk-set-uri: http://localhost:8080/realms/onified/protocol/openid-connect/certs

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

keycloak:
  auth-server-url: http://localhost:8080
  realm: onified
  resource: authentication-service
  credentials:
    secret: ${KEYCLOAK_CLIENT_SECRET}
  admin:
    username: admin
    password: ${KEYCLOAK_ADMIN_PASSWORD:admin}

logging:
  file:
    name: ${LOG_DIR:logs}/authentication-service/application.log
  level:
    com.onified: DEBUG
    org.springframework.security: DEBUG
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
COPY --from=build /app/target/authentication-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9083
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dependencies
- Spring Boot 3.5.0
- Spring Cloud Netflix Eureka Client
- Spring Security OAuth2 Resource Server
- Spring Data JPA
- PostgreSQL Driver
- Keycloak Admin Client
- Auth0 Java JWT
- Lombok

## API Endpoints

### Authentication

#### Login
- **URL**: `POST /api/v1/auth/login`
- **Description**: Authenticate user with username/password
- **Request Body**: LoginRequestDTO
- **Response**: LoginResponseDTO

#### Refresh Token
- **URL**: `POST /api/v1/auth/refresh`
- **Description**: Refresh access token using refresh token
- **Request Body**: RefreshTokenRequestDTO
- **Response**: LoginResponseDTO

#### Logout
- **URL**: `POST /api/v1/auth/logout`
- **Description**: Logout user and invalidate tokens
- **Request Body**: LogoutRequestDTO
- **Response**: 200 OK

#### Validate Token
- **URL**: `POST /api/v1/auth/validate`
- **Description**: Validate JWT token
- **Request Body**: TokenValidationRequestDTO
- **Response**: TokenValidationResponseDTO

### User Management

#### Get User Profile
- **URL**: `GET /api/v1/users/profile`
- **Description**: Get current user profile
- **Headers**: Authorization: Bearer {token}
- **Response**: UserProfileDTO

#### Update User Profile
- **URL**: `PUT /api/v1/users/profile`
- **Description**: Update user profile
- **Headers**: Authorization: Bearer {token}
- **Request Body**: UserProfileUpdateDTO
- **Response**: UserProfileDTO

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
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
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
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
```

## Business Logic

### Authentication Flow
1. User provides credentials
2. Service validates with Keycloak
3. Keycloak returns tokens
4. Service returns tokens to client

### Token Management
- JWT token validation
- Refresh token handling
- Token revocation
- Session management

### User Management
- User profile operations
- Role assignment
- Status management

## Security Configuration

### OAuth2 Resource Server
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        return http.build();
    }
}
```

### CORS Configuration
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Keycloak Integration

### Client Configuration
- **Client ID**: authentication-service
- **Client Protocol**: openid-connect
- **Access Type**: confidential
- **Valid Redirect URIs**: http://localhost:4200/*
- **Web Origins**: http://localhost:4200

### Realm Configuration
- **Realm Name**: onified
- **Token Lifespan**: 15 minutes
- **Refresh Token Lifespan**: 30 days

### User Roles
- **admin**: Full access
- **user**: Standard user access
- **guest**: Limited access

## Database Schema

### Tables
1. **users**
   - id (PK)
   - username (unique)
   - email
   - first_name
   - last_name
   - status
   - created_at
   - updated_at

2. **roles**
   - id (PK)
   - name (unique)
   - description
   - created_at
   - updated_at

3. **user_roles**
   - user_id (FK)
   - role_id (FK)

## Error Handling

### Exception Types
- `AuthenticationException`: Authentication failures
- `TokenValidationException`: Token validation errors
- `UserNotFoundException`: User not found
- `GlobalExceptionHandler`: Centralized error handling

### Error Response Format
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/v1/auth/login"
}
```

## Monitoring

### Health Checks
- Database connectivity
- Keycloak connectivity
- Service registration status

### Metrics
- Authentication attempts
- Token validations
- Error rates
- Response times

### Logging
- Authentication events
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
# Test login endpoint
curl -X POST http://localhost:9083/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}'

# Test protected endpoint
curl -X GET http://localhost:9083/api/v1/users/profile \
  -H "Authorization: Bearer {token}"
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d authentication-service

# Check status
docker-compose ps authentication-service

# View logs
docker-compose logs authentication-service
```

### Local Development
```bash
# Run locally
cd authentication-service
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting

### Common Issues

1. **Keycloak Connection Issues**
   ```bash
   # Check Keycloak logs
   docker-compose logs keycloak
   
   # Verify Keycloak is running
   curl http://localhost:8080/realms/onified
   ```

2. **Token Validation Issues**
   ```bash
   # Check JWT token format
   # Verify token signature
   # Check token expiration
   ```

3. **Database Connection Issues**
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   
   # Verify database credentials
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f authentication-service

# Search for authentication events
docker-compose logs authentication-service | grep -i "authentication"

# Search for errors
docker-compose logs authentication-service | grep -i error
```

## Performance Optimization

### Token Caching
- JWT token caching
- User profile caching
- Role information caching

### Database Optimization
- User queries optimization
- Role queries optimization
- Connection pool tuning

## Integration Points

### Dependencies
- **Eureka Server**: Service discovery
- **Keycloak**: Identity provider
- **PostgreSQL**: User data persistence

### Dependents
- **API Gateway**: Authentication validation
- **Other Services**: User context

## Security Best Practices

### Token Security
- Secure token storage
- Token rotation
- Token revocation

### Password Security
- Password hashing
- Password policies
- Brute force protection

### API Security
- Rate limiting
- Input validation
- SQL injection prevention

## Next Steps
After Authentication Service is running:
1. Start Permission Registry Service
2. Configure user roles and permissions
3. Test authentication flow
4. Verify Keycloak integration 