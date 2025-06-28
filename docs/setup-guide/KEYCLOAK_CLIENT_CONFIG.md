# Keycloak Client Configuration Guide

## Overview
This guide provides detailed configuration settings for Keycloak clients used in the Onified platform.

## Client 1: Authentication Service Client

### Client ID: `onified-auth-service`

#### General Settings
```
Client ID: onified-auth-service
Name: Onified Authentication Service
Description: Backend authentication service for Onified platform
Client Protocol: openid-connect
Root URL: http://localhost:9083
```

#### Capability Config
```
Client authentication: ON
Authorization: OFF
Standard flow: ON
Direct access grants: ON
Service accounts roles: ON
OIDC CIBA: OFF
OAuth 2.0 Device Authorization Grant: OFF
OAuth 2.0 Dynamic Client Registration: OFF
```

#### Login Settings
```
Valid redirect URIs: 
- http://localhost:9083/*
- http://localhost:9083/api/auth/*
- http://localhost:9083/actuator/*

Web Origins: 
- http://localhost:9083
- http://localhost:9080

Admin URL: http://localhost:9083
Base URL: http://localhost:9083
Master SAML Processing URL: (leave empty)
```

#### Advanced Settings
```
Access Type: confidential
Standard Flow Enabled: ON
Implicit Flow Enabled: OFF
Direct Access Grants Enabled: ON
Service Accounts Enabled: ON
OIDC CIBA Grant Enabled: OFF
OAuth 2.0 Device Authorization Grant Enabled: OFF
OIDC Dynamic Client Registration Enabled: OFF
Backchannel Logout: OFF
Backchannel Logout URL: (leave empty)
Backchannel Logout Session Required: OFF
Logout URL: (leave empty)
Logout URL +: (leave empty)
```

#### Credentials Tab
After saving the client, go to the **Credentials** tab and copy the **Client Secret**. Update this in your `docker-compose.yml`:

```yaml
authentication-service:
  environment:
    KEYCLOAK_CLIENT_SECRET: your-actual-client-secret-here
```

## Client 2: Web Application Client

### Client ID: `onified-web-app`

#### General Settings
```
Client ID: onified-web-app
Name: Onified Web Application
Description: Frontend Angular application for Onified platform
Client Protocol: openid-connect
Root URL: http://localhost:4200
```

#### Capability Config
```
Client authentication: OFF
Authorization: OFF
Standard flow: ON
Direct access grants: OFF
Service accounts roles: OFF
OIDC CIBA: OFF
OAuth 2.0 Device Authorization Grant: OFF
OAuth 2.0 Dynamic Client Registration: OFF
```

#### Login Settings
```
Valid redirect URIs: 
- http://localhost:4200/*
- http://localhost:4200
- http://localhost:4200/dashboard
- http://localhost:4200/login
- http://localhost:4200/callback

Web Origins: 
- http://localhost:4200
- http://localhost:4200/*

Admin URL: http://localhost:4200
Base URL: http://localhost:4200
Master SAML Processing URL: (leave empty)
```

#### Advanced Settings
```
Access Type: public
Standard Flow Enabled: ON
Implicit Flow Enabled: OFF
Direct Access Grants Enabled: OFF
Service Accounts Enabled: OFF
OIDC CIBA Grant Enabled: OFF
OAuth 2.0 Device Authorization Grant Enabled: OFF
OIDC Dynamic Client Registration Enabled: OFF
Backchannel Logout: OFF
Backchannel Logout URL: (leave empty)
Backchannel Logout Session Required: OFF
Logout URL: http://localhost:4200/logout
Logout URL +: (leave empty)
```

## Step-by-Step Configuration

### Step 1: Create Authentication Service Client

1. **Navigate to Clients**
   - Go to Keycloak Admin Console
   - Select the `onified` realm
   - Click "Clients" in the left sidebar
   - Click "Create" button

2. **General Settings**
   ```
   Client ID: onified-auth-service
   Name: Onified Authentication Service
   Description: Backend authentication service for Onified platform
   Client Protocol: openid-connect
   Root URL: http://localhost:9083
   ```

3. **Click "Save"**

4. **Settings Tab Configuration**
   - **Access Type**: Select "confidential"
   - **Valid Redirect URIs**: Add the redirect URIs listed above
   - **Web Origins**: Add the web origins listed above
   - **Admin URL**: `http://localhost:9083`
   - **Base URL**: `http://localhost:9083`

5. **Capability Config Tab**
   - Enable "Client authentication"
   - Enable "Standard flow"
   - Enable "Direct access grants"
   - Enable "Service accounts roles"

6. **Click "Save"**

