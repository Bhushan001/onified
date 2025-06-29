# Authentication Service - Swagger Documentation

This document provides information about the Swagger/OpenAPI documentation for the Authentication Service.

## Overview

The Authentication Service now includes comprehensive Swagger documentation that provides interactive API documentation, testing capabilities, and detailed endpoint specifications for user authentication and authorization.

## Features

- **Interactive API Documentation**: Browse and test authentication APIs directly from the Swagger UI
- **OpenAPI 3.0 Specification**: Modern, standardized API documentation format
- **Comprehensive Endpoint Coverage**: All authentication endpoints are documented with examples
- **Request/Response Examples**: Pre-filled examples for easy testing
- **Error Response Documentation**: Detailed error codes and messages
- **Schema Validation**: Automatic validation of request/response schemas
- **Security Documentation**: JWT authentication scheme documentation

## Accessing the Documentation

### Swagger UI
Once the service is running, you can access the Swagger UI at:
```
http://localhost:9083/swagger-ui.html
```

### OpenAPI JSON Specification
The raw OpenAPI specification is available at:
```
http://localhost:9083/api-docs
```

### OpenAPI YAML Specification
A static OpenAPI specification file is also provided:
```
openapi-spec.yaml
```

## API Endpoints

The service provides the following API groups:

### 1. Authentication
- **POST** `/api/authentication/login` - Authenticate user
- **POST** `/api/authentication/refresh` - Refresh access token
- **POST** `/api/authentication/register` - Register new user
- **GET** `/api/authentication/health` - Health check

### 2. Public Endpoints
- **GET** `/api/public/test` - Public test endpoint

## Data Models

### LoginRequest
```json
{
  "username": "john.doe",
  "password": "securePassword123"
}
```

### LoginResponse
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "john.doe",
  "message": "Login successful",
  "status": "SUCCESS"
}
```

### UserCreateRequest
```json
{
  "username": "john.doe",
  "password": "SecurePassword123!",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER"]
}
```

### UserResponse
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "roles": ["USER"]
}
```

### ApiResponse<T>
Standardized response wrapper:
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": { /* actual response data */ }
}
```

## Configuration

The Swagger documentation is configured in `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    display-request-duration: true
    try-it-out-enabled: true
    filter: true
  packages-to-scan: com.onified.ai.authentication_service.controller
  paths-to-match: /api/**
```

## Dependencies

The following dependencies are required for Swagger documentation:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-common</artifactId>
    <version>2.2.0</version>
</dependency>
```

## Usage Examples

### User Login
1. Navigate to the Swagger UI
2. Find the "Authentication" section
3. Click on "POST /api/authentication/login"
4. Click "Try it out"
5. Enter the request body:
```json
{
  "username": "john.doe",
  "password": "securePassword123"
}
```
6. Click "Execute"

### User Registration
1. Navigate to the "Authentication" section
2. Click on "POST /api/authentication/register"
3. Click "Try it out"
4. Enter the request body:
```json
{
  "username": "newuser",
  "password": "SecurePassword123!",
  "email": "newuser@example.com",
  "firstName": "New",
  "lastName": "User",
  "roles": ["USER"]
}
```
5. Click "Execute"

### Token Refresh
1. Navigate to the "Authentication" section
2. Click on "POST /api/authentication/refresh"
3. Click "Try it out"
4. Enter the refresh token parameter
5. Click "Execute"

## Error Handling

The API uses standard HTTP status codes:

- **200** - Success
- **201** - Created
- **400** - Bad Request
- **401** - Unauthorized
- **409** - Conflict (user already exists)
- **500** - Internal Server Error

## Security

The API documentation includes security schemes for JWT authentication:

- **Bearer Token**: JWT tokens are used for authentication
- **Token Refresh**: Automatic token renewal mechanism
- **Keycloak Integration**: Secure authentication using Keycloak

## Keycloak Integration

This service integrates with Keycloak for authentication:

- **Realm**: `onified`
- **Client**: `onified-auth-service`
- **Token Format**: JWT
- **Token Expiration**: Configurable (default: 1 hour)

## Development

### Adding New Endpoints
When adding new endpoints, ensure you include the following annotations:

```java
@Operation(
    summary = "Brief description",
    description = "Detailed description"
)
@ApiResponses(value = {
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200", 
        description = "Success description"
    ),
    // Add other response codes as needed
})
@Parameter(description = "Parameter description", required = true)
```

### Updating Documentation
1. Add OpenAPI annotations to your controllers and DTOs
2. Update the `openapi-spec.yaml` file if needed
3. Test the documentation by running the service and accessing Swagger UI

## Testing

### Manual Testing
Use the interactive Swagger UI to test all endpoints with real data.

### Automated Testing
The service includes comprehensive test coverage for all authentication flows.

## Troubleshooting

### Common Issues

1. **Swagger UI not accessible**: Ensure the service is running and the port is correct (9083)
2. **Missing endpoints**: Check that controllers are in the correct package (`com.onified.ai.authentication_service.controller`)
3. **Import errors**: Ensure all SpringDoc dependencies are included in `pom.xml`
4. **Authentication errors**: Verify Keycloak is running and properly configured

### Building the Project
```bash
mvn clean install
```

### Running the Service
```bash
mvn spring-boot:run
```

### Keycloak Setup
Ensure Keycloak is running with the following configuration:
- Realm: `onified`
- Client: `onified-auth-service`
- Client Secret: Configured in application.yml

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)

## Support

For issues related to the API documentation, please contact the development team or create an issue in the project repository. 