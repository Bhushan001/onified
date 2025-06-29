# Platform Management Service - Swagger Security Configuration

## 🔐 Security Configuration for Swagger UI

The Platform Management Service includes Spring Security configuration that controls access to various endpoints, including Swagger UI and OpenAPI documentation.

## 📋 Current Security Configuration

### Public Access Endpoints

The following endpoints are configured for public access (no authentication required):

#### 1. Swagger UI and OpenAPI Documentation
```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
.requestMatchers("/api-docs/**", "/api-docs.yaml", "/api-docs").permitAll()
.requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/v3/api-docs").permitAll()
.requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
.requestMatchers("/configuration/ui", "/configuration/security").permitAll()
```

**Accessible URLs**:
- `http://localhost:9081/swagger-ui.html` - Swagger UI Interface
- `http://localhost:9081/api-docs` - OpenAPI JSON Specification
- `http://localhost:9081/api-docs.yaml` - OpenAPI YAML Specification
- `http://localhost:9081/v3/api-docs` - Alternative OpenAPI JSON endpoint

#### 2. Actuator Endpoints (Monitoring)
```java
.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
.requestMatchers("/actuator/info").permitAll()
```

**Accessible URLs**:
- `http://localhost:9081/actuator/health` - Service health check
- `http://localhost:9081/actuator/info` - Service information

#### 3. Public API Endpoints
```java
.requestMatchers("/api/public/**").permitAll()
```

**Accessible URLs**:
- `http://localhost:9081/api/public/test` - Public test endpoint

#### 4. API Endpoints (Temporarily Public for Testing)
```java
.requestMatchers("/api/password-policies/**").permitAll()
.requestMatchers("/api/tenants/**").permitAll()
```

**Note**: These endpoints are currently public for testing purposes. In production, they should be secured with proper authentication.

## 🔧 Configuration Details

### Security Configuration File
**Location**: `src/main/java/com/onified/ai/platform_management/security/SecurityConfig.java`

### Key Security Features
- **CSRF Disabled**: Cross-Site Request Forgery protection is disabled for API endpoints
- **Stateless Sessions**: No session management (JWT-based authentication expected)
- **Request-Based Authorization**: URL pattern-based access control

## 🚀 How to Access Swagger UI

### After Starting the Service
1. **Start the Platform Management Service**
2. **Navigate to**: `http://localhost:9081/swagger-ui.html`
3. **You should see**: The Swagger UI interface with all documented endpoints

### Alternative Access Points
- **OpenAPI JSON**: `http://localhost:9081/api-docs`
- **OpenAPI YAML**: `http://localhost:9081/api-docs.yaml`
- **Health Check**: `http://localhost:9081/actuator/health`

## 🔒 Production Security Considerations

### Current Configuration (Development/Testing)
- Swagger UI is publicly accessible
- API endpoints are temporarily public for testing
- No authentication required for documentation

### Recommended Production Configuration
```java
// In production, you might want to:
.requestMatchers("/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN")  // Admin only
.requestMatchers("/api-docs/**").hasRole("DEVELOPER")                    // Developer only
.requestMatchers("/api/password-policies/**").hasRole("ADMIN")           // Admin only
.requestMatchers("/api/tenants/**").hasRole("ADMIN")                     // Admin only
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. "Access Denied" Error
**Problem**: Cannot access Swagger UI
**Solution**: Ensure the security configuration includes the Swagger paths:
```java
.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
```

#### 2. "404 Not Found" Error
**Problem**: Swagger UI page not found
**Solution**: 
- Verify the service is running on port 9081
- Check that SpringDoc dependency is included in `pom.xml`
- Ensure the correct URL: `http://localhost:9081/swagger-ui.html`

## ✅ Verification Checklist

- [ ] Service starts without security errors
- [ ] Swagger UI accessible at `http://localhost:9081/swagger-ui.html`
- [ ] OpenAPI JSON accessible at `http://localhost:9081/api-docs`
- [ ] Health check accessible at `http://localhost:9081/actuator/health`
- [ ] Public test endpoint accessible at `http://localhost:9081/api/public/test`
- [ ] API endpoints accessible for testing

The security configuration now allows public access to Swagger UI and related documentation endpoints while maintaining security for other resources. 