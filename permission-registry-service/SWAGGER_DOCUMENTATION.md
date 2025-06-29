# Permission Registry Service - Swagger Documentation

## Overview

The Permission Registry Service provides comprehensive RBAC/ABAC (Role-Based Access Control / Attribute-Based Access Control) functionality for the Onified.ai platform. This document explains how to access and use the Swagger documentation.

## Accessing Swagger UI

### Local Development
- **Swagger UI**: http://localhost:9084/swagger-ui.html
- **OpenAPI JSON**: http://localhost:9084/api-docs
- **OpenAPI YAML**: http://localhost:9084/api-docs.yaml

### Production
- **Swagger UI**: https://api.onified.ai/permission-registry/swagger-ui.html
- **OpenAPI JSON**: https://api.onified.ai/permission-registry/api-docs

## Service Features

### Core Components

1. **Role Management** (`/api/roles`)
   - Create, read, update, and delete roles
   - Support for role inheritance and hierarchy
   - Tenant customization capabilities
   - Role function categorization

2. **Permission Bundle Units (PBUs)** (`/api/pbus`)
   - Granular permission definitions
   - API endpoint mapping
   - Action and scope associations
   - Version control support

3. **Scopes** (`/api/scopes`)
   - Resource scope definitions
   - Hierarchical scope structures
   - Active/inactive state management

4. **Actions** (`/api/actions`)
   - Available action definitions
   - Action categorization
   - State management

5. **Constraints**
   - **General Constraints** (`/api/constraints/general`): Application-wide constraint rules
   - **Field Constraints** (`/api/constraints/field`): Field-level access control

6. **Contextual Behaviors** (`/api/behaviors`)
   - Context-aware permission behaviors
   - Dynamic permission evaluation
   - Behavior state management

7. **Associations**
   - **PBU-Constraint Associations** (`/api/pbus/constraints`): Link constraints to PBUs
   - **Role Inheritance** (`/api/role-inheritance`): Define role hierarchies
   - **Role Constraint Overrides** (`/api/role-constraint-overrides`): Role-specific constraint modifications

## API Response Format

All API responses follow a standardized format:

```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": {
    // Actual response data
  }
}
```

### Error Response Format

```json
{
  "statusCode": 404,
  "status": "NOT_FOUND",
  "body": {
    "errorCode": "NOT_FOUND",
    "message": "Role not found"
  }
}
```

## Using Swagger UI

### 1. Authentication
- The service currently allows public access to all endpoints
- JWT Bearer token authentication is configured for future use
- No authentication required for testing

### 2. Testing Endpoints

#### Health Check
```bash
GET /api/public/health
```
Returns service health status and timestamp.

#### Service Information
```bash
GET /api/public/info
```
Returns basic service information and features.

#### Echo Test
```bash
POST /api/public/echo
Content-Type: application/json

{
  "test": "data"
}
```
Echoes back the provided data for testing.

### 3. Role Management Examples

#### Create a Role
```bash
POST /api/roles
Content-Type: application/json

{
  "roleId": "ADMIN_ROLE",
  "displayName": "Administrator Role",
  "appCode": "USER_MGMT",
  "moduleCode": "AUTH",
  "roleFunction": "USER_ADMINISTRATION",
  "isActive": true,
  "tenantCustomizable": false
}
```

#### Get All Roles
```bash
GET /api/roles
```

#### Get Role by ID
```bash
GET /api/roles/ADMIN_ROLE
```

#### Update Role
```bash
PUT /api/roles/ADMIN_ROLE
Content-Type: application/json

{
  "roleId": "ADMIN_ROLE",
  "displayName": "Updated Administrator Role",
  "appCode": "USER_MGMT",
  "moduleCode": "AUTH",
  "roleFunction": "USER_ADMINISTRATION",
  "isActive": true,
  "tenantCustomizable": true
}
```

#### Delete Role
```bash
DELETE /api/roles/ADMIN_ROLE
```

### 4. Permission Bundle Unit Examples

#### Create a PBU
```bash
POST /api/pbus
Content-Type: application/json

{
  "pbuId": "USER_CREATE_PBU",
  "displayName": "User Creation Permission",
  "apiEndpoint": "/api/users",
  "actionCode": "CREATE",
  "scopeCode": "USER",
  "isActive": true,
  "version": "1.0"
}
```

#### Get All PBUs
```bash
GET /api/pbus
```

#### Get PBU by ID
```bash
GET /api/pbus/USER_CREATE_PBU
```

## Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Request successful, no content to return
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists
- **500 Internal Server Error**: Server error

## Development and Testing

### Local Development Setup

1. **Start the service**:
   ```bash
   cd permission-registry-service
   ./mvnw spring-boot:run
   ```

2. **Access Swagger UI**: http://localhost:9084/swagger-ui.html

3. **Test endpoints** using the Swagger UI interface

### Database Setup

The service requires a PostgreSQL database. Configure the connection in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/permission_registry_db
    username: postgres
    password: root
```

### Environment Variables

- `PERMISSION_REGISTRY_PORT`: Service port (default: 9084)
- `PERMISSION_DB_URL`: Database URL
- `PERMISSION_DB_USERNAME`: Database username
- `PERMISSION_DB_PASSWORD`: Database password
- `EUREKA_ENABLED`: Enable Eureka client (default: true)
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: Eureka server URL

## Troubleshooting

### Common Issues

1. **Swagger UI not accessible**
   - Check if the service is running on the correct port
   - Verify security configuration allows access to `/swagger-ui/**`
   - Check application logs for errors

2. **Database connection issues**
   - Verify PostgreSQL is running
   - Check database credentials in `application.yml`
   - Ensure database `permission_registry_db` exists

3. **CORS issues**
   - The service has CORS enabled for cross-origin requests
   - Check browser console for CORS errors

### Logs

Check application logs for detailed error information:
- Log file: `/app/logs/permission-registry-service.log` (Docker) or console output
- Log level: DEBUG for detailed information

## Next Steps

1. **Implement Authentication**: Add JWT token validation
2. **Add Authorization**: Implement role-based access control for the API itself
3. **Add Validation**: Implement request validation and error handling
4. **Add Caching**: Implement caching for frequently accessed data
5. **Add Monitoring**: Add metrics and monitoring capabilities

## Support

For issues or questions:
- Check the application logs
- Review the OpenAPI specification
- Contact the development team at dev@onified.ai 