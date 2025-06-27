# API Gateway Service

## 📋 Overview

The API Gateway is the central entry point for all client requests to the Onified microservices platform. It provides routing, security, load balancing, and cross-cutting concerns like authentication, rate limiting, and circuit breaking.

## 🎯 Purpose

- **Request Routing**: Routes incoming requests to appropriate microservices
- **Authentication & Authorization**: Validates JWT tokens and enforces security policies
- **Load Balancing**: Distributes requests across multiple service instances
- **Circuit Breaking**: Prevents cascading failures with circuit breaker patterns
- **Rate Limiting**: Controls request frequency to prevent abuse
- **CORS Handling**: Manages Cross-Origin Resource Sharing policies
- **Request/Response Transformation**: Modifies requests and responses as needed
- **Monitoring & Logging**: Centralized logging and metrics collection

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   API Gateway   │    │   Microservices │
│   (Browser/App) │◄──►│   (Spring Cloud │◄──►│   (Auth, User,  │
│                 │    │    Gateway)     │    │    Permission)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Eureka Server │
                       │   (Discovery)   │
                       └─────────────────┘
```

## 📦 Spring Boot Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-parent` | 3.2.5 | Spring Boot parent POM |
| `spring-cloud-starter-gateway` | 2023.0.1 | API Gateway functionality |
| `spring-cloud-starter-netflix-eureka-client` | 2023.0.1 | Service discovery |
| `spring-cloud-starter-circuitbreaker-reactor-resilience4j` | 2023.0.1 | Circuit breaker |
| `spring-boot-starter-actuator` | 3.2.5 | Health checks and metrics |
| `spring-boot-starter-webflux` | 3.2.5 | Reactive web support |
| `spring-boot-starter-security` | 3.2.5 | Security framework |
| `spring-boot-starter-test` | 3.2.5 | Testing support |

