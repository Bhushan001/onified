# Application Config Service - Swagger Documentation

This document provides information about the Swagger/OpenAPI documentation for the Application Config Service.

## Overview

The Application Config Service now includes comprehensive Swagger documentation that provides interactive API documentation, testing capabilities, and detailed endpoint specifications.

## Features

- **Interactive API Documentation**: Browse and test APIs directly from the Swagger UI
- **OpenAPI 3.0 Specification**: Modern, standardized API documentation format
- **Comprehensive Endpoint Coverage**: All REST endpoints are documented with examples
- **Request/Response Examples**: Pre-filled examples for easy testing
- **Error Response Documentation**: Detailed error codes and messages
- **Schema Validation**: Automatic validation of request/response schemas

## Accessing the Documentation

### Swagger UI
Once the service is running, you can access the Swagger UI at:
```
http://localhost:9082/swagger-ui.html
```

### OpenAPI JSON Specification
The raw OpenAPI specification is available at:
```
http://localhost:9082/api-docs
```

### OpenAPI YAML Specification
A static OpenAPI specification file is also provided:
```
openapi-spec.yaml
```

## API Endpoints

The service provides the following API groups:

### 1. Application Management
- **POST** `/api/applications` - Create a new application
- **GET** `/api/applications` - Get all applications
- **GET** `/api/applications/{appCode}` - Get application by code
- **PUT** `/api/applications/{appCode}` - Update an application
- **DELETE** `/api/applications/{appCode}` - Delete an application

### 2. Module Management
- **POST** `/api/modules` - Create a new application module
- **GET** `/api/modules/{moduleId}` - Get module by ID
- **GET** `/api/modules/by-app/{appCode}` - Get modules by application code
- **PUT** `/api/modules/{moduleId}` - Update an application module
- **DELETE** `/api/modules/{moduleId}` - Delete an application module

### 3. Public Endpoints
- **GET** `/api/public/test` - Public test endpoint

## Data Models

### ApplicationRequestDTO
```json
{
  "appCode": "APP001",
  "displayName": "User Management System",
  "isActive": true
}
```

### ApplicationResponseDTO
```json
{
  "appCode": "APP001",
  "displayName": "User Management System",
  "isActive": true
}
```

### ModuleRequestDTO
```json
{
  "appCode": "APP001",
  "moduleCode": "MOD001",
  "isActive": true
}
```

### ModuleResponseDTO
```json
{
  "moduleId": 1,
  "appCode": "APP001",
  "moduleCode": "MOD001",
  "isActive": true
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
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    display-request-duration: true
  packages-to-scan: com.onified.ai.appConfig.controller
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

### Creating an Application
1. Navigate to the Swagger UI
2. Find the "Application Management" section
3. Click on "POST /api/applications"
4. Click "Try it out"
5. Enter the request body:
```json
{
  "appCode": "APP001",
  "displayName": "User Management System",
  "isActive": true
}
```
6. Click "Execute"

### Creating a Module
1. Navigate to the "Module Management" section
2. Click on "POST /api/modules"
3. Click "Try it out"
4. Enter the request body:
```json
{
  "appCode": "APP001",
  "moduleCode": "MOD001",
  "isActive": true
}
```
5. Click "Execute"

## Error Handling

The API uses standard HTTP status codes:

- **200** - Success
- **201** - Created
- **204** - No Content (for deletions)
- **400** - Bad Request
- **404** - Not Found
- **409** - Conflict (duplicate entries)
- **500** - Internal Server Error

## Security

The API documentation includes security schemes for JWT authentication, though the current implementation may not require authentication for all endpoints.

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

## Troubleshooting

### Common Issues

1. **Swagger UI not accessible**: Ensure the service is running and the port is correct
2. **Missing endpoints**: Check that controllers are in the correct package (`com.onified.ai.appConfig.controller`)
3. **Import errors**: Ensure all SpringDoc dependencies are included in `pom.xml`

### Building the Project
```bash
mvn clean install
```

### Running the Service
```bash
mvn spring-boot:run
```

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

## Support

For issues related to the API documentation, please contact the development team or create an issue in the project repository. 