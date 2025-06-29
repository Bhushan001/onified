# Platform Management Service - Swagger/OpenAPI Documentation

## Overview

The Platform Management Service now includes comprehensive Swagger/OpenAPI documentation that provides interactive API documentation, testing capabilities, and detailed schema information.

## Features

- **Interactive API Documentation**: Browse and test all API endpoints directly from the browser
- **Comprehensive Schema Documentation**: Detailed documentation of all request/response models
- **Authentication Support**: Documentation includes authentication requirements
- **Response Examples**: Pre-configured examples for all endpoints
- **Error Handling**: Documented error responses and status codes

## Accessing the Documentation

### Swagger UI
- **URL**: `http://localhost:9081/swagger-ui.html`
- **Description**: Interactive web interface for exploring and testing APIs

### OpenAPI JSON Specification
- **URL**: `http://localhost:9081/api-docs`
- **Description**: Raw OpenAPI 3.0 specification in JSON format

### OpenAPI YAML Specification
- **URL**: `http://localhost:9081/api-docs.yaml`
- **Description**: Raw OpenAPI 3.0 specification in YAML format

## API Endpoints Documentation

### 1. Public Test Endpoints
- **Tag**: Public Test
- **Description**: Public endpoints for testing service connectivity
- **Endpoints**:
  - `GET /api/public/test` - Test service connectivity

### 2. Password Policy Management
- **Tag**: Password Policy Management
- **Description**: APIs for managing password policies and security configurations
- **Endpoints**:
  - `GET /api/password-policies/platform` - Get platform default password policy
  - `GET /api/password-policies` - Get all password policies
  - `GET /api/password-policies/{id}` - Get password policy by ID
  - `GET /api/password-policies/name/{policyName}` - Get password policy by name
  - `GET /api/password-policies/default` - Get default password policy
  - `POST /api/password-policies` - Create new password policy
  - `PUT /api/password-policies/{id}` - Update password policy
  - `DELETE /api/password-policies/{id}` - Delete password policy
  - `PUT /api/password-policies/{id}/default` - Set password policy as default
  - `GET /api/password-policies/default/ensure` - Ensure default policy exists

### 3. Tenant Management
- **Tag**: Tenant Management
- **Description**: APIs for managing multi-tenant configurations and tenant lifecycle operations
- **Endpoints**:
  - `GET /api/tenants` - Get all tenants
  - `GET /api/tenants/{tenantId}` - Get tenant by ID
  - `POST /api/tenants` - Create new tenant
  - `PUT /api/tenants/{tenantId}` - Update tenant
  - `DELETE /api/tenants/{tenantId}` - Delete tenant

## Data Models

### PasswordPolicy
```json
{
  "id": 1,
  "policyName": "Standard Policy",
  "description": "Standard password policy for all users",
  "minLength": 10,
  "maxPasswordAge": 90,
  "minPasswordAge": 0,
  "passwordHistory": 4,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "[FirstInitial][LastInitial][Random]",
  "bannedPatterns": "password,123456,qwerty,admin",
  "isActive": true,
  "isDefault": false
}
```

### TenantDTO
```json
{
  "tenantId": "tenant-001",
  "name": "Acme Corporation",
  "status": "ACTIVE",
  "extraConfig": "{\"timezone\": \"UTC\", \"locale\": \"en-US\"}",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "createdBy": "admin@onified.com",
  "updatedBy": "admin@onified.com"
}
```

### ApiResponse<T>
```json
{
  "status": 200,
  "message": "SUCCESS",
  "data": {}
}
```

### CustomErrorResponse
```json
{
  "status": 400,
  "message": "ERROR",
  "details": "Invalid request parameters",
  "additionalInfo": "Additional error information"
}
```

## Configuration

### SpringDoc Properties
The following properties are configured in `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    display-request-duration: true
  packages-to-scan: com.onified.ai.platform_management.controller
  paths-to-match: /api/**
```

### OpenAPI Configuration
The OpenAPI configuration is defined in `OpenApiConfig.java` and includes:
- API information (title, version, description)
- Contact information
- License information
- Server configurations for local and Docker environments

## Usage Examples

### Testing with Swagger UI

1. **Access Swagger UI**: Navigate to `http://localhost:9081/swagger-ui.html`
2. **Select an Endpoint**: Choose from the available endpoints
3. **Click "Try it out"**: This enables interactive testing
4. **Fill Parameters**: Enter required parameters
5. **Execute**: Click "Execute" to test the endpoint
6. **View Response**: See the actual response from the service

### Testing with curl

```bash
# Test service connectivity
curl -X GET "http://localhost:9081/api/public/test"

# Get all password policies
curl -X GET "http://localhost:9081/api/password-policies"

# Create a new password policy
curl -X POST "http://localhost:9081/api/password-policies" \
  -H "Content-Type: application/json" \
  -d '{
    "policyName": "Test Policy",
    "description": "Test password policy",
    "minLength": 8,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSpecial": false
  }'

# Get all tenants
curl -X GET "http://localhost:9081/api/tenants"
```

## Security Considerations

- **Authentication**: Most endpoints require authentication (JWT, API Key, or OAuth2)
- **Authorization**: Ensure proper permissions before accessing endpoints
- **Rate Limiting**: API calls are rate-limited for system stability
- **Input Validation**: All inputs are validated according to defined schemas

## Troubleshooting

### Common Issues

1. **Swagger UI not accessible**
   - Ensure the service is running on port 9081
   - Check if the SpringDoc dependency is properly included
   - Verify the application.yml configuration

2. **Missing endpoints in documentation**
   - Ensure controllers are in the `com.onified.ai.platform_management.controller` package
   - Check that endpoints use the `/api/**` path pattern
   - Verify OpenAPI annotations are properly applied

3. **Schema not displaying correctly**
   - Ensure DTOs and entities have proper `@Schema` annotations
   - Check for import conflicts with OpenAPI annotations
   - Verify Lombok annotations are properly configured

### Debug Mode

To enable debug logging for SpringDoc:

```yaml
logging:
  level:
    org.springdoc: DEBUG
```

## Development

### Adding New Endpoints

1. **Add OpenAPI Annotations**: Use `@Operation`, `@Parameter`, `@ApiResponse` annotations
2. **Document Request/Response Models**: Add `@Schema` annotations to DTOs
3. **Update Configuration**: Ensure new controllers are scanned
4. **Test Documentation**: Verify endpoints appear in Swagger UI

### Example New Endpoint

```java
@GetMapping("/example")
@Operation(
    summary = "Example Endpoint",
    description = "Example endpoint with OpenAPI documentation",
    responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Success",
            content = @Content(schema = @Schema(implementation = ExampleDTO.class))
        )
    }
)
public ResponseEntity<ExampleDTO> exampleEndpoint() {
    // Implementation
}
```

## Dependencies

The following dependency is required for Swagger/OpenAPI support:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## References

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/) 