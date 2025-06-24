# Shared Documentation Templates

This file contains common templates and sections that can be referenced by individual service documentation to reduce redundancy.

## Docker Configuration Template

### Standard Multi-Stage Dockerfile
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
COPY --from=build /app/target/{SERVICE_NAME}-0.0.1-SNAPSHOT.jar app.jar
EXPOSE {PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Deployment Commands
```bash
# Build and start
docker-compose up -d {service-name}

# Check status
docker-compose ps {service-name}

# View logs
docker-compose logs {service-name}

# Rebuild and restart
docker-compose build {service-name}
docker-compose up -d {service-name}
```

## Application Properties Template

### Standard Spring Boot Configuration
```yaml
server:
  port: {PORT}

spring:
  application:
    name: {SERVICE_NAME}
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
    name: ${LOG_DIR:logs}/{service-name}/application.log
  level:
    com.onified: DEBUG
    org.springframework.web: DEBUG
```

## Health Check Template

### Health Check Endpoint
- **URL**: `GET /actuator/health`
- **Description**: Service health status
- **Response**: Health status with details

### Health Check Test
```bash
curl -X GET http://localhost:{PORT}/actuator/health
```

## Monitoring Template

### Health Checks
- Database connectivity
- Service registration status
- Memory usage
- Disk space

### Metrics
- Request count
- Response times
- Error rates
- Database connection pool

### Logging
- Request/response logging
- Error logging
- Performance logging
- Security events

## Testing Template

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
# Test health endpoint
curl -X GET http://localhost:{PORT}/actuator/health

# Test specific endpoints
curl -X GET http://localhost:{PORT}/api/v1/{endpoint}
```

## Deployment Template

### Docker Deployment
```bash
# Build and start
docker-compose up -d {service-name}

# Check status
docker-compose ps {service-name}

# View logs
docker-compose logs {service-name}
```

### Local Development
```bash
# Run locally
cd {service-name}
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting Template

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

3. **Port Conflicts**
   ```bash
   # Check what's using a port
   netstat -ano | findstr :{PORT}
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f {service-name}

# Search for errors
docker-compose logs {service-name} | grep -i error

# Search for specific operations
docker-compose logs {service-name} | grep -i "{operation}"
```

## Performance Optimization Template

### Database Optimization
- Index on frequently queried fields
- Connection pool tuning
- Query optimization

### Caching Strategy
- Application-level caching
- Database query caching
- Response caching

## Error Handling Template

### Exception Types
- `NotFoundException`: Resource not found
- `BadRequestException`: Invalid input data
- `ConflictException`: Duplicate entries
- `GlobalExceptionHandler`: Centralized error handling

### Error Response Format
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Error description",
  "path": "/api/v1/{endpoint}"
}
```

## Integration Points Template

### Dependencies
- **Eureka Server**: Service discovery
- **PostgreSQL**: Data persistence
- **Other Services**: As needed

### Dependents
- **API Gateway**: Route configuration
- **Other Services**: Service calls

## Security Best Practices Template

### API Security
- Input validation
- SQL injection prevention
- XSS prevention
- Rate limiting

### Data Protection
- Data encryption
- Audit logging
- Access control
- Data validation 