# Authentication Service Postman Collection

This folder contains the Postman collection and environments for the Onified Authentication Service.

## Files
- `Authentication_Service_Collection.json` — The Postman collection for authentication APIs
- `Authentication_Service_Environment.json` — Environment for direct microservice calls (`http://localhost:9083`)
- `Authentication_Service_Gateway_Environment.json` — Environment for gateway calls (`http://localhost:9080`)

## Usage

### 1. Import the Collection and Environments
- Open Postman
- Click **Import**
- Import `Authentication_Service_Collection.json`
- Import both environment files

### 2. Select the Environment
- For direct calls to the microservice, select **Authentication Service (Direct)**
- For calls routed through the API gateway, select **Authentication Service (Gateway)**

### 3. Make Requests
- All requests use the `{{baseUrl}}` variable, so switching environments will automatically target the correct backend.
- Example endpoints:
  - Health: `GET {{baseUrl}}/api/authentication/health`
  - Login: `POST {{baseUrl}}/api/authentication/login`
  - Register: `POST {{baseUrl}}/api/authentication/register`
  - Refresh: `POST {{baseUrl}}/api/authentication/refresh?refreshToken={{refreshToken}}`

### 4. Switching Between Direct and Gateway
- To test the service directly, use the **Direct** environment (`baseUrl = http://localhost:9083`)
- To test via the gateway, use the **Gateway** environment (`baseUrl = http://localhost:9080`)

## Example

**Direct:**
```
GET http://localhost:9083/api/authentication/health
```
**Gateway:**
```
GET http://localhost:9080/api/authentication/health
```

## Notes
- Make sure the authentication service and/or gateway are running on the expected ports.
- You can duplicate this structure for other microservices in the `postman/` directory. 