7. **Credentials Tab**
   - Copy the generated Client Secret
   - Update your `docker-compose.yml` with this secret

### Step 2: Create Web Application Client

1. **Navigate to Clients**
   - Click "Create" button again

2. **General Settings**
   ```
   Client ID: onified-web-app
   Name: Onified Web Application
   Description: Frontend Angular application for Onified platform
   Client Protocol: openid-connect
   Root URL: http://localhost:4200
   ```

3. **Click "Save"**

4. **Settings Tab Configuration**
   - **Access Type**: Select "public"
   - **Valid Redirect URIs**: Add the redirect URIs listed above
   - **Web Origins**: Add the web origins listed above
   - **Admin URL**: `http://localhost:4200`
   - **Base URL**: `http://localhost:4200`
   - **Logout URL**: `http://localhost:4200/logout`

5. **Capability Config Tab**
   - Enable "Standard flow"
   - Keep other options disabled

6. **Click "Save"**

## Environment Configuration

### Update docker-compose.yml
```yaml
authentication-service:
  environment:
    KEYCLOAK_AUTH_SERVER_URL: http://keycloak:8080
    KEYCLOAK_REALM: onified
    KEYCLOAK_CLIENT_ID: onified-auth-service
    KEYCLOAK_CLIENT_SECRET: your-actual-client-secret-here
```

### Update Angular Environment
```typescript
// web/src/environments/environment.ts
export const environment = {
  // ... other settings
  keycloak: {
    issuer: 'http://localhost:9090/realms/onified',
    clientId: 'onified-web-app',
    redirectUri: 'http://localhost:4200',
    postLogoutRedirectUri: 'http://localhost:4200',
    scope: 'openid profile email',
    responseType: 'code',
    showDebugInformation: true
  }
};
```

## Testing the Configuration

### Test Authentication Service Client
```bash
# Test client credentials flow
curl -X POST http://localhost:9090/realms/onified/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=onified-auth-service" \
  -d "client_secret=your-client-secret"
```

### Test Web Application Client
```bash
# Test authorization code flow (browser)
# Navigate to: http://localhost:9090/realms/onified/protocol/openid-connect/auth?client_id=onified-web-app&response_type=code&scope=openid&redirect_uri=http://localhost:4200
```

## Common Issues and Solutions

### 1. CORS Errors
**Problem**: Browser blocks requests due to CORS policy
**Solution**: Ensure Web Origins are correctly configured:
```
http://localhost:4200
http://localhost:4200/*
```

### 2. Invalid Redirect URI
**Problem**: "Invalid redirect URI" error during login
**Solution**: Add exact redirect URIs to Valid Redirect URIs:
```
http://localhost:4200
http://localhost:4200/*
http://localhost:4200/callback
```

### 3. Client Secret Issues
**Problem**: Authentication fails with invalid client secret
**Solution**: 
- Regenerate client secret in Credentials tab
- Update environment variables
- Restart authentication service

### 4. Access Type Mismatch
**Problem**: Service can't authenticate with Keycloak
**Solution**: 
- Authentication service: Use "confidential" access type
- Web application: Use "public" access type

## Security Best Practices

### 1. Client Secrets
- Store client secrets securely (environment variables)
- Never commit secrets to version control
- Rotate secrets regularly

### 2. Redirect URIs
- Use exact URIs, avoid wildcards in production
- Use HTTPS in production
- Validate redirect URIs on your application side

### 3. Web Origins
- Restrict to specific domains
- Avoid using `*` in production
- Use HTTPS in production

### 4. Access Types
- Use "confidential" for server-to-server communication
- Use "public" for browser-based applications
- Never use "bearer-only" for interactive applications

## Production Configuration

### Update URLs for Production
```
Root URL: https://your-domain.com
Valid Redirect URIs: https://your-domain.com/*
Web Origins: https://your-domain.com
Admin URL: https://your-domain.com
```

### Environment Variables
```yaml
# Production docker-compose.yml
KEYCLOAK_AUTH_SERVER_URL: https://keycloak.your-domain.com
KEYCLOAK_REALM: onified
KEYCLOAK_CLIENT_ID: onified-auth-service
KEYCLOAK_CLIENT_SECRET: your-production-client-secret
```

### Angular Production Environment
```typescript
// web/src/environments/environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.your-domain.com/api',
  keycloak: {
    issuer: 'https://keycloak.your-domain.com/realms/onified',
    clientId: 'onified-web-app',
    redirectUri: 'https://your-domain.com',
    postLogoutRedirectUri: 'https://your-domain.com',
    scope: 'openid profile email',
    responseType: 'code',
    showDebugInformation: false
  }
};
``` 