# Testing Guide - Complete Authentication Flow

## Overview
Now that Keycloak is working and returning access tokens, this guide helps you test the complete authentication flow from frontend to backend.

## 🧪 Testing Checklist

### ✅ Keycloak Integration (COMPLETED)
- [x] Keycloak server running on http://localhost:9090
- [x] Realm `onified` created
- [x] Client `onified-auth-service` configured
- [x] Client `onified-auth-service` configured
- [x] Access tokens being returned successfully

## 🔍 Step-by-Step Testing

### 1. Test Authentication Service API

#### Test Login Endpoint
```bash
# Test login with Keycloak user
curl -X POST http://localhost:9081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": {
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 300,
    "username": "admin"
  }
}
```

#### Test Health Endpoint
```bash
curl http://localhost:9081/api/auth/health
```

**Expected Response:**
```json
{
  "statusCode": 200,
  "status": "SUCCESS",
  "body": "Authentication Service is running"
}
```

#### Test Token Refresh
```bash
# Use the refresh token from login response
curl -X POST "http://localhost:9081/api/auth/refresh?refreshToken=YOUR_REFRESH_TOKEN"
```

### 2. Test API Gateway

#### Test Gateway Health
```bash
curl http://localhost:9080/actuator/health
```

#### Test Gateway Routing
```bash
# Test authentication service through gateway
curl http://localhost:9080/api/auth/health

# Test other services through gateway
curl http://localhost:9080/appConfig/api/applications
curl http://localhost:9080/ums/users
curl http://localhost:9080/permissions/api/roles
```

### 3. Test Angular Frontend

#### Start Frontend (Development Mode)
```bash
cd web
ng serve
```

#### Test Frontend Authentication
1. Navigate to http://localhost:4200
2. Try to login with Keycloak user credentials
3. Check browser console for any errors
4. Verify token storage in localStorage

#### Test Frontend API Calls
```bash
# Check if frontend can call backend APIs
curl -H "Origin: http://localhost:4200" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS http://localhost:9080/api/auth/login
```

### 4. Test Complete Flow

#### End-to-End Authentication Test
1. **Start all services:**
   ```bash
   docker-compose up -d
   cd web && ng serve
   ```

2. **Open browser to:** http://localhost:4200

3. **Login with Keycloak user:**
   - Username: `admin`
   - Password: `admin123`

4. **Verify successful login:**
   - Should redirect to dashboard
   - Check localStorage for tokens
   - Check browser console for errors

5. **Test protected routes:**
   - Navigate to different pages
   - Verify authentication state persists
   - Test logout functionality

## 🔧 Debugging Commands

### Check Service Status
```bash
# Check all containers
docker-compose ps

# Check service logs
docker-compose logs -f authentication-service
docker-compose logs -f onified-gateway
docker-compose logs -f keycloak
```

### Check Network Connectivity
```bash
# Test service communication
docker-compose exec authentication-service ping keycloak
docker-compose exec onified-gateway ping authentication-service
```

### Check Token Validity
```bash
# Decode JWT token (replace with your actual token)
echo "YOUR_ACCESS_TOKEN" | cut -d. -f2 | base64 -d | jq .
```

### Check Database
```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U onified -d onified

# List tables
\dt

# Check user data
SELECT * FROM users;
```

## 🐛 Common Issues and Solutions

### 1. CORS Errors
**Symptoms:** Browser console shows CORS errors
**Solution:**
```bash
# Check gateway CORS configuration
curl -H "Origin: http://localhost:4200" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS http://localhost:9080/api/auth/login
```

### 2. Token Validation Issues
**Symptoms:** 401 Unauthorized errors
**Solution:**
- Verify client secret in `docker-compose.yml`
- Check token expiration
- Validate JWT signature

### 3. Service Discovery Issues
**Symptoms:** Services can't find each other
**Solution:**
```bash
# Check Eureka dashboard
curl http://localhost:8761

# Check service registration
docker-compose logs -f eureka-server
```

### 4. Frontend Authentication Issues
**Symptoms:** Login fails or tokens not stored
**Solution:**
- Check browser console for errors
- Verify environment configuration
- Check localStorage for tokens

## 📊 Performance Testing

### Load Testing Authentication
```bash
# Test login endpoint performance
ab -n 100 -c 10 -p login_data.json \
   -T application/json \
   http://localhost:9081/api/auth/login
```

### Token Refresh Testing
```bash
# Test token refresh performance
ab -n 50 -c 5 \
   "http://localhost:9081/api/auth/refresh?refreshToken=YOUR_TOKEN"
```

## 🔒 Security Testing

### Test Token Security
```bash
# Test with invalid token
curl -H "Authorization: Bearer invalid-token" \
     http://localhost:9080/api/auth/health

# Test with expired token
curl -H "Authorization: Bearer expired-token" \
     http://localhost:9080/api/auth/health
```

### Test CORS Security
```bash
# Test with different origins
curl -H "Origin: http://malicious-site.com" \
     -H "Access-Control-Request-Method: POST" \
     -X OPTIONS http://localhost:9080/api/auth/login
```

## 📝 Test Results Template

### Authentication Service Tests
```
✅ Login endpoint: [PASS/FAIL]
✅ Health endpoint: [PASS/FAIL]
✅ Token refresh: [PASS/FAIL]
✅ Error handling: [PASS/FAIL]
```

### API Gateway Tests
```
✅ Health endpoint: [PASS/FAIL]
✅ Service routing: [PASS/FAIL]
✅ CORS headers: [PASS/FAIL]
✅ Authentication: [PASS/FAIL]
```

### Frontend Tests
```
✅ Login form: [PASS/FAIL]
✅ Token storage: [PASS/FAIL]
✅ API calls: [PASS/FAIL]
✅ Error handling: [PASS/FAIL]
```

### Integration Tests
```
✅ End-to-end login: [PASS/FAIL]
✅ Token persistence: [PASS/FAIL]
✅ Logout functionality: [PASS/FAIL]
✅ Protected routes: [PASS/FAIL]
```

## 🚀 Next Steps After Testing

1. **If all tests pass:**
   - Deploy to development environment
   - Set up monitoring and logging
   - Configure production settings

2. **If tests fail:**
   - Check service logs for errors
   - Verify configuration settings
   - Test individual components
   - Review troubleshooting guide

3. **Performance optimization:**
   - Monitor response times
   - Optimize database queries
   - Configure caching
   - Set up load balancing

## 📞 Support

If you encounter issues during testing:
1. Check the troubleshooting sections in each service guide
2. Review service logs for error messages
3. Verify configuration settings
4. Test individual components in isolation 