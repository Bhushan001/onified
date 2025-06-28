# User Management Service - Postman Collection

This directory contains the complete Postman setup for the User Management Service, including the API collection, environment configurations, and usage documentation.

## Files Overview

- **User_Management_Service_Collection.json** - Complete API collection with all endpoints
- **User_Management_Service_Environment.json** - Environment for direct service calls
- **User_Management_Service_Gateway_Environment.json** - Environment for API gateway calls
- **README.md** - This documentation file

## Service Information

- **Service Name**: user-management-service
- **Default Port**: 9085
- **Base Path**: `/api/users`
- **Public Test Endpoint**: `/public/test`

## Quick Start

1. **Import the Collection**: Import `User_Management_Service_Collection.json` into Postman
2. **Import Environment**: Choose either the direct or gateway environment file
3. **Set Environment**: Select the appropriate environment in Postman
4. **Test Connection**: Use the "Public Test" request to verify connectivity

## Environment Setup

### Direct Service Environment
Use this environment for direct calls to the user-management-service:
- **Base URL**: `http://localhost:9085`
- **Service Port**: 9085

### Gateway Environment
Use this environment for calls through the API gateway:
- **Base URL**: `http://localhost:8080`
- **Gateway Port**: 8080
- **Service Port**: 9085

## API Endpoints

### Public Endpoints

#### GET /public/test
- **Description**: Public endpoint to test service connectivity
- **Authentication**: None required
- **Response**: Simple text response confirming service is reachable

### User Management Endpoints

#### GET /api/users
- **Description**: Retrieve all users in the system
- **Authentication**: None required (configured as permitAll)
- **Response**: List of all users with their details

#### POST /api/users
- **Description**: Create a new user
- **Authentication**: None required
- **Request Body**:
```json
{
  "username": "john.doe",
  "password": "SecurePass123!",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER", "ADMIN"]
}
```
- **Validation**:
  - Username: 3-50 characters, required
  - Password: minimum 8 characters, required
  - Email: valid email format, required
  - First/Last Name: maximum 100 characters each, optional
  - Roles: optional array of role names

#### GET /api/users/{id}
- **Description**: Retrieve a specific user by UUID
- **Authentication**: None required
- **Path Parameter**: `id` (UUID)
- **Response**: User details including roles and attributes

#### GET /api/users/username/{username}
- **Description**: Retrieve a specific user by username
- **Authentication**: None required
- **Path Parameter**: `username` (string)
- **Response**: User details including roles and attributes

#### GET /api/users/auth-details/{username}
- **Description**: Get user authentication details including password hash (used by auth service)
- **Authentication**: None required
- **Path Parameter**: `username` (string)
- **Response**: User auth details with password hash and roles

#### PUT /api/users/{id}
- **Description**: Update an existing user's information
- **Authentication**: None required
- **Path Parameter**: `id` (UUID)
- **Request Body**:
```json
{
  "username": "john.doe.updated",
  "email": "john.doe.updated@example.com",
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "password": "NewSecurePass123!"
}
```
- **Note**: All fields are optional for updates

#### DELETE /api/users/{id}
- **Description**: Delete a user by their UUID
- **Authentication**: None required
- **Path Parameter**: `id` (UUID)
- **Response**: Success confirmation

### Role Management Endpoints

#### POST /api/users/{id}/roles
- **Description**: Assign a role to a specific user
- **Authentication**: None required
- **Path Parameter**: `id` (UUID)
- **Request Body**:
```json
{
  "roleId": "ADMIN"
}
```
- **Note**: Role must exist in the permission registry service

#### DELETE /api/users/{id}/roles/{roleName}
- **Description**: Remove a specific role from a user
- **Authentication**: None required
- **Path Parameters**: 
  - `id` (UUID)
  - `roleName` (string)
- **Response**: Updated user details

### User Attributes Endpoints

#### POST /api/users/{id}/attributes
- **Description**: Add or update a custom attribute for a user
- **Authentication**: None required
- **Path Parameter**: `id` (UUID)
- **Request Body**:
```json
{
  "attributeName": "department",
  "attributeValue": "Engineering"
}
```
- **Validation**: Both attributeName and attributeValue are required

#### DELETE /api/users/{id}/attributes/{attributeName}
- **Description**: Remove a specific attribute from a user
- **Authentication**: None required
- **Path Parameters**:
  - `id` (UUID)
  - `attributeName` (string)
- **Response**: Updated user details

## Environment Variables

### Common Variables
- `base_url`: Service base URL
- `service_name`: Service name (user-management-service)
- `service_port`: Service port (9085)
- `user_id`: UUID for testing user operations
- `username`: Username for testing
- `role_name`: Role name for testing
- `attribute_name`: Attribute name for testing

### Test Data Variables
- `test_user_username`: Test username
- `test_user_email`: Test email
- `test_user_password`: Test password
- `test_user_firstname`: Test first name
- `test_user_lastname`: Test last name

### Gateway-Specific Variables
- `gateway_port`: API gateway port (8080)
- `auth_token`: Authentication token (for future use)

## Usage Examples

### 1. Create a New User
```bash
POST {{base_url}}/api/users
Content-Type: application/json

{
  "username": "{{test_user_username}}",
  "password": "{{test_user_password}}",
  "email": "{{test_user_email}}",
  "firstName": "{{test_user_firstname}}",
  "lastName": "{{test_user_lastname}}",
  "roles": ["USER"]
}
```

### 2. Get User by Username
```bash
GET {{base_url}}/api/users/username/{{username}}
```

### 3. Assign Role to User
```bash
POST {{base_url}}/api/users/{{user_id}}/roles
Content-Type: application/json

{
  "roleId": "{{role_name}}"
}
```

### 4. Add User Attribute
```bash
POST {{base_url}}/api/users/{{user_id}}/attributes
Content-Type: application/json

{
  "attributeName": "{{attribute_name}}",
  "attributeValue": "Engineering"
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
  "message": "Error description"
}
```

## Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists (e.g., duplicate username/email)
- **500 Internal Server Error**: Server error

## Data Models

### User Response
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "status": "ACTIVE|INACTIVE|SUSPENDED|DELETED",
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "roles": ["string"],
  "attributes": [
    {
      "attributeName": "string",
      "attributeValue": "string"
    }
  ]
}
```

### User Auth Details Response
```json
{
  "id": "uuid",
  "username": "string",
  "passwordHash": "string",
  "roles": ["string"]
}
```

## Validation Rules

### User Creation/Update
- **Username**: 3-50 characters, unique
- **Password**: Minimum 8 characters
- **Email**: Valid email format, unique
- **First/Last Name**: Maximum 100 characters each
- **Roles**: Must exist in permission registry service

### User Attributes
- **Attribute Name**: Required, non-blank
- **Attribute Value**: Required, non-blank

## Dependencies

The User Management Service depends on:
- **Permission Registry Service**: For role validation
- **PostgreSQL Database**: For data persistence
- **Eureka Server**: For service discovery (if enabled)

## Troubleshooting

### Common Issues

1. **Service Not Reachable**
   - Verify the service is running on port 9085
   - Check if the service is registered with Eureka
   - Use the public test endpoint to verify connectivity

2. **Role Assignment Fails**
   - Ensure the role exists in the permission registry service
   - Check the permission registry service is running and accessible
   - Verify the role name is correct

3. **Database Connection Issues**
   - Check PostgreSQL is running
   - Verify database credentials in application.yml
   - Check database URL and port

4. **Validation Errors**
   - Ensure all required fields are provided
   - Check field length constraints
   - Verify email format is valid
   - Ensure username/email uniqueness

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
   - Look for application logs in `/app/logs/user-management-service.log`
   - Check for Feign client errors when calling permission registry

4. **Database Verification**
   - Connect to PostgreSQL and check user tables
   - Verify user_management_db exists and is accessible

## Security Notes

- All endpoints are currently configured as `permitAll` for testing
- Passwords are hashed using BCrypt before storage
- User authentication details endpoint is used by the authentication service
- Role validation is performed against the permission registry service

## Future Enhancements

- Add authentication and authorization
- Implement user status management
- Add bulk user operations
- Implement user search and filtering
- Add audit logging for user operations 