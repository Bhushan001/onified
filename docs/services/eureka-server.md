# Eureka Server Service

## 📋 Overview

The Eureka Server is the service discovery component of the Onified microservices platform. It provides a registry where all microservices can register themselves and discover other services. This enables dynamic service discovery and load balancing across the platform.

## 🎯 Purpose

- **Service Registration**: Allows microservices to register themselves with the discovery server
- **Service Discovery**: Enables services to find and communicate with other services
- **Load Balancing**: Provides client-side load balancing capabilities
- **Health Monitoring**: Monitors the health status of registered services
- **Failover**: Handles service failures and provides failover mechanisms

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Microservice  │    │   Eureka Server │    │   Microservice  │
│      A          │◄──►│   (Registry)    │◄──►│      B          │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Microservice  │
                       │      C          │
                       └─────────────────┘
```

## 📦 Spring Boot Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| `spring-boot-starter-parent` | 3.2.5 | Spring Boot parent POM |
| `spring-boot-starter-web` | 3.2.5 | Web application support |
| `spring-cloud-starter-netflix-eureka-server` | 2023.0.1 | Eureka server functionality |
| `spring-boot-starter-actuator` | 3.2.5 | Health checks and metrics |
| `spring-boot-starter-test` | 3.2.5 | Testing support |

## ⚙️ Configuration

### Application Properties

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

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
    com.netflix.eureka: DEBUG
    org.springframework.cloud.netflix.eureka: DEBUG
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8761 | Port on which Eureka server runs |
| `EUREKA_INSTANCE_HOSTNAME` | localhost | Hostname for Eureka instance |
| `LOG_LEVEL` | INFO | Logging level for the application |

## 🚀 Deployment

### Docker Deployment

```yaml
eureka-server:
  build: ./eureka-server
  container_name: eureka-server
  ports:
    - "8761:8761"
  environment:
    - SERVER_PORT=8761
    - EUREKA_INSTANCE_HOSTNAME=eureka-server
  healthcheck:
    test: ["CMD-SHELL", "curl -sf http://localhost:8761/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 10
    start_period: 20s
```

### Local Development

```bash
cd eureka-server
mvn spring-boot:run
```

## 📊 Monitoring and Health

### Health Check Endpoint
- **URL**: http://localhost:8761/actuator/health
- **Method**: GET
- **Response**: JSON with health status

### Eureka Dashboard
- **URL**: http://localhost:8761
- **Features**: 
  - View registered services
  - Service health status
  - Instance details
  - Service statistics

### Metrics Endpoints
- **Health**: http://localhost:8761/actuator/health
- **Info**: http://localhost:8761/actuator/info
- **Metrics**: http://localhost:8761/actuator/metrics

## 🔧 Service Registration

### Client Configuration
Services register with Eureka using the following configuration:

```yaml
eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

### Registration Process
1. Service starts up
2. Registers with Eureka server
3. Sends periodic heartbeats
4. Eureka server maintains registry
5. Other services can discover registered services

## 🛡️ Security

### Basic Authentication (Optional)
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://username:password@eureka-server:8761/eureka/
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```

### Network Security
- Use internal Docker networks
- Configure firewall rules
- Implement TLS/SSL for production

## 🔄 High Availability

### Multi-Instance Setup
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```

### Failover Configuration
- Multiple Eureka instances
- Cross-registration between instances
- Automatic failover handling

## 📈 Performance Tuning

### JVM Settings
```bash
-Dspring-boot.run.jvmArguments="-Xms512m -Xmx1024m -XX:+UseG1GC"
```

### Eureka Settings
```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
    eviction-interval-timer-in-ms: 60000
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Service Not Registering
```bash
# Check Eureka server logs
docker-compose logs eureka-server

# Verify service configuration
curl http://localhost:8761/eureka/apps
```

#### 2. Connection Refused
```bash
# Check if Eureka server is running
docker-compose ps eureka-server

# Check port availability
netstat -ano | findstr :8761
```

#### 3. Service Discovery Issues
```bash
# Check registered services
curl http://localhost:8761/eureka/apps

# Check specific service
curl http://localhost:8761/eureka/apps/SERVICE-NAME
```

### Log Analysis
```bash
# View Eureka server logs
docker-compose logs -f eureka-server

# Filter for registration events
docker-compose logs eureka-server | grep "Registered"
```

## 🔍 API Reference

### REST Endpoints

#### Get All Applications
```
GET /eureka/apps
```

#### Get Specific Application
```
GET /eureka/apps/{appName}
```

#### Get Application Instance
```
GET /eureka/apps/{appName}/{instanceId}
```

#### Health Check
```
GET /actuator/health
```

## 📚 Best Practices

### 1. Configuration
- Use environment-specific configurations
- Implement proper logging levels
- Configure health checks

### 2. Security
- Implement authentication for production
- Use HTTPS in production environments
- Configure network security

### 3. Monitoring
- Set up health check monitoring
- Configure alerting for service failures
- Monitor registration patterns

### 4. Performance
- Tune JVM settings for your environment
- Configure appropriate timeouts
- Monitor memory usage

## 🔗 Related Services

- **API Gateway**: Uses Eureka for service discovery
- **Authentication Service**: Registers with Eureka
- **User Management Service**: Registers with Eureka
- **Permission Registry Service**: Registers with Eureka
- **Application Config Service**: Registers with Eureka
- **Platform Management Service**: Registers with Eureka
- **Tenant Management Service**: Registers with Eureka

## 📄 Configuration Files

### Main Configuration
- `application.yml`: Main application configuration
- `pom.xml`: Maven dependencies and build configuration
- `Dockerfile`: Container configuration

### Environment-Specific
- `application-dev.yml`: Development environment
- `application-prod.yml`: Production environment
- `application-test.yml`: Testing environment 