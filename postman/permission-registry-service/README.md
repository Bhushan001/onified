# Permission Registry Service - Postman Collection

This directory contains the complete Postman setup for the Permission Registry Service, including the API collection, environment configurations, and usage documentation.

## Files Overview

- **Permission_Registry_Service_Collection.json** - Complete API collection with all endpoints
- **Permission_Registry_Service_Environment.json** - Environment for direct service calls
- **Permission_Registry_Service_Gateway_Environment.json** - Environment for API gateway calls
- **README.md** - This documentation file

## Service Information

- **Service Name**: permission-registry-service
- **Default Port**: 9084
- **Base Paths**: Multiple paths for different resources
- **Public Test Endpoint**: `/public/test`

## Quick Start

1. **Import the Collection**: Import `Permission_Registry_Service_Collection.json` into Postman
2. **Import Environment**: Choose either the direct or gateway environment file
3. **Set Environment**: Select the appropriate environment in Postman
4. **Test Connection**: Use the "Public Test" request to verify connectivity

## Environment Setup

### Direct Service Environment
Use this environment for direct calls to the permission-registry-service:
- **Base URL**: `http://localhost:9084`
- **Service Port**: 9084

### Gateway Environment
Use this environment for calls through the API gateway:
- **Base URL**: `http://localhost:8080`
- **Gateway Port**: 8080
- **Service Port**: 9084

## API Endpoints

### Public Endpoints

#### GET /public/test
- **Description**: Public endpoint to test service connectivity
- **Authentication**: None required
- **Response**: Simple text response confirming service is reachable

### Actions Management

#### GET /api/actions
- **Description**: Retrieve all actions in the system
- **Authentication**: None required (configured as permitAll)
- **Response**: List of all actions

#### POST /api/actions
- **Description**: Create a new action
- **Authentication**: None required
- **Request Body**:
```json
{
  "actionCode": "CREATE",
  "displayName": "Create Resource",
  "description": "Permission to create new resources",
  "isActive": true
}
```

#### GET /api/actions/{actionCode}
- **Description**: Retrieve a specific action by its code
- **Authentication**: None required
- **Path Parameter**: `actionCode` (string)

#### PUT /api/actions/{actionCode}
- **Description**: Update an existing action
- **Authentication**: None required
- **Path Parameter**: `actionCode` (string)
- **Request Body**: Same as POST

#### DELETE /api/actions/{actionCode}
- **Description**: Delete an action by its code
- **Authentication**: None required
- **Path Parameter**: `actionCode` (string)

### Scopes Management

#### GET /api/scopes
- **Description**: Retrieve all scopes in the system
- **Authentication**: None required

#### POST /api/scopes
- **Description**: Create a new scope
- **Authentication**: None required
- **Request Body**:
```json
{
  "scopeCode": "USER_MANAGEMENT",
  "displayName": "User Management",
  "description": "Scope for user management operations",
  "isActive": true
}
```

#### GET /api/scopes/{scopeCode}
- **Description**: Retrieve a specific scope by its code
- **Authentication**: None required
- **Path Parameter**: `scopeCode` (string)

#### PUT /api/scopes/{scopeCode}
- **Description**: Update an existing scope
- **Authentication**: None required
- **Path Parameter**: `scopeCode` (string)
- **Request Body**: Same as POST

#### DELETE /api/scopes/{scopeCode}
- **Description**: Delete a scope by its code
- **Authentication**: None required
- **Path Parameter**: `scopeCode` (string)

### Roles Management

#### GET /api/roles
- **Description**: Retrieve all roles in the system
- **Authentication**: None required

#### POST /api/roles
- **Description**: Create a new role
- **Authentication**: None required
- **Request Body**:
```json
{
  "roleId": "ADMIN",
  "displayName": "Administrator",
  "appCode": "UMS",
  "moduleCode": "USER_MANAGEMENT",
  "roleFunction": "MANAGE_USERS",
  "isActive": true,
  "inheritanceDepth": 0,
  "tenantCustomizable": true
}
```

#### GET /api/roles/{roleId}
- **Description**: Retrieve a specific role by its ID
- **Authentication**: None required
- **Path Parameter**: `roleId` (string)

#### PUT /api/roles/{roleId}
- **Description**: Update an existing role
- **Authentication**: None required
- **Path Parameter**: `roleId` (string)
- **Request Body**: Same as POST

#### DELETE /api/roles/{roleId}
- **Description**: Delete a role by its ID
- **Authentication**: None required
- **Path Parameter**: `roleId` (string)

### Permission Bundle Units (PBUs)

#### GET /api/pbus
- **Description**: Retrieve all Permission Bundle Units
- **Authentication**: None required

#### POST /api/pbus
- **Description**: Create a new Permission Bundle Unit
- **Authentication**: None required
- **Request Body**:
```json
{
  "pbuId": "USER_CREATE",
  "displayName": "Create User",
  "apiEndpoint": "/api/users",
  "actionCode": "CREATE",
  "scopeCode": "USER_MANAGEMENT",
  "isActive": true,
  "version": "1.0"
}
```

#### GET /api/pbus/{pbuId}
- **Description**: Retrieve a specific PBU by its ID
- **Authentication**: None required
- **Path Parameter**: `pbuId` (string)

#### PUT /api/pbus/{pbuId}
- **Description**: Update an existing PBU
- **Authentication**: None required
- **Path Parameter**: `pbuId` (string)
- **Request Body**: Same as POST

#### DELETE /api/pbus/{pbuId}
- **Description**: Delete a PBU by its ID
- **Authentication**: None required
- **Path Parameter**: `pbuId` (string)

### Role Inheritance Management

#### GET /api/role-inheritance
- **Description**: Retrieve all role inheritance relationships
- **Authentication**: None required

#### POST /api/role-inheritance
- **Description**: Create a new role inheritance relationship
- **Authentication**: None required
- **Request Body**:
```json
{
  "parentRoleId": "ADMIN",
  "childRoleId": "USER_MANAGER",
  "approvedBy": "system"
}
```

#### GET /api/role-inheritance/children/{parentRoleId}
- **Description**: Get all child roles of a specific parent role
- **Authentication**: None required
- **Path Parameter**: `parentRoleId` (string)

#### GET /api/role-inheritance/parents/{childRoleId}
- **Description**: Get all parent roles of a specific child role
- **Authentication**: None required
- **Path Parameter**: `childRoleId` (string)

#### DELETE /api/role-inheritance/{parentRoleId}/{childRoleId}
- **Description**: Delete a role inheritance relationship
- **Authentication**: None required
- **Path Parameters**: `parentRoleId` (string), `childRoleId` (string)

## Environment Variables

### Common Variables
- `base_url`: Service base URL
- `service_name`: Service name (permission-registry-service)
- `service_port`: Service port (9084)
- `action_code`: Action code for testing
- `scope_code`: Scope code for testing
- `role_id`: Role ID for testing
- `pbu_id`: PBU ID for testing
- `parent_role_id`: Parent role ID for inheritance testing
- `child_role_id`: Child role ID for inheritance testing

### Test Data Variables
- `test_action_code`: Test action code
- `test_scope_code`: Test scope code
- `test_role_id`: Test role ID
- `test_pbu_id`: Test PBU ID
- `constraint_id`: Constraint ID for testing
- `behavior_id`: Behavior ID for testing

### Gateway-Specific Variables
- `gateway_port`: API gateway port (8080)
- `auth_token`: Authentication token (for future use)

## Usage Examples

### 1. Create Action and Scope
```bash
# First create a scope
POST {{base_url}}/api/scopes
Content-Type: application/json

{
  "scopeCode": "{{test_scope_code}}",
  "displayName": "Test Scope",
  "description": "Test scope for API testing",
  "isActive": true
}

# Then create an action
POST {{base_url}}/api/actions
Content-Type: application/json

{
  "actionCode": "{{test_action_code}}",
  "displayName": "Test Action",
  "description": "Test action for API testing",
  "isActive": true
}
```

### 2. Create Role
```bash
POST {{base_url}}/api/roles
Content-Type: application/json

{
  "roleId": "{{test_role_id}}",
  "displayName": "Test Role",
  "appCode": "TEST_APP",
  "moduleCode": "TEST_MODULE",
  "roleFunction": "TEST_FUNCTION",
  "isActive": true,
  "inheritanceDepth": 0,
  "tenantCustomizable": true
}
```

### 3. Create PBU
```bash
POST {{base_url}}/api/pbus
Content-Type: application/json

{
  "pbuId": "{{test_pbu_id}}",
  "displayName": "Test PBU",
  "apiEndpoint": "/api/test",
  "actionCode": "{{test_action_code}}",
  "scopeCode": "{{test_scope_code}}",
  "isActive": true,
  "version": "1.0"
}
```

### 4. Create Role Inheritance
```bash
POST {{base_url}}/api/role-inheritance
Content-Type: application/json

{
  "parentRoleId": "{{parent_role_id}}",
  "childRoleId": "{{child_role_id}}",
  "approvedBy": "system"
}
```

## Response Format

All endpoints return responses in the following format:

### Success Response
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "data": {
    // Response data here
  }
}
```

### Error Response
```json
{
  "statusCode": "400",
  "status": "CONFLICT",
  "data": {
    "statusCode": "CONFLICT",
    "message": "Action with this code already exists"
  }
}
```

## Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Resource deleted successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists
- **500 Internal Server Error**: Server error

## Data Models

### Action
```json
{
  "actionCode": "string",
  "displayName": "string",
  "description": "string",
  "isActive": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Scope
```json
{
  "scopeCode": "string",
  "displayName": "string",
  "description": "string",
  "isActive": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Role
```json
{
  "roleId": "string",
  "displayName": "string",
  "appCode": "string",
  "moduleCode": "string",
  "roleFunction": "string",
  "isActive": "boolean",
  "inheritanceDepth": "integer",
  "tenantCustomizable": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Permission Bundle Unit (PBU)
```json
{
  "pbuId": "string",
  "displayName": "string",
  "apiEndpoint": "string",
  "actionCode": "string",
  "scopeCode": "string",
  "isActive": "boolean",
  "version": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Role Inheritance
```json
{
  "parentRoleId": "string",
  "childRoleId": "string",
  "approvedBy": "string",
  "approvalDate": "timestamp"
}
```

## Validation Rules

### Actions
- **Action Code**: Required, unique identifier
- **Display Name**: Required, human-readable name
- **Description**: Optional, detailed description
- **Is Active**: Required, boolean flag

### Scopes
- **Scope Code**: Required, unique identifier
- **Display Name**: Required, human-readable name
- **Description**: Optional, detailed description
- **Is Active**: Required, boolean flag

### Roles
- **Role ID**: Required, unique identifier
- **Display Name**: Required, human-readable name
- **App Code**: Required, application identifier
- **Module Code**: Required, module identifier
- **Role Function**: Required, function description
- **Is Active**: Required, boolean flag
- **Inheritance Depth**: Optional, calculated by service
- **Tenant Customizable**: Required, boolean flag

### PBUs
- **PBU ID**: Required, unique identifier
- **Display Name**: Required, human-readable name
- **API Endpoint**: Required, endpoint path
- **Action Code**: Required, must reference existing action
- **Scope Code**: Required, must reference existing scope
- **Is Active**: Required, boolean flag
- **Version**: Required, version string

## Dependencies

The Permission Registry Service depends on:
- **PostgreSQL Database**: For data persistence
- **Eureka Server**: For service discovery (if enabled)
- **Application Config Service**: For configuration management (via Feign client)

## Additional Controllers (Not in Collection)

The service also includes these controllers that are not included in the main collection:

### Constraints Management
- **General Constraints**: `/api/constraints/general`
- **Field Constraints**: `/api/constraints/field`

### Behaviors Management
- **Contextual Behaviors**: `/api/behaviors/contextual`

### PBU Associations
- **PBU-Constraint Associations**: `/api/pbus/{pbuId}/associations`
- **PBU-Behavior Associations**: `/api/pbus/{pbuId}/associations`

### Role Constraint Overrides
- **Role Constraint Overrides**: `/api/roles/{roleId}/constraint-overrides`

## Troubleshooting

### Common Issues

1. **Service Not Reachable**
   - Verify the service is running on port 9084
   - Check if the service is registered with Eureka
   - Use the public test endpoint to verify connectivity

2. **Database Connection Issues**
   - Check PostgreSQL is running
   - Verify database credentials in application.yml
   - Check database URL and port (default: localhost:5432/permission_registry_db)

3. **Validation Errors**
   - Ensure all required fields are provided
   - Check for unique constraint violations
   - Verify referenced entities exist (actions, scopes)

4. **PBU Creation Fails**
   - Ensure the action code exists and is active
   - Ensure the scope code exists and is active
   - Check PBU ID naming conventions

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
   - Look for application logs in `/app/logs/permission-registry-service.log`
   - Check for database connection errors
   - Verify Feign client communication

4. **Database Verification**
   - Connect to PostgreSQL and check permission tables
   - Verify permission_registry_db exists and is accessible
   - Check if entities are being created properly

## Security Notes

- All endpoints are currently configured as `permitAll` for testing
- Actions, scopes, roles, and PBUs are stored in PostgreSQL
- Role inheritance relationships are managed separately
- PBU associations with constraints and behaviors are supported

## Best Practices

### Permission Management
1. **Use Descriptive Codes**: Use meaningful action and scope codes
2. **Version PBUs**: Always specify version for PBUs
3. **Manage Dependencies**: Ensure actions and scopes exist before creating PBUs
4. **Role Hierarchy**: Plan role inheritance carefully to avoid cycles

### Data Consistency
1. **Validate References**: Always check if referenced entities exist
2. **Use Active Entities**: Only reference active actions and scopes
3. **Maintain Relationships**: Keep role inheritance and PBU associations updated
4. **Audit Changes**: Monitor changes to permission structures

## Future Enhancements

- Add authentication and authorization
- Implement permission caching
- Add bulk operations for permissions
- Implement permission templates
- Add audit logging for permission changes
- Implement permission validation rules
- Add permission analytics and reporting 