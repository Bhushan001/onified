# Tenant Management Service - Postman Collection

This directory contains the complete Postman setup for the Tenant Management Service, including the API collection, environment configurations, and usage documentation.

## Files Overview

- **Tenant_Management_Service_Collection.json** - Complete API collection with all endpoints
- **Tenant_Management_Service_Environment.json** - Environment for direct service calls
- **Tenant_Management_Service_Gateway_Environment.json** - Environment for API gateway calls
- **README.md** - This documentation file

## Service Information

- **Service Name**: tenant-management-service
- **Default Port**: 9086
- **Base Path**: `/api/tenants/{tenantId}/config`
- **Public Test Endpoint**: `/public/test`

## Quick Start

1. **Import the Collection**: Import `Tenant_Management_Service_Collection.json` into Postman
2. **Import Environment**: Choose either the direct or gateway environment file
3. **Set Environment**: Select the appropriate environment in Postman
4. **Test Connection**: Use the "Public Test" request to verify connectivity

## Environment Setup

### Direct Service Environment
Use this environment for direct calls to the tenant-management-service:
- **Base URL**: `http://localhost:9086`
- **Service Port**: 9086

### Gateway Environment
Use this environment for calls through the API gateway:
- **Base URL**: `http://localhost:8080`
- **Gateway Port**: 8080
- **Service Port**: 9086

## API Endpoints

### Public Endpoints

#### GET /public/test
- **Description**: Public endpoint to test service connectivity
- **Authentication**: None required
- **Response**: Simple text response confirming service is reachable

### Tenant Configuration Endpoints

#### GET /api/tenants/{tenantId}/config
- **Description**: Retrieve tenant configuration
- **Authentication**: None required
- **Path Parameter**: `tenantId` (string)
- **Response**: Tenant configuration object
- **Behavior**: If config doesn't exist, a default config will be created and returned

#### PUT /api/tenants/{tenantId}/config
- **Description**: Update tenant configuration
- **Authentication**: None required
- **Path Parameter**: `tenantId` (string)
- **Request Body**:
```json
{
  "tenantId": "tenant-001",
  "branding": "{\"logo\": \"https://example.com/logo.png\", \"primaryColor\": \"#007bff\", \"secondaryColor\": \"#6c757d\"}",
  "appSubscriptions": ["app1", "app2", "app3"]
}
```

## Environment Variables

### Common Variables
- `base_url`: Service base URL
- `service_name`: Service name (tenant-management-service)
- `service_port`: Service port (9086)
- `tenant_id`: Tenant ID for testing
- `test_tenant_id`: Test tenant ID

### Sample Data Variables
- `sample_branding_json`: Sample branding configuration in JSON format
- `sample_app_subscriptions`: Sample app subscriptions array

### Gateway-Specific Variables
- `gateway_port`: API gateway port (8080)
- `auth_token`: Authentication token (for future use)

## Usage Examples

### 1. Test Service Connectivity
```bash
GET {{base_url}}/public/test
```

### 2. Get Tenant Configuration
```bash
GET {{base_url}}/api/tenants/{{tenant_id}}/config
```

### 3. Update Tenant Configuration
```bash
PUT {{base_url}}/api/tenants/{{tenant_id}}/config
Content-Type: application/json

{
  "tenantId": "{{tenant_id}}",
  "branding": "{{sample_branding_json}}",
  "appSubscriptions": {{sample_app_subscriptions}}
}
```

### 4. Create New Tenant Configuration
```bash
PUT {{base_url}}/api/tenants/{{test_tenant_id}}/config
Content-Type: application/json

{
  "tenantId": "{{test_tenant_id}}",
  "branding": "{\"logo\": \"https://newcompany.com/logo.png\", \"primaryColor\": \"#28a745\", \"secondaryColor\": \"#17a2b8\"}",
  "appSubscriptions": ["user-management", "application-config"]
}
```

## Response Format

### Success Response
```json
{
  "tenantId": "tenant-001",
  "branding": "{\"logo\": \"https://example.com/logo.png\", \"primaryColor\": \"#007bff\"}",
  "appSubscriptions": ["app1", "app2", "app3"]
}
```

### Default Configuration Response
When a tenant configuration doesn't exist, the service returns:
```json
{
  "tenantId": "tenant-001",
  "branding": "{}",
  "appSubscriptions": []
}
```

## Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully (when default config is created)
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

## Data Models

### TenantConfig Entity
```json
{
  "tenantId": "string",
  "branding": "string (JSON format)",
  "appSubscriptions": ["string"]
}
```

### Branding Configuration Example
```json
{
  "logo": "https://example.com/logo.png",
  "primaryColor": "#007bff",
  "secondaryColor": "#6c757d",
  "companyName": "Example Corp",
  "theme": "light"
}
```

## Validation Rules

### Tenant Configuration
- **Tenant ID**: Required, string identifier
- **Branding**: Optional, JSON string format
- **App Subscriptions**: Optional, array of application IDs

### Branding JSON Structure
- **Logo**: URL to company logo
- **Primary Color**: Hex color code for primary theme
- **Secondary Color**: Hex color code for secondary theme
- **Company Name**: Display name for the tenant
- **Theme**: UI theme preference (light/dark)

## Dependencies

The Tenant Management Service depends on:
- **PostgreSQL Database**: For data persistence
- **Eureka Server**: For service discovery (if enabled)
- **Platform Management Service**: For password policy operations (via Feign client)

## Feign Client Integration

The service includes a Feign client (`PlatformPasswordPolicyClient`) for calling the platform-management-service:

### Available Password Policy Operations
- `GET /api/password-policies` - Get all password policies
- `GET /api/password-policies/{id}` - Get specific password policy
- `GET /api/password-policies/default` - Get default password policy
- `POST /api/password-policies` - Create new password policy
- `PUT /api/password-policies/{id}` - Update password policy
- `DELETE /api/password-policies/{id}` - Delete password policy
- `PUT /api/password-policies/{id}/default` - Set as default policy

## Troubleshooting

### Common Issues

1. **Service Not Reachable**
   - Verify the service is running on port 9086
   - Check if the service is registered with Eureka
   - Use the public test endpoint to verify connectivity

2. **Database Connection Issues**
   - Check PostgreSQL is running
   - Verify database credentials in application.yml
   - Check database URL and port (default: localhost:5432/tenant_management_db)

3. **Configuration Not Found**
   - The service automatically creates default configurations for new tenants
   - Check if the tenant ID is correct
   - Verify the database connection

4. **JSON Parsing Errors**
   - Ensure branding field contains valid JSON
   - Check for proper escaping of quotes in JSON strings
   - Validate JSON structure before sending

### Debug Steps

1. **Check Service Health**
   ```bash
   GET {{base_url}}/actuator/health
   ```

2. **Verify Public Endpoint**
   ```bash
   GET {{base_url}}/public/test
   ```

3. **Check Service Logs**
   - Look for application logs in `/app/logs/tenant-management-service.log`
   - Check for database connection errors
   - Verify Feign client communication with platform service

4. **Database Verification**
   - Connect to PostgreSQL and check tenant_config table
   - Verify tenant_management_db exists and is accessible
   - Check if tenant configurations are being created

## Security Notes

- All endpoints are currently configured as public for testing
- Tenant configurations are stored in PostgreSQL
- Branding information is stored as JSON text
- App subscriptions are stored as a list of application IDs

## Best Practices

### Tenant Configuration Management
1. **Use Descriptive Tenant IDs**: Use meaningful tenant identifiers
2. **Validate Branding JSON**: Ensure branding configuration is valid JSON
3. **Manage App Subscriptions**: Keep app subscription lists updated
4. **Backup Configurations**: Regularly backup tenant configurations

### Branding Configuration
1. **Use HTTPS URLs**: Always use HTTPS for logo URLs
2. **Validate Colors**: Ensure color codes are valid hex values
3. **Keep JSON Clean**: Use proper JSON formatting for branding
4. **Test Configurations**: Verify configurations work in the UI

## Future Enhancements

- Add authentication and authorization
- Implement tenant isolation
- Add tenant configuration validation
- Implement tenant configuration templates
- Add bulk tenant operations
- Implement tenant configuration versioning
- Add audit logging for configuration changes 