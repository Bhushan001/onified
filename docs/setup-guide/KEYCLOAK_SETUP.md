# Keycloak Setup Guide for Onified Platform

## Overview
This guide explains how to set up Keycloak as the identity provider for the Onified platform, integrating with the authentication service.

## Prerequisites
- Docker and Docker Compose installed
- Java 21
- Maven

## Quick Start

### 1. Start Keycloak with Docker Compose
```bash
docker-compose up -d keycloak keycloak-db
```

### 2. Access Keycloak Admin Console
- URL: http://localhost:9090
- Username: `admin`
- Password: `admin123`

## Keycloak Configuration

### 1. Create Realm
1. Log into Keycloak Admin Console
2. Click "Create Realm" in the top-left dropdown
3. Enter realm name: `onified`
4. Click "Create"

### 2. Create Client for Authentication Service
1. In the `onified` realm, go to "Clients" → "Create"
2. Configure the client:
   - **Client ID**: `onified-auth-service`
   - **Client Protocol**: `openid-connect`
   - **Root URL**: `http://localhost:9081`

3. In the "Settings" tab:
   - **Access Type**: `confidential`
   - **Valid Redirect URIs**: `http://localhost:9081/*`
   - **Web Origins**: `http://localhost:9081`
   - **Admin URL**: `http://localhost:9081`

4. Save the client

5. Go to the "Credentials" tab and copy the client secret

### 3. Create Client for Web Application
1. Create another client:
   - **Client ID**: `onified-auth-service`
   - **Client Protocol**: `openid-connect`
   - **Root URL**: `http://localhost:4200`

2. In the "Settings" tab:
   - **Access Type**: `public`
   - **Valid Redirect URIs**: `http://localhost:4200/*`
   - **Web Origins**: `http://localhost:4200`

### 4. Create Users
1. Go to "Users" → "Add User"
2. Create test users:
   - **Username**: `admin`
   - **Email**: `admin@onified.com`
   - **First Name**: `Admin`
   - **Last Name**: `User`
   - **Email Verified**: `ON`

3. Set password:
   - Go to "Credentials" tab
   - Set password: `admin123`
   - **Temporary**: `OFF`

4. Create additional users as needed

### 5. Create Roles
1. Go to "Roles" → "Add Role"
2. Create roles:
   - `ADMIN`
   - `USER`
   - `MANAGER`

### 6. Assign Roles to Users
1. Go to "Users" → Select user → "Role Mappings"
2. Assign appropriate roles to users

## Environment Configuration

### Update Authentication Service Environment
Update the authentication service environment variables in `docker-compose.yml`:

```yaml
authentication-service:
  environment:
    KEYCLOAK_AUTH_SERVER_URL: http://keycloak:8080
    KEYCLOAK_REALM: onified
    KEYCLOAK_CLIENT_ID: onified-auth-service
    KEYCLOAK_CLIENT_SECRET: your-actual-client-secret
```

### Update Application Configuration
Update `authentication-service/src/main/resources/application.yml`:

```yaml
keycloak:
  auth-server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://localhost:9090}
  realm: ${KEYCLOAK_REALM:onified}
  client-id: ${KEYCLOAK_CLIENT_ID:onified-auth-service}
  client-secret: ${KEYCLOAK_CLIENT_SECRET:your-client-secret}
  admin:
    username: admin
    password: admin123
    realm: master
```

## Testing the Integration

### 1. Start All Services
```bash
docker-compose up -d
```

### 2. Test Authentication
```bash
# Login with Keycloak user
curl -X POST http://localhost:9081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 3. Test Token Refresh
```bash
# Use the refresh token from login response
curl -X POST "http://localhost:9081/api/auth/refresh?refreshToken=YOUR_REFRESH_TOKEN"
```

## Security Considerations

### 1. Production Configuration
- Change default admin password
- Use HTTPS in production
- Configure proper client secrets
- Set up proper redirect URIs
- Enable email verification

### 2. Network Security
- Restrict Keycloak admin access
- Use proper firewall rules
- Configure SSL/TLS certificates

### 3. User Management
- Implement password policies
- Set up account lockout policies
- Configure session timeouts

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure Keycloak is running: `docker-compose ps`
   - Check logs: `docker-compose logs keycloak`

2. **Authentication Failed**
   - Verify client secret is correct
   - Check realm name matches
   - Ensure user exists and is enabled

3. **CORS Issues**
   - Verify Web Origins configuration
   - Check redirect URIs

### Useful Commands

```bash
# Check Keycloak logs
docker-compose logs -f keycloak

# Restart Keycloak
docker-compose restart keycloak

# Reset Keycloak database
docker-compose down -v
docker-compose up -d keycloak keycloak-db
```

## Integration with Other Services

### Gateway Configuration
The API Gateway can be configured to validate JWT tokens from Keycloak:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://keycloak:8080/realms/onified
```

### Frontend Integration
For Angular frontend integration, use libraries like:
- `angular-oauth2-oidc`
- `@auth0/angular-jwt`

## Next Steps

1. Configure user registration flows
2. Set up password reset functionality
3. Implement social login providers
4. Configure audit logging
5. Set up monitoring and alerting 