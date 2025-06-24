# API Gateway

## Overview
The API Gateway serves as the single entry point for all client requests to the Onified microservices platform. It handles routing, load balancing, authentication, and provides a unified API interface.

## Build Order: 6th Service
This service should be built after all business services and before the Angular web application.

## Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular Web   │    │  API Gateway    │    │   Eureka Server │
│   Application   │◄──►│  (Spring Cloud) │◄──►│  (Discovery)    │
│   Port: 4200    │    │   Port: 9080    │    │   Port: 8761    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Authentication │    │ Application     │    │ Permission      │
│     Service     │    │ Config Service  │    │ Registry        │
│   Port: 9083    │    │   Port: 9082    │    │   Port: 9084    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
┌─────────────────┐
│ User Management │
│     Service     │
│   Port: 9085    │
└─────────────────┘
```

## Configuration

### Application Properties
```yaml
server:
  port: 9080

spring:
  application:
    name: onified-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        # Authentication Service Routes
        - id: auth-service
          uri: lb://authentication-service
          predicates:
            - Path=/api/v1/auth/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: auth-service-circuit-breaker
                fallbackUri: forward:/fallback/auth

        # Application Config Service Routes
        - id: app-config-service
          uri: lb://application-config-service
          predicates:
            - Path=/api/v1/applications/**, /api/v1/modules/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: app-config-service-circuit-breaker
                fallbackUri: forward:/fallback/app-config

        # Permission Registry Service Routes
        - id: permission-registry-service
          uri: lb://permission-registry-service
          predicates:
            - Path=/api/v1/permissions/**, /api/v1/roles/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: permission-registry-service-circuit-breaker
                fallbackUri: forward:/fallback/permission-registry

        # User Management Service Routes
        - id: user-management-service
          uri: lb://user-management-service
          predicates:
            - Path=/api/v1/users/**
          filters:
            - StripPrefix=0
            - name: CircuitBreaker
              args:
                name: user-management-service-circuit-breaker
                fallbackUri: forward:/fallback/user-management

      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "http://localhost:4200"
            allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
            allowed-headers: "*"
            allow-credentials: true

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway
  endpoint:
    health:
      show-details: always

logging:
  file:
    name: ${LOG_DIR:logs}/onified-gateway/application.log
  level:
    com.onified: DEBUG
    org.springframework.cloud.gateway: DEBUG
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
COPY --from=build /app/target/onified-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dependencies
- Spring Boot 3.5.0
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Cloud Circuit Breaker
- Spring Boot Actuator
- Spring Boot WebFlux
- Resilience4j

## Route Configuration

### Authentication Service Routes
```yaml
- id: auth-service
  uri: lb://authentication-service
  predicates:
    - Path=/api/v1/auth/**
  filters:
    - StripPrefix=0
    - name: CircuitBreaker
      args:
        name: auth-service-circuit-breaker
        fallbackUri: forward:/fallback/auth
```

### Application Config Service Routes
```yaml
- id: app-config-service
  uri: lb://application-config-service
  predicates:
    - Path=/api/v1/applications/**, /api/v1/modules/**
  filters:
    - StripPrefix=0
    - name: CircuitBreaker
      args:
        name: app-config-service-circuit-breaker
        fallbackUri: forward:/fallback/app-config
```

### Permission Registry Service Routes
```yaml
- id: permission-registry-service
  uri: lb://permission-registry-service
  predicates:
    - Path=/api/v1/permissions/**, /api/v1/roles/**
  filters:
    - StripPrefix=0
    - name: CircuitBreaker
      args:
        name: permission-registry-service-circuit-breaker
        fallbackUri: forward:/fallback/permission-registry
```

### User Management Service Routes
```yaml
- id: user-management-service
  uri: lb://user-management-service
  predicates:
    - Path=/api/v1/users/**
  filters:
    - StripPrefix=0
    - name: CircuitBreaker
      args:
        name: user-management-service-circuit-breaker
        fallbackUri: forward:/fallback/user-management
```

## API Endpoints

### Gateway Health
- **URL**: `GET /actuator/health`
- **Description**: Gateway health status

### Gateway Info
- **URL**: `GET /actuator/info`
- **Description**: Gateway information

### Gateway Metrics
- **URL**: `GET /actuator/metrics`
- **Description**: Gateway metrics

### Gateway Routes
- **URL**: `GET /actuator/gateway/routes`
- **Description**: List all configured routes

### Fallback Endpoints
- **URL**: `GET /fallback/{service}`
- **Description**: Circuit breaker fallback responses

## Circuit Breaker Configuration

### Resilience4j Configuration
```yaml
resilience4j:
  circuitbreaker:
    instances:
      auth-service-circuit-breaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 5s
        failure-rate-threshold: 50
        event-consumer-buffer-size: 10
      app-config-service-circuit-breaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 5s
        failure-rate-threshold: 50
        event-consumer-buffer-size: 10
      permission-registry-service-circuit-breaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 5s
        failure-rate-threshold: 50
        event-consumer-buffer-size: 10
      user-management-service-circuit-breaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 5s
        failure-rate-threshold: 50
        event-consumer-buffer-size: 10
```

## CORS Configuration

### Global CORS
```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "http://localhost:4200"
            allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
            allowed-headers: "*"
            allow-credentials: true
```

## Load Balancing

### Service Discovery
- Automatic service discovery via Eureka
- Load balancing across multiple instances
- Health check integration
- Service instance selection

### Load Balancer Configuration
```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
```

## Security

### Authentication Filter
```java
@Component
public class AuthenticationFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().value())) {
            return chain.filter(exchange);
        }
        
        // Extract and validate JWT token
        String token = extractToken(request);
        if (token != null && validateToken(token)) {
            return chain.filter(exchange);
        }
        
        // Return unauthorized response
        return unauthorizedResponse(exchange);
    }
}
```

### Public Endpoints
```java
private boolean isPublicEndpoint(String path) {
    return path.startsWith("/api/v1/auth/login") ||
           path.startsWith("/api/v1/auth/refresh") ||
           path.startsWith("/actuator/health") ||
           path.startsWith("/actuator/info");
}
```

## Monitoring

### Health Checks
- Gateway health status
- Service discovery status
- Circuit breaker status
- Route health

### Metrics
- Request count
- Response times
- Error rates
- Circuit breaker metrics
- Route metrics

### Logging
- Request/response logging
- Error logging
- Performance logging
- Circuit breaker events

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
# Test gateway health
curl -X GET http://localhost:9080/actuator/health

# Test route to authentication service
curl -X GET http://localhost:9080/api/v1/auth/health

# Test route to user management service
curl -X GET http://localhost:9080/api/v1/users
```

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d onified-gateway

# Check status
docker-compose ps onified-gateway

# View logs
docker-compose logs onified-gateway
```

### Local Development
```bash
# Run locally
cd onified-gateway
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Troubleshooting

### Common Issues

1. **Service Discovery Issues**
   ```bash
   # Check Eureka server
   # http://localhost:8761
   
   # Verify services are registered
   curl http://localhost:8761/eureka/apps
   ```

2. **Circuit Breaker Issues**
   ```bash
   # Check circuit breaker status
   curl http://localhost:9080/actuator/health
   
   # View circuit breaker metrics
   curl http://localhost:9080/actuator/metrics/resilience4j.circuitbreaker.calls
   ```

3. **Routing Issues**
   ```bash
   # Check route configuration
   curl http://localhost:9080/actuator/gateway/routes
   
   # Test specific routes
   curl -X GET http://localhost:9080/api/v1/users
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f onified-gateway

# Search for routing events
docker-compose logs onified-gateway | grep -i "route"

# Search for errors
docker-compose logs onified-gateway | grep -i error
```

## Performance Optimization

### Caching Strategy
- Route caching
- Service discovery caching
- Response caching
- Circuit breaker caching

### Connection Pooling
- WebClient connection pooling
- Database connection pooling
- HTTP connection pooling

### Rate Limiting
```java
@Component
public class RateLimitingFilter implements GlobalFilter {
    
    private final RateLimiter rateLimiter = RateLimiter.create(100.0); // 100 requests per second
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (rateLimiter.tryAcquire()) {
            return chain.filter(exchange);
        }
        
        return tooManyRequestsResponse(exchange);
    }
}
```

## Integration Points

### Dependencies
- **Eureka Server**: Service discovery
- **All Microservices**: Routing targets

### Dependents
- **Angular Web App**: API access point
- **External Clients**: API access point

## Security Best Practices

### API Security
- JWT token validation
- Rate limiting
- CORS configuration
- Input validation

### Network Security
- HTTPS enforcement
- Request/response encryption
- API key management
- Audit logging

## Next Steps
After API Gateway is running:
1. Start Angular Web Application
2. Configure frontend routing
3. Test complete API flow
4. Verify all integrations 