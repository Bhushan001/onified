# Eureka Server

## Overview
Eureka Server is the service discovery component of the Onified microservices platform. It acts as a registry where all microservices register themselves and discover other services.

## Build Order: 1st Service
This service should be built and started first as other services depend on it for service discovery.

## Architecture
```
┌─────────────────┐
│  Eureka Server  │
│   (Discovery)   │
│   Port: 8761    │
└─────────────────┘
         ▲
         │
    ┌────┴────┐
    │         │
┌───▼───┐ ┌───▼───┐
│Service│ │Service│
│   A   │ │   B   │
└───────┘ └───────┘
```

## Configuration

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
      defaultZone: http://localhost:8761/eureka/
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 1000
```

### Docker Configuration
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dependencies
- Spring Boot 3.5.0
- Spring Cloud Netflix Eureka Server
- Java 21

## API Endpoints

### Health Check
- **URL**: `http://localhost:8761/actuator/health`
- **Method**: GET
- **Response**: Service health status

### Eureka Dashboard
- **URL**: `http://localhost:8761`
- **Description**: Web interface to view registered services

### Service Registry
- **URL**: `http://localhost:8761/eureka/apps`
- **Method**: GET
- **Response**: JSON list of all registered services

## Service Registration

### How Services Register
1. Services include `spring-cloud-starter-netflix-eureka-client` dependency
2. Configure `eureka.client.service-url.defaultZone`
3. Services automatically register on startup
4. Heartbeat every 30 seconds by default

### Registration Example
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

## Monitoring

### Dashboard Features
- View all registered services
- Service health status
- Instance details
- Service metadata

### Health Indicators
- **UP**: Service is healthy and responding
- **DOWN**: Service is unhealthy or unreachable
- **OUT_OF_SERVICE**: Service is manually taken out of service

## Troubleshooting

### Common Issues

1. **Service Not Registering**
   ```bash
   # Check Eureka server logs
   docker-compose logs eureka-server
   
   # Verify service configuration
   # Ensure eureka.client.service-url.defaultZone is correct
   ```

2. **Service Discovery Issues**
   ```bash
   # Check Eureka dashboard
   # http://localhost:8761
   
   # Verify service is listed in registry
   curl http://localhost:8761/eureka/apps
   ```

3. **High Memory Usage**
   ```bash
   # Monitor memory usage
   docker stats eureka-server
   
   # Check for memory leaks in logs
   docker-compose logs eureka-server | grep -i memory
   ```

### Log Analysis
```bash
# View real-time logs
docker-compose logs -f eureka-server

# Search for registration events
docker-compose logs eureka-server | grep -i "registered"

# Search for errors
docker-compose logs eureka-server | grep -i error
```

## Performance Tuning

### Configuration Optimizations
```yaml
eureka:
  server:
    # Disable self-preservation in development
    enable-self-preservation: false
    
    # Faster eviction for development
    eviction-interval-timer-in-ms: 1000
    
    # Response cache settings
    response-cache-update-interval-ms: 3000
```

### Memory Settings
```bash
# JVM memory settings for production
java -Xms512m -Xmx1024m -jar app.jar
```

## Security Considerations

### Production Security
- Enable HTTPS
- Implement authentication
- Use secure communication between services
- Regular security updates

### Network Security
- Firewall rules for port 8761
- Internal network access only
- VPN access for remote management

## Deployment

### Docker Deployment
```bash
# Build and start
docker-compose up -d eureka-server

# Check status
docker-compose ps eureka-server

# View logs
docker-compose logs eureka-server
```

### Local Development
```bash
# Run locally
cd eureka-server
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Integration with Other Services

### Dependencies
- **None**: Eureka Server has no dependencies on other services

### Dependents
- **All Microservices**: Application Config, Authentication, Permission Registry, User Management, API Gateway

### Service Discovery Flow
1. Eureka Server starts first
2. Other services start and register with Eureka
3. Services discover each other through Eureka
4. API Gateway routes requests based on service registry

## Testing

### Health Check Test
```bash
curl -X GET http://localhost:8761/actuator/health
```

### Service Registration Test
```bash
# Start a test service
# Verify it appears in Eureka dashboard
# http://localhost:8761
```

### Load Testing
```bash
# Test with multiple service instances
# Verify load balancing works correctly
```

## Maintenance

### Regular Tasks
- Monitor service registrations
- Check for stale registrations
- Review logs for errors
- Update dependencies

### Backup
- Configuration files
- Service registry data (if persisted)
- Log files

## Next Steps
After Eureka Server is running:
1. Start Application Config Service
2. Configure service discovery for other services
3. Verify all services register correctly 