## ⚙️ Configuration

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
          enabled: false
      routes:
        # Platform Management Service
        - id: platform-management-service
          uri: http://platform-management-service:9081
          predicates:
            - Path=/platform-management/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: platform-management-circuit-breaker
                fallbackUri: forward:/fallback/platform-management

        # Authentication Service
        - id: authentication-service
          uri: http://authentication-service:9083
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1

        # User Management Service
        - id: user-management-service
          uri: http://user-management-service:9085
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1

        # Permission Registry Service
        - id: permission-registry-service
          uri: http://permission-registry-service:9084
          predicates:
            - Path=/api/permissions/**
          filters:
            - StripPrefix=1

        # Application Config Service
        - id: application-config-service
          uri: http://application-config-service:9082
          predicates:
            - Path=/api/config/**
          filters:
            - StripPrefix=1

        # Tenant Management Service
        - id: tenant-management-service
          uri: http://tenant-management-service:9086
          predicates:
            - Path=/api/tenants/**
          filters:
            - StripPrefix=1

# Eureka Client Configuration
eureka:
  client:
    enabled: ${EUREKA_ENABLED:false}
    service-url:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://eureka-server:8761/eureka/}
    register-with-eureka: ${EUREKA_ENABLED:false}
    fetch-registry: ${EUREKA_ENABLED:false}
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}

# Circuit Breaker Configuration
resilience4j:
  circuitbreaker:
    instances:
      platform-management-circuit-breaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
      user-management-circuit-breaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,gateway
  endpoint:
    health:
      show-details: always

# Logging Configuration
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    com.onified.gateway: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 9080 | Port on which gateway runs |
| `EUREKA_ENABLED` | false | Enable/disable Eureka client |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | http://eureka-server:8761/eureka/ | Eureka server URL |
| `LOG_LEVEL` | INFO | Logging level |

## 🚀 Deployment

### Docker Deployment

```yaml
onified-gateway:
  build: ./onified-gateway
  container_name: onified-gateway
  ports:
    - "${GATEWAY_PORT:-9080}:9080"
  environment:
    - LOG_DIR=${GATEWAY_LOG_DIR:-/app/logs}
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    - EUREKA_ENABLED=true
    - SPRING_APPLICATION_NAME=onified-gateway
  volumes:
    - ${GATEWAY_LOG_DIR:-./logs/gateway}:/app/logs
  depends_on:
    - eureka-server
    - keycloak
  healthcheck:
    test: ["CMD-SHELL", "curl -sf http://localhost:9080/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 10
    start_period: 30s
```

### Local Development

```bash
cd onified-gateway
mvn spring-boot:run
```

## 🔧 Route Configuration

### Route Definitions

#### Platform Management Service
```yaml
- id: platform-management-service
  uri: http://platform-management-service:9081
  predicates:
    - Path=/platform-management/**
  filters:
    - StripPrefix=1
    - name: CircuitBreaker
      args:
        name: platform-management-circuit-breaker
        fallbackUri: forward:/fallback/platform-management
```

#### Authentication Service
```yaml
- id: authentication-service
  uri: http://authentication-service:9083
  predicates:
    - Path=/api/auth/**
  filters:
    - StripPrefix=1
```

#### User Management Service
```yaml
- id: user-management-service
  uri: http://user-management-service:9085
  predicates:
    - Path=/api/users/**
  filters:
    - StripPrefix=1
```

### Predicates
- **Path**: Route based on URL path
- **Method**: Route based on HTTP method
- **Header**: Route based on request headers
- **Query**: Route based on query parameters
- **Host**: Route based on host header

### Filters
- **StripPrefix**: Remove path prefix
- **CircuitBreaker**: Circuit breaker pattern
- **RateLimiter**: Rate limiting
- **AddRequestHeader**: Add headers to request
- **AddResponseHeader**: Add headers to response

## 🛡️ Security Configuration

### JWT Token Validation
```java
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Extract JWT token from Authorization header
            String authHeader = request.getHeaders().getFirst("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Validate token with Keycloak
                if (validateToken(token)) {
                    return chain.filter(exchange);
                }
            }
            
            return onError(exchange, "No valid token", HttpStatus.UNAUTHORIZED);
        };
    }
}
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
```

## 🔄 Circuit Breaker Configuration

### Resilience4j Configuration
```yaml
resilience4j:
  circuitbreaker:
    instances:
      platform-management-circuit-breaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        record-exceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
```

### Fallback Configuration
```java
@Component
public class FallbackController {
    
    @GetMapping("/fallback/platform-management")
    public Mono<String> platformManagementFallback() {
        return Mono.just("Platform Management Service is currently unavailable");
    }
    
    @GetMapping("/fallback/user-management")
    public Mono<String> userManagementFallback() {
        return Mono.just("User Management Service is currently unavailable");
    }
}
```

## 📊 Monitoring and Health

### Health Check Endpoint
- **URL**: http://localhost:9080/actuator/health
- **Method**: GET
- **Response**: JSON with health status

### Gateway Endpoints
- **Routes**: http://localhost:9080/actuator/gateway/routes
- **Global Filters**: http://localhost:9080/actuator/gateway/globalfilters
- **Route Filters**: http://localhost:9080/actuator/gateway/routefilters

### Metrics Endpoints
- **Health**: http://localhost:9080/actuator/health
- **Info**: http://localhost:9080/actuator/info
- **Metrics**: http://localhost:9080/actuator/metrics

## 🔍 API Reference

### Gateway Routes

#### Platform Management
```
GET /platform-management/**
POST /platform-management/**
PUT /platform-management/**
DELETE /platform-management/**
```

#### Authentication
```
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh
GET /api/auth/validate
```

#### User Management
```
GET /api/users/**
POST /api/users/**
PUT /api/users/**
DELETE /api/users/**
```

#### Permission Registry
```
GET /api/permissions/**
POST /api/permissions/**
PUT /api/permissions/**
DELETE /api/permissions/**
```

#### Application Config
```
GET /api/config/**
POST /api/config/**
PUT /api/config/**
DELETE /api/config/**
```

#### Tenant Management
```
GET /api/tenants/**
POST /api/tenants/**
PUT /api/tenants/**
DELETE /api/tenants/**
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Route Not Found
```bash
# Check route configuration
curl http://localhost:9080/actuator/gateway/routes

# Check service availability
docker-compose ps
```

#### 2. Circuit Breaker Issues
```bash
# Check circuit breaker status
curl http://localhost:9080/actuator/health

# View circuit breaker metrics
curl http://localhost:9080/actuator/metrics/resilience4j.circuitbreaker.calls
```

#### 3. Authentication Issues
```bash
# Check JWT token
curl -H "Authorization: Bearer <token>" http://localhost:9080/api/auth/validate

# Check Keycloak connection
docker-compose logs keycloak
```

### Log Analysis
```bash
# View gateway logs
docker-compose logs -f onified-gateway

# Filter for routing events
docker-compose logs onified-gateway | grep "Route matched"
```

## 📈 Performance Tuning

### JVM Settings
```bash
-Dspring-boot.run.jvmArguments="-Xms1g -Xmx2g -XX:+UseG1GC"
```

### Gateway Settings
```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000
        response-timeout: 10s
        pool:
          max-connections: 200
          max-idle-time: 15s
```

## 📚 Best Practices

### 1. Security
- Implement proper JWT validation
- Configure CORS policies
- Use HTTPS in production
- Implement rate limiting

### 2. Performance
- Configure appropriate timeouts
- Use circuit breakers for fault tolerance
- Monitor gateway metrics
- Optimize route configurations

### 3. Monitoring
- Set up health check monitoring
- Configure alerting for failures
- Monitor circuit breaker states
- Track request/response times

### 4. Configuration
- Use environment-specific configurations
- Implement proper logging
- Configure fallback mechanisms
- Test all routes thoroughly

## 🔗 Related Services

- **Eureka Server**: Service discovery
- **Authentication Service**: JWT token validation
- **User Management Service**: User operations
- **Permission Registry Service**: Permission management
- **Application Config Service**: Configuration management
- **Platform Management Service**: Platform operations
- **Tenant Management Service**: Tenant management

## 📄 Configuration Files

### Main Configuration
- `application.yml`: Main application configuration
- `pom.xml`: Maven dependencies and build configuration
- `Dockerfile`: Container configuration

### Security Configuration
- `SecurityConfig.java`: Security configuration
- `JwtAuthenticationFilter.java`: JWT authentication filter
- `CorsConfig.java`: CORS configuration

### Circuit Breaker Configuration
- `FallbackController.java`: Fallback endpoints
- `CircuitBreakerConfig.java`: Circuit breaker configuration 