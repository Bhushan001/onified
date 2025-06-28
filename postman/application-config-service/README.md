# Application Config Service Postman Collection

This folder contains the Postman collection and environments for the Onified Application Config Service.

## Files
- `Application_Config_Service_Collection.json` — The Postman collection for application config APIs
- `Application_Config_Service_Environment.json` — Environment for direct microservice calls (`http://localhost:9082`)
- `Application_Config_Service_Gateway_Environment.json` — Environment for gateway calls (`http://localhost:9080`)
- `README.md` — This documentation file

## Usage

### 1. Import the Collection and Environments
- Open Postman
- Click **Import**
- Import `Application_Config_Service_Collection.json`
- Import both environment files

### 2. Select the Environment
- For direct calls to the microservice, select **Application Config Service (Direct)**
- For calls routed through the API gateway, select **Application Config Service (Gateway)**

### 3. Make Requests
- All requests use the `{{baseUrl}}` variable, so switching environments will automatically target the correct backend.
- Use `{{appCode}}` and `{{moduleId}}` variables for dynamic values in requests.

## Available Endpoints

### Public Test
- **GET** `{{baseUrl}}/public/test` - Service connectivity test

### Applications
- **POST** `{{baseUrl}}/api/applications` - Create a new application
- **GET** `{{baseUrl}}/api/applications/{{appCode}}` - Get application by app code
- **GET** `{{baseUrl}}/api/applications` - Get all applications
- **PUT** `{{baseUrl}}/api/applications/{{appCode}}` - Update application
- **DELETE** `{{baseUrl}}/api/applications/{{appCode}}` - Delete application

### Modules
- **POST** `{{baseUrl}}/api/modules` - Create a new module
- **GET** `{{baseUrl}}/api/modules/{{moduleId}}` - Get module by ID
- **GET** `{{baseUrl}}/api/modules/by-app/{{appCode}}` - Get modules by app code
- **PUT** `{{baseUrl}}/api/modules/{{moduleId}}` - Update module
- **DELETE** `{{baseUrl}}/api/modules/{{moduleId}}` - Delete module

## Request/Response Examples

### Create Application
```json
POST {{baseUrl}}/api/applications
Content-Type: application/json

{
  "appCode": "TEST_APP",
  "displayName": "Test Application",
  "isActive": true
}
```

**Response:**
```json
{
  "statusCode": 201,
  "status": "SUCCESS",
  "data": {
    "appCode": "TEST_APP",
    "displayName": "Test Application",
    "isActive": true
  }
}
```

### Create Module
```json
POST {{baseUrl}}/api/modules
Content-Type: application/json

{
  "appCode": "TEST_APP",
  "moduleCode": "USER_MODULE",
  "isActive": true
}
```

**Response:**
```json
{
  "statusCode": 201,
  "status": "SUCCESS",
  "data": {
    "moduleId": 1,
    "appCode": "TEST_APP",
    "moduleCode": "USER_MODULE",
    "isActive": true
  }
}
```

## Environment Variables

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `baseUrl` | Base URL for the service | `http://localhost:9082` (Direct) / `http://localhost:9080` (Gateway) |
| `appCode` | Application code for testing | `TEST_APP` |
| `moduleId` | Module ID for testing | `1` |

## Testing Workflow

### 1. Test Service Connectivity
```bash
GET {{baseUrl}}/public/test
```

### 2. Create Test Application
```bash
POST {{baseUrl}}/api/applications
{
  "appCode": "TEST_APP",
  "displayName": "Test Application",
  "isActive": true
}
```

### 3. Create Test Module
```bash
POST {{baseUrl}}/api/modules
{
  "appCode": "TEST_APP",
  "moduleCode": "USER_MODULE",
  "isActive": true
}
```

### 4. Retrieve and Update
- Get the created application: `GET {{baseUrl}}/api/applications/TEST_APP`
- Get modules for the app: `GET {{baseUrl}}/api/modules/by-app/TEST_APP`
- Update the application or module as needed

### 5. Cleanup (Optional)
- Delete the module: `DELETE {{baseUrl}}/api/modules/1`
- Delete the application: `DELETE {{baseUrl}}/api/applications/TEST_APP`

## Switching Between Direct and Gateway

- **Direct Service Call**: Use the **Direct** environment (`baseUrl = http://localhost:9082`)
- **Gateway Call**: Use the **Gateway** environment (`baseUrl = http://localhost:9080`)

## Example URLs

**Direct:**
```
GET http://localhost:9082/api/applications
POST http://localhost:9082/api/modules
```

**Gateway:**
```
GET http://localhost:9080/api/applications
POST http://localhost:9080/api/modules
```

## Notes
- Make sure the application-config-service and/or gateway are running on the expected ports.
- The service uses PostgreSQL database for data persistence.
- All responses follow a standardized format with `statusCode`, `status`, and `data` fields.
- Application codes should be unique across the system.
- Module IDs are auto-generated integers.
- The `isActive` field controls whether applications/modules are active in the system.

## Troubleshooting

### Service Not Accessible
1. Verify the service is running: `docker-compose ps application-config-service`
2. Check the correct port: `9082` for direct, `9080` for gateway
3. Verify database connectivity
4. Check service logs: `docker-compose logs application-config-service`

### Database Issues
1. Verify PostgreSQL is running and accessible
2. Check database connection settings in `application.yml`
3. Verify database schema is created
4. Check database logs for connection errors

### Validation Errors
1. Ensure required fields are provided (`appCode`, `displayName`, `isActive`)
2. Check for unique constraint violations (duplicate `appCode`)
3. Verify data types (boolean for `isActive`)
4. Check request format and Content-Type header 