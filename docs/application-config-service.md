# Application Config Service

## Overview
The Application Config Service manages application configurations, modules, and settings across the Onified platform. It provides centralized configuration management and module registration capabilities.

## Build Order: 2nd Service
This service should be built after Eureka Server and before other business services.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐
│  Eureka Server  │    │ Application     │
│   (Discovery)   │◄──►│ Config Service  │
│   Port: 8761    │    │   Port: 9082    │
└─────────────────┘    └─────────────────┘
```

## Configuration

### Application Properties
```yaml
server:
  port: 9082

spring:
  application:
    name: application-config-service
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
    name: ${LOG_DIR:logs}/application-config-service/application.log
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
COPY --from=build /app/target/application-config-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9082
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

### Applications

#### Get All Applications
- **URL**: `GET /api/v1/applications`
- **Description**: Retrieve all applications
- **Response**: List of ApplicationResponseDTO

#### Get Application by ID
- **URL**: `GET /api/v1/applications/{id}`
- **Description**: Retrieve application by ID
- **Response**: ApplicationResponseDTO

#### Create Application
- **URL**: `POST /api/v1/applications`
- **Description**: Create new application
- **Request Body**: ApplicationRequestDTO
- **Response**: ApplicationResponseDTO

#### Update Application
- **URL**: `PUT /api/v1/applications/{id}`
- **Description**: Update existing application
- **Request Body**: ApplicationRequestDTO
- **Response**: ApplicationResponseDTO

#### Delete Application
- **URL**: `DELETE /api/v1/applications/{id}`
- **Description**: Delete application
- **Response**: 204 No Content

### Modules

#### Get All Modules
- **URL**: `GET /api/v1/modules`
- **Description**: Retrieve all modules
- **Response**: List of ModuleResponseDTO

#### Get Module by ID
- **URL**: `GET /api/v1/modules/{id}`
- **Description**: Retrieve module by ID
- **Response**: ModuleResponseDTO

#### Create Module
- **URL**: `POST /api/v1/modules`
- **Description**: Create new module
- **Request Body**: ModuleRequestDTO
- **Response**: ModuleResponseDTO

#### Update Module
- **URL**: `PUT /api/v1/modules/{id}`
- **Description**: Update existing module
- **Request Body**: ModuleRequestDTO
- **Response**: ModuleResponseDTO

#### Delete Module
- **URL**: `DELETE /api/v1/modules/{id}`
- **Description**: Delete module
- **Response**: 204 No Content

### Health Check
- **URL**: `GET /actuator/health`
- **Description**: Service health status

## Data Models

### Application Entity
```java
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String version;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<AppModule> modules;
}
```

### AppModule Entity
```java
@Entity
@Table(name = "app_modules")
public class AppModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
    
    @Enumerated(EnumType.STRING)
    private ModuleStatus status;
}
```

## Business Logic

### Application Management
- CRUD operations for applications
- Version control and status management
- Module association

### Module Management
- CRUD operations for modules
- Application-module relationships
- Status tracking

### Validation Rules
- Application names must be unique
- Required fields validation
- Status transitions validation

## Database Schema

### Tables
1. **applications**
   - id (PK)
   - name (unique)
   - description
   - version
   - status
   - created_at
   - updated_at

2. **app_modules**
   - id (PK)
   - name
   - description
   - application_id (FK)
   - status
   - created_at
   - updated_at

## Error Handling

### Exception Types
- `BadRequestException`: Invalid input data
- `ConflictException`: Duplicate entries
- `NotFoundException`: Resource not found
- `GlobalExceptionHandler`: Centralized error handling

### Error Response Format
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Application name already exists",
  "path": "/api/v1/applications"
}
```

## Security

### Current Implementation
- Basic request validation
- Input sanitization
- SQL injection prevention via JPA

### Future Enhancements
- JWT token validation
- Role-based access control
- API rate limiting

## Monitoring

### Health Checks
- Database connectivity
- Service registration status
- Memory usage

### Metrics
- Request count
- Response times
- Error rates
- Database connection pool

### Logging
- Request/response logging
- Error logging
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
# Test endpoints
curl -X GET http://localhost:9082/api/v1/applications
curl -X POST http://localhost:9082/api/v1/applications \
  -H "Content-Type: application/json" \
  -d '{"name":"test-app","description":"Test Application","version":"1.0.0"}'
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d application-config-service

# Check status
docker-compose ps application-config-service

# View logs
docker-compose logs application-config-service
```

### Local Development
```bash
# Run locally
cd application-config-service
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   ```bash
   # Check PostgreSQL logs
   docker-compose logs postgres
   
   # Verify database credentials
   # Check application.yml configuration
   ```

2. **Service Registration Issues**
   ```bash
   # Check Eureka server
   # http://localhost:8761
   
   # Verify service appears in registry
   ```

3. **Validation Errors**
   ```bash
   # Check request payload
   # Verify required fields
   # Check unique constraints
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f application-config-service

# Search for errors
docker-compose logs application-config-service | grep -i error

# Search for specific operations
docker-compose logs application-config-service | grep -i "create application"
```

## Performance Optimization

### Database Optimization
- Index on frequently queried fields
- Connection pool tuning
- Query optimization

### Caching Strategy
- Application-level caching
- Database query caching
- Response caching

## Integration Points

### Dependencies
- **Eureka Server**: Service discovery
- **PostgreSQL**: Data persistence

### Dependents
- **API Gateway**: Route configuration
- **Other Services**: Configuration lookup

## Next Steps
After Application Config Service is running:
1. Start Authentication Service
2. Configure application and module registrations
3. Test API endpoints
4. Verify service discovery 