# Eureka Troubleshooting Guide

## Common Eureka Issues and Solutions

### 1. Self-Preservation Warning

**Problem:**
```
EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. 
RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED 
JUST TO BE SAFE.
```

**Cause:**
- Eureka's self-preservation mode is enabled
- Services are taking too long to start or register
- Network connectivity issues
- Incorrect heartbeat intervals

**Solutions:**

#### A. Server-Side Configuration (Recommended for Local Development)

Update `eureka-server/src/main/resources/application.yml`:

```yaml
eureka:
  server:
    # Disable self-preservation for local development
    enable-self-preservation: false
    # More frequent eviction for local dev
    eviction-interval-timer-in-ms: 5000
    # Lower renewal threshold
    renewal-percent-threshold: 0.85
    # Faster response cache updates
    response-cache-update-interval-ms: 3000
```

#### B. Client-Side Configuration

Add to each service's `application.yml`:

```yaml
eureka:
  instance:
    # Faster heartbeat for local development
    lease-renewal-interval-in-seconds: 5
    # Shorter lease duration
    lease-expiration-duration-in-seconds: 10
    # Enable health check
    health-check-url-path: /actuator/health
    # Prefer IP address
    prefer-ip-address: true
  client:
    # Faster registry fetch
    registry-fetch-interval-seconds: 5
    # Enable health check
    healthcheck:
      enabled: true
```

### 2. Service Registration Issues

**Problem:** Services not appearing in Eureka dashboard

**Solutions:**

#### Check Service Configuration

Ensure each service has:

```yaml
spring:
  application:
    name: your-service-name

eureka:
  client:
    enabled: true
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 5
    lease-expiration-duration-in-seconds: 10
```

#### Check Network Connectivity

```bash
# Test Eureka server connectivity
curl http://localhost:8761/eureka/apps

# Check if service can reach Eureka
curl http://localhost:8761/actuator/health
```

### 3. Service Discovery Issues

**Problem:** Services can't find each other

**Solutions:**

#### A. Use Service Names in Feign Clients

```java
@FeignClient(name = "user-management-service")
public interface UserManagementClient {
    @GetMapping("/api/users/{id}")
    UserDto getUser(@PathVariable String id);
}
```

#### B. Check Service Names

Ensure service names match exactly:

```yaml
# In user-management-service/application.yml
spring:
  application:
    name: user-management-service

# In authentication-service/application.yml  
spring:
  application:
    name: authentication-service
```

### 4. Startup Order Issues

**Problem:** Services fail to start due to dependencies

**Solution:** Start services in correct order:

1. **Eureka Server** (port 8761)
2. **Core Services** (ports 9081-9086)
3. **Authentication Service** (port 9083)
4. **API Gateway** (port 9080)

### 5. Health Check Issues

**Problem:** Services show as UP but are actually down

**Solutions:**

#### A. Enable Health Checks

```yaml
eureka:
  instance:
    health-check-url-path: /actuator/health
  client:
    healthcheck:
      enabled: true
```

#### B. Custom Health Indicators

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Your health check logic
        return Health.up().build();
    }
}
```

### 6. Performance Issues

**Problem:** Slow service discovery or registration

**Solutions:**

#### A. Optimize Intervals

```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 5
    lease-expiration-duration-in-seconds: 10
  client:
    registry-fetch-interval-seconds: 5
    eureka-server-connect-timeout-seconds: 5
    eureka-server-read-timeout-seconds: 5
```

#### B. Disable Unnecessary Features

```yaml
eureka:
  server:
    enable-self-preservation: false  # For local development
    eviction-interval-timer-in-ms: 5000
```

### 7. Debugging Commands

#### Check Eureka Status

```bash
# Check Eureka server
curl http://localhost:8761/actuator/health

# List all registered services
curl http://localhost:8761/eureka/apps

# Check specific service
curl http://localhost:8761/eureka/apps/USER-MANAGEMENT-SERVICE
```

#### Check Service Health

```bash
# Check all services
for port in 8761 9080 9081 9082 9083 9084 9085 9086; do
    echo "Port $port:"
    curl -s http://localhost:$port/actuator/health | jq '.status' 2>/dev/null || echo "DOWN"
done
```

#### Monitor Logs

```bash
# Monitor Eureka logs
tail -f logs/eureka-server.log

# Monitor service logs
tail -f logs/*.log | grep -i eureka
```

### 8. Production vs Development Settings

#### Development Settings (Current)

```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 5000
    renewal-percent-threshold: 0.85
  instance:
    lease-renewal-interval-in-seconds: 5
    lease-expiration-duration-in-seconds: 10
```

#### Production Settings

```yaml
eureka:
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 60000
    renewal-percent-threshold: 0.85
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

### 9. Quick Fix Script

Create a script to reset Eureka state:

```bash
#!/bin/bash
echo "Resetting Eureka state..."

# Stop all services
./scripts/start-local.sh stop

# Clear Eureka cache (if any)
rm -rf logs/*

# Restart Eureka first
cd eureka-server
mvn spring-boot:run &
cd ..

# Wait for Eureka to start
sleep 10

# Start other services
./scripts/start-local.sh start

echo "Eureka reset complete!"
```

### 10. Prevention Tips

1. **Always start Eureka first**
2. **Use consistent service names**
3. **Enable health checks**
4. **Monitor logs for warnings**
5. **Use appropriate intervals for your environment**
6. **Test service discovery regularly**

### 11. Emergency Recovery

If Eureka is completely broken:

```bash
# 1. Stop all services
./scripts/start-local.sh stop

# 2. Clear all logs
rm -rf logs/*

# 3. Restart Eureka with clean state
cd eureka-server
mvn spring-boot:run

# 4. In another terminal, restart other services
./scripts/start-local.sh start
```

This should resolve the self-preservation warning and ensure proper service discovery. 