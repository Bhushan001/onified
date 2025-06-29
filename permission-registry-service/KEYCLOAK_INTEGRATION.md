# Keycloak Integration for Permission Registry Service

## Overview

The Permission Registry Service now includes automatic synchronization with Keycloak for role management using **FeignClient**. This integration ensures that roles created, updated, or deleted in the permission-registry are automatically reflected in Keycloak for authentication and authorization purposes.

## Architecture

```
Permission Registry Service (Database) ←→ Keycloak
                ↓
        Automatic Sync on CRUD operations
                ↓
        JWT tokens with roles for services
```

## Implementation Details

### **FeignClient Approach**
- **Declarative API**: Clean, readable interface definitions
- **Built-in Error Handling**: Custom error decoder for Keycloak-specific errors
- **Type Safety**: Compile-time checking for API contracts
- **Consistent Architecture**: Matches existing FeignClient usage in the project
- **Automatic Retry**: Configurable retry mechanisms
- **Circuit Breaker Ready**: Easy integration with resilience patterns

### **Components**
- `KeycloakFeignClient` - Declarative interface for Keycloak API calls
- `KeycloakFeignConfig` - Configuration with error handling and logging
- `KeycloakSyncService` - Business logic for role synchronization
- `RoleService` - Enhanced with automatic sync on CRUD operations

## Features

### ✅ Automatic Synchronization
- **Create Role**: Automatically creates role in Keycloak when created in permission-registry
- **Update Role**: Automatically updates role in Keycloak when modified in permission-registry
- **Delete Role**: Automatically removes role from Keycloak when deleted from permission-registry

### ✅ Manual Sync
- **Bulk Sync**: Sync all existing roles to Keycloak via API endpoint
- **Recovery**: Re-sync roles if Keycloak data is lost or corrupted

### ✅ Error Handling
- **Non-blocking**: Keycloak sync failures don't prevent database operations
- **Detailed Error Decoder**: Specific error messages for different HTTP status codes
- **Graceful degradation**: Service continues to work even if Keycloak is unavailable
- **Logging**: Comprehensive logging for troubleshooting

## Configuration

### Environment Variables

Add these to your `.env` file or environment:

```bash
# Keycloak Configuration
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8080
KEYCLOAK_REALM=master
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
```

### Default Values

If not specified, the service uses these defaults:
- **Keycloak URL**: `http://localhost:8080`
- **Realm**: `master`
- **Admin Username**: `admin`
- **Admin Password**: `admin`
- **Admin Client ID**: `admin-cli`

### Feign Configuration

The service includes optimized Feign configuration:

```yaml
feign:
  client:
    config:
      keycloak-client:
        connect-timeout: 5000
        read-timeout: 10000
        logger-level: BASIC
        error-decoder: com.onified.ai.permission_registry.client.KeycloakFeignConfig$KeycloakErrorDecoder
```

## API Endpoints

### Role Management (Auto-sync)

#### Create Role
```http
POST /api/roles
Content-Type: application/json

{
  "roleId": "UMS.USER_MANAGEMENT.ADMIN",
  "displayName": "User Management Administrator",
  "appCode": "UMS",
  "moduleCode": "USER_MANAGEMENT",
  "roleFunction": "ADMIN",
  "isActive": true,
  "tenantCustomizable": false
}
```

#### Update Role
```http
PUT /api/roles/{roleId}
Content-Type: application/json

{
  "roleId": "UMS.USER_MANAGEMENT.ADMIN",
  "displayName": "Updated User Management Administrator",
  "appCode": "UMS",
  "moduleCode": "USER_MANAGEMENT",
  "roleFunction": "ADMIN",
  "isActive": true,
  "tenantCustomizable": true
}
```

#### Delete Role
```http
DELETE /api/roles/{roleId}
```

### Manual Sync

#### Sync All Roles to Keycloak
```http
POST /api/roles/sync-to-keycloak
```

**Use Cases:**
- Initial setup after Keycloak deployment
- Recovery after Keycloak data loss
- Ensuring consistency between systems

## Testing

### Postman Collection

Use the provided Postman collection: `postman/permission-registry-service/Keycloak_Sync_Collection.json`

### Test Scenarios

1. **Create Role Test**
   ```bash
   # Create a role
   curl -X POST http://localhost:9084/api/roles \
     -H "Content-Type: application/json" \
     -d '{
       "roleId": "TEST.APP.USER",
       "displayName": "Test User Role",
       "appCode": "TEST",
       "moduleCode": "APP",
       "roleFunction": "USER",
       "isActive": true,
       "tenantCustomizable": false
     }'
   
   # Verify in Keycloak admin console
   # Check: http://localhost:8080/auth/admin/master/console/#/realms/master/roles
   ```

