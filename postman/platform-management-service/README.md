# Platform Management Service Postman Collection

This folder contains the Postman collection and environments for the Onified Platform Management Service.

## Files
- `Platform_Management_Service_Collection.json` — The Postman collection for platform management APIs
- `Platform_Management_Service_Environment.json` — Environment for direct microservice calls (`http://localhost:9081`)
- `Platform_Management_Service_Gateway_Environment.json` — Environment for gateway calls (`http://localhost:9080`)
- `README.md` — This documentation file

## Usage

### 1. Import the Collection and Environments
- Open Postman
- Click **Import**
- Import `Platform_Management_Service_Collection.json`
- Import both environment files

### 2. Select the Environment
- For direct calls to the microservice, select **Platform Management Service (Direct)**
- For calls routed through the API gateway, select **Platform Management Service (Gateway)**

### 3. Make Requests
- All requests use the `{{baseUrl}}` variable, so switching environments will automatically target the correct backend.
- Use `{{policyId}}`, `{{policyName}}`, and `{{tenantId}}` variables for dynamic values in requests.

## Available Endpoints

### Public Test
- **GET** `{{baseUrl}}/public/test` - Service connectivity test

### Password Policies
- **GET** `{{baseUrl}}/api/password-policies` - Get all password policies
- **GET** `{{baseUrl}}/api/password-policies/{{policyId}}` - Get password policy by ID
- **GET** `{{baseUrl}}/api/password-policies/name/{{policyName}}` - Get password policy by name
- **GET** `{{baseUrl}}/api/password-policies/default` - Get default password policy
- **POST** `{{baseUrl}}/api/password-policies` - Create a new password policy
- **PUT** `{{baseUrl}}/api/password-policies/{{policyId}}` - Update password policy
- **PUT** `{{baseUrl}}/api/password-policies/{{policyId}}/default` - Set policy as default
- **GET** `{{baseUrl}}/api/password-policies/default/ensure` - Ensure default policy exists
- **DELETE** `{{baseUrl}}/api/password-policies/{{policyId}}` - Delete password policy
- **GET** `{{baseUrl}}/api/password-policy/platform` - Get platform password policy

### Tenants
- **GET** `{{baseUrl}}/api/tenants` - Get all tenants
- **GET** `{{baseUrl}}/api/tenants/{{tenantId}}` - Get tenant by ID
- **POST** `{{baseUrl}}/api/tenants` - Create a new tenant
- **PUT** `{{baseUrl}}/api/tenants/{{tenantId}}` - Update tenant
- **DELETE** `{{baseUrl}}/api/tenants/{{tenantId}}` - Delete tenant

## Request/Response Examples

### Create Password Policy
```json
POST {{baseUrl}}/api/password-policies
Content-Type: application/json

{
  "policyName": "Strict Policy",
  "description": "Strict password policy for sensitive applications",
  "minLength": 12,
  "maxPasswordAge": 60,
  "minPasswordAge": 1,
  "passwordHistory": 6,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "[Random]",
  "bannedPatterns": "password,123456,qwerty,admin,root",
  "isActive": true,
  "isDefault": false
}
```

**Response:**
```json
{
  "id": 2,
  "policyName": "Strict Policy",
  "description": "Strict password policy for sensitive applications",
  "minLength": 12,
  "maxPasswordAge": 60,
  "minPasswordAge": 1,
  "passwordHistory": 6,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "initialPasswordFormat": "[Random]",
  "bannedPatterns": "password,123456,qwerty,admin,root",
  "isActive": true,
  "isDefault": false
}
```

### Create Tenant
```json
POST {{baseUrl}}/api/tenants
Content-Type: application/json

{
  "tenantId": "tenant2",
  "name": "Tenant Two",
  "status": "ACTIVE",
  "extraConfig": "{\"theme\": \"dark\"}"
}
```

**Response:**
```json
{
  "statusCode": 201,
  "status": "SUCCESS",
  "data": {
    "tenantId": "tenant2",
    "name": "Tenant Two",
    "status": "ACTIVE",
    "extraConfig": "{\"theme\": \"dark\"}",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00",
    "createdBy": "system",
    "updatedBy": "system"
  }
}
```

## Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `baseUrl` | Base URL for the service | `http://localhost:9081` (Direct) / `http://localhost:9080` (Gateway) |
| `policyId` | Password policy ID for testing | `1` |
| `policyName` | Password policy name for testing | `Default Policy` |
| `tenantId` | Tenant ID for testing | `tenant1` |

## Testing Workflow

### 1. Test Service Connectivity
```bash
GET {{baseUrl}}/public/test
```

### 2. Password Policy Management
```bash
# Get all policies
GET {{baseUrl}}/api/password-policies

# Get default policy
GET {{baseUrl}}/api/password-policies/default

# Create new policy
POST {{baseUrl}}/api/password-policies
{
  "policyName": "Strict Policy",
  "description": "Strict password policy",
  "minLength": 12,
  "maxPasswordAge": 60,
  "requireUppercase": true,
  "requireLowercase": true,
  "requireNumber": true,
  "requireSpecial": true,
  "isActive": true,
  "isDefault": false
}

# Set as default
PUT {{baseUrl}}/api/password-policies/2/default

# Get platform policy
GET {{baseUrl}}/api/password-policy/platform
```

### 3. Tenant Management
```bash
# Get all tenants
GET {{baseUrl}}/api/tenants

# Create new tenant
POST {{baseUrl}}/api/tenants
{
  "tenantId": "tenant2",
  "name": "Tenant Two",
  "status": "ACTIVE",
  "extraConfig": "{\"theme\": \"dark\"}"
}

# Update tenant
PUT {{baseUrl}}/api/tenants/tenant1
{
  "tenantId": "tenant1",
  "name": "Updated Tenant One",
  "status": "ACTIVE",
  "extraConfig": "{\"theme\": \"light\", \"features\": [\"advanced\"]}"
}
```

## Password Policy Fields

| Field | Type | Description | Default |
|-------|------|-------------|---------|
| `policyName` | String | Unique policy name | Required |
| `description` | String | Policy description | Optional |
| `minLength` | Integer | Minimum password length | 10 |
| `maxPasswordAge` | Integer | Maximum password age in days | 90 |
| `minPasswordAge` | Integer | Minimum password age in days | 0 |
| `passwordHistory` | Integer | Number of previous passwords to remember | 4 |
| `requireUppercase` | Boolean | Require uppercase letters | true |
| `requireLowercase` | Boolean | Require lowercase letters | true |
| `requireNumber` | Boolean | Require numbers | true |
| `requireSpecial` | Boolean | Require special characters | true |
| `initialPasswordFormat` | String | Format for initial passwords | "[FirstInitial][LastInitial][Random]" |
| `bannedPatterns` | String | Comma-separated banned patterns | "password,123456,qwerty,admin" |
| `isActive` | Boolean | Whether policy is active | true |
| `isDefault` | Boolean | Whether policy is default | false |

## Tenant Fields

| Field | Type | Description |
|-------|------|-------------|
| `tenantId` | String | Unique tenant identifier |
| `name` | String | Tenant display name |
| `status` | String | Tenant status (ACTIVE, INACTIVE, etc.) |
| `extraConfig` | String | JSON string for additional configuration |
| `createdAt` | DateTime | Creation timestamp |
| `updatedAt` | DateTime | Last update timestamp |
| `createdBy` | String | User who created the tenant |
| `updatedBy` | String | User who last updated the tenant |

## Switching Between Direct and Gateway

- **Direct Service Call**: Use the **Direct** environment (`baseUrl = http://localhost:9081`)
- **Gateway Call**: Use the **Gateway** environment (`baseUrl = http://localhost:9080`)

## Example URLs

**Direct:**
```
GET http://localhost:9081/api/password-policies
POST http://localhost:9081/api/tenants
```

**Gateway:**
```
GET http://localhost:9080/api/password-policies
POST http://localhost:9080/api/tenants
```

## Notes
- Make sure the platform-management-service and/or gateway are running on the expected ports.
- The service uses PostgreSQL database for data persistence.
- Password policies support complex validation rules and can be set as default.
- Only one password policy can be default at a time.
- Tenants support custom configuration via the `extraConfig` field.
- All responses follow a standardized format with `statusCode`, `status`, and `data` fields.

## Troubleshooting

### Service Not Accessible
1. Verify the service is running: `docker-compose ps platform-management-service`
2. Check the correct port: `9081` for direct, `9080` for gateway
3. Verify database connectivity
4. Check service logs: `docker-compose logs platform-management-service`

### Database Issues
1. Verify PostgreSQL is running and accessible
2. Check database connection settings in `application.yml`
3. Verify database schema is created
4. Check database logs for connection errors

### Password Policy Issues
1. Ensure `policyName` is unique across the system
2. Check that only one policy is set as default
3. Verify password policy validation rules
4. Check for constraint violations

### Tenant Issues
1. Ensure `tenantId` is unique across the system
2. Verify `extraConfig` is valid JSON
3. Check tenant status values
4. Verify required fields are provided 