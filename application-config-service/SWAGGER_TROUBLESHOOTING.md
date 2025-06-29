# Swagger UI Troubleshooting Guide

This guide helps resolve common issues when accessing Swagger UI for the Application Config Service.

## Issue: "Forbidden" Error When Accessing Swagger UI

### Problem
You're getting a "403 Forbidden" error when trying to access:
- `http://localhost:9082/swagger-ui.html`
- `http://localhost:9082/api-docs`

### Root Cause
Spring Security is blocking access to Swagger UI endpoints because they're not explicitly permitted in the security configuration.

### Solution
The security configuration has been updated to allow access to Swagger UI endpoints. If you're still experiencing issues:

1. **Restart the application** after the security configuration changes
2. **Clear browser cache** and try again
3. **Check the application logs** for any security-related errors

## Common Issues and Solutions

### 1. Swagger UI Not Loading

**Symptoms:**
- Blank page when accessing `/swagger-ui.html`
- Console errors in browser developer tools

**Solutions:**
```bash
# 1. Check if the service is running
curl http://localhost:9082/actuator/health

# 2. Check if OpenAPI docs are accessible
curl http://localhost:9082/api-docs

# 3. Restart the application
mvn spring-boot:run
```

### 2. 404 Not Found

**Symptoms:**
- 404 error when accessing Swagger UI
- "Page not found" error

**Solutions:**
1. Verify the service is running on the correct port (9082)
2. Check if SpringDoc dependencies are properly included
3. Ensure the application started without errors

### 3. CORS Issues

**Symptoms:**
- CORS errors in browser console
- Swagger UI loads but API calls fail

**Solutions:**
The security configuration has been updated to handle CORS. If issues persist:

1. Check if you're accessing from the correct origin
2. Verify the service is running on the expected port
3. Check browser console for specific CORS error messages

### 4. Authentication Required

**Symptoms:**
- "Authentication required" errors
- Redirect to login page

**Solutions:**
The security configuration has been updated to permit Swagger UI access without authentication. If you still see authentication prompts:

1. Clear browser cache and cookies
2. Try accessing in an incognito/private window
3. Check if there are any additional security filters

## Testing Steps

### Step 1: Verify Service is Running
```bash
# Test health endpoint
curl http://localhost:9082/actuator/health

# Expected response:
# {"status":"UP"}
```

### Step 2: Test Public Endpoints
```bash
# Test public test endpoint
curl http://localhost:9082/api/public/test

# Expected response:
# "Application Config Service is reachable (public)"

# Test health endpoint
curl http://localhost:9082/api/public/health

# Expected response:
# "Application Config Service is healthy"
```

### Step 3: Test OpenAPI Documentation
```bash
# Test OpenAPI JSON endpoint
curl http://localhost:9082/api-docs

# Expected response: JSON OpenAPI specification
```

### Step 4: Access Swagger UI
1. Open your browser
2. Navigate to: `http://localhost:9082/swagger-ui.html`
3. You should see the Swagger UI interface

## Configuration Verification

### Check Security Configuration
Ensure your `SecurityConfig.java` includes these patterns:

```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
.requestMatchers("/api-docs/**", "/api-docs").permitAll()
.requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll()
.requestMatchers("/swagger-resources/**").permitAll()
.requestMatchers("/webjars/**").permitAll()
```

### Check Application Properties
Ensure your `application.yml` includes:

```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```

### Check Dependencies
Ensure your `pom.xml` includes:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

## Debugging

### Enable Debug Logging
Add to `application.yml`:

```yaml
logging:
  level:
    org.springdoc: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### Check Application Logs
Look for these log messages during startup:

```
INFO  - SpringDoc OpenAPI started
INFO  - Swagger UI available at: /swagger-ui.html
INFO  - OpenAPI documentation available at: /api-docs
```

### Browser Developer Tools
1. Open browser developer tools (F12)
2. Check the Console tab for JavaScript errors
3. Check the Network tab for failed requests
4. Look for any CORS or security-related errors

## Alternative Access Methods

If Swagger UI is still not accessible, try these alternatives:

### 1. Direct API Testing
Use tools like Postman or curl to test the API directly:

```bash
# Test creating an application
curl -X POST http://localhost:9082/api/applications \
  -H "Content-Type: application/json" \
  -d '{"appCode":"TEST001","displayName":"Test App","isActive":true}'
```

### 2. OpenAPI Specification
Access the raw OpenAPI specification:

```bash
curl http://localhost:9082/api-docs > openapi.json
```

Then use online tools like:
- [Swagger Editor](https://editor.swagger.io/)
- [Swagger UI Online](https://petstore.swagger.io/)

### 3. Alternative Ports
If port 9082 is blocked, try changing the port in `application.yml`:

```yaml
server:
  port: 8080  # or any other available port
```

## Still Having Issues?

If you're still experiencing problems:

1. **Check the application startup logs** for any errors
2. **Verify all dependencies** are properly downloaded
3. **Try a clean build**: `mvn clean install`
4. **Check if the port is already in use**: `netstat -an | grep 9082`
5. **Try running on a different port**
6. **Check firewall settings** if applicable

## Support

For additional help:
1. Check the application logs for specific error messages
2. Verify the service is running correctly
3. Test the basic endpoints first before trying Swagger UI
4. Contact the development team with specific error messages and logs 