2. **Update Role Test**
   ```bash
   # Update the role
   curl -X PUT http://localhost:9084/api/roles/TEST.APP.USER \
     -H "Content-Type: application/json" \
     -d '{
       "roleId": "TEST.APP.USER",
       "displayName": "Updated Test User Role",
       "appCode": "TEST",
       "moduleCode": "APP",
       "roleFunction": "USER",
       "isActive": true,
       "tenantCustomizable": true
     }'
   ```

3. **Manual Sync Test**
   ```bash
   # Sync all roles
   curl -X POST http://localhost:9084/api/roles/sync-to-keycloak
   ```

## Troubleshooting

### Common Issues

#### 1. Keycloak Connection Failed
```
Error: Failed to get Keycloak admin token
```
**Solution:**
- Verify Keycloak is running on the configured URL
- Check admin credentials in configuration
- Ensure admin-cli client exists in Keycloak

#### 2. Role Sync Failed
```
Warning: Failed to sync role to Keycloak, but role was saved to database
```
**Solution:**
- Check Keycloak logs for detailed error
- Verify role naming conventions
- Ensure admin user has sufficient permissions

#### 3. Role Already Exists in Keycloak
```
Error: Role already exists
```
**Solution:**
- Use manual sync endpoint to re-sync all roles
- Check for duplicate role names
- Clean up Keycloak roles manually if needed

### Error Decoder

The FeignClient includes a custom error decoder that provides specific error messages:

- **400**: Bad request to Keycloak
- **401**: Unauthorized access to Keycloak
- **403**: Forbidden access to Keycloak
- **404**: Resource not found in Keycloak
- **409**: Conflict in Keycloak (e.g., role already exists)
- **500**: Internal server error in Keycloak

### Logs

Monitor these log patterns:

```bash
# Success logs
"Successfully synced role 'ROLE_NAME' to Keycloak"
"Successfully updated role 'ROLE_NAME' in Keycloak"
"Successfully deleted role 'ROLE_NAME' from Keycloak"

# Error logs
"Failed to sync role 'ROLE_NAME' to Keycloak: [error details]"
"Failed to get Keycloak admin token: [error details]"
```

### Keycloak Admin Console

Access Keycloak admin console to verify roles:
```
URL: http://localhost:8080/auth/admin/master/console/#/realms/master/roles
Username: admin
Password: admin
```

## Security Considerations

### ✅ Secure Configuration
- Use environment variables for sensitive data
- Don't commit credentials to version control
- Use strong admin passwords in production

### ✅ Network Security
- Ensure Keycloak is accessible from permission-registry service
- Use HTTPS in production environments
- Consider network segmentation

### ✅ Error Handling
- Sync failures don't expose sensitive information
- Detailed logging for debugging (avoid in production)
- Graceful degradation when Keycloak is unavailable

## Production Deployment

### 1. Environment Setup
```bash
# Production environment variables
KEYCLOAK_AUTH_SERVER_URL=https://keycloak.yourdomain.com
KEYCLOAK_REALM=your-realm
KEYCLOAK_ADMIN_USERNAME=your-admin-username
KEYCLOAK_ADMIN_PASSWORD=your-secure-password
KEYCLOAK_ADMIN_CLIENT_ID=admin-cli
```

### 2. Health Checks
Monitor the sync status:
```bash
# Check service health
curl http://localhost:9084/actuator/health

# Check role sync endpoint
curl -X POST http://localhost:9084/api/roles/sync-to-keycloak
```

### 3. Monitoring
- Monitor sync success/failure rates
- Set up alerts for sync failures
- Track role creation/update/deletion metrics

## Advantages of FeignClient over RestTemplate

### ✅ **FeignClient Benefits:**
- **Declarative**: Clean interface definitions
- **Type Safety**: Compile-time checking
- **Built-in Error Handling**: Custom error decoders
- **Retry Support**: Automatic retry mechanisms
- **Circuit Breaker Ready**: Easy resilience integration
- **Less Boilerplate**: More concise code
- **Consistent**: Matches existing project architecture

### ❌ **RestTemplate Disadvantages:**
- **Imperative**: More verbose code
- **Manual Error Handling**: More error-prone
- **No Built-in Retry**: Manual retry implementation needed
- **Runtime Errors**: More prone to runtime issues

## Future Enhancements

### Planned Features
- [ ] Batch sync with progress tracking
- [ ] Role hierarchy sync (parent-child relationships)
- [ ] Client-specific role sync
- [ ] Sync status dashboard
- [ ] Retry mechanism for failed syncs
- [ ] Webhook notifications for sync events

### Integration Points
- [ ] User Management Service role assignment sync
- [ ] Authentication Service JWT validation
- [ ] Gateway Service role-based routing 