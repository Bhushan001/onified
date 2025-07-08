# Social Login Implementation - Step by Step Guide

## 🎯 **Complete Implementation Checklist**

### **Phase 1: OAuth2 Provider Setup (30 minutes)**

#### ✅ **Step 1.1: Google OAuth2 Configuration**
1. **Go to Google Cloud Console:**
   - Visit: https://console.cloud.google.com/
   - Create new project: "Onified Social Login"

2. **Enable APIs:**
   - Go to "APIs & Services" → "Library"
   - Enable "Google+ API"
   - Enable "Google Identity API"

3. **Create OAuth2 Credentials:**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth 2.0 Client IDs"
   - Choose "Web application"
   - Configure:
     ```
     Name: Onified Social Login
     Authorized JavaScript origins:
     - http://localhost:4200
     - http://localhost:9090
     
     Authorized redirect URIs:
     - http://localhost:9090/realms/onified/broker/google/endpoint
     - http://localhost:4200/auth/callback
     ```

4. **Save Credentials:**
   - Copy **Client ID** and **Client Secret**
   - Store in secure location

#### ✅ **Step 1.2: LinkedIn OAuth2 Configuration**
1. **Go to LinkedIn Developer Console:**
   - Visit: https://www.linkedin.com/developers/
   - Click "Create App"

2. **Configure Application:**
   ```
   App Name: Onified Social Login
   LinkedIn Page: Select your company page
   App Logo: Upload appropriate logo
   ```

3. **Configure OAuth2 Settings:**
   - Go to "Auth" tab
   - **Redirect URLs**:
     - `http://localhost:9090/realms/onified/broker/linkedin/endpoint`
     - `http://localhost:4200/auth/callback`
   - **Application Permissions**:
     - `r_liteprofile` (Read basic profile)
     - `r_emailaddress` (Read email address)

4. **Save Credentials:**
   - Copy **Client ID** and **Client Secret**
   - Store in secure location

### **Phase 2: Keycloak Configuration (20 minutes)**

#### ✅ **Step 2.1: Add Google Identity Provider**
1. **Access Keycloak Admin Console:**
   ```bash
   # URL: http://localhost:9090
   # Login: admin/admin123
   # Select realm: onified
   ```

2. **Add Google Provider:**
   ```
   Navigate: Identity Providers → Add provider → Google
   
   Configuration:
   - Alias: google
   - Display Name: Google
   - Client ID: [Your Google Client ID]
   - Client Secret: [Your Google Client Secret]
   ```

3. **Configure Mappers:**
   ```
   Username: preferred_username
   Email: email
   First Name: given_name
   Last Name: family_name
   ```

#### ✅ **Step 2.2: Add LinkedIn Identity Provider**
1. **Add LinkedIn Provider:**
   ```
   Navigate: Identity Providers → Add provider → LinkedIn
   
   Configuration:
   - Alias: linkedin
   - Display Name: LinkedIn
   - Client ID: [Your LinkedIn Client ID]
   - Client Secret: [Your LinkedIn Client Secret]
   ```

2. **Configure Mappers:**
   ```
   Username: id
   Email: email-address
   First Name: first-name
   Last Name: last-name
   ```

### **Phase 3: Backend Implementation (45 minutes)**

#### ✅ **Step 3.1: Update Dependencies**
The dependencies have been updated in `authentication-service/pom.xml`:
- ✅ OAuth2 Client starter
- ✅ WebFlux for reactive HTTP client

#### ✅ **Step 3.2: Create Model Classes**
The following model classes have been created:
- ✅ `SocialLoginRequest.java`
- ✅ `SocialSignupRequest.java`
- ✅ `SocialLoginResponse.java`
- ✅ `UserInfo.java`
- ✅ `TokenResponse.java`

#### ✅ **Step 3.3: Create Social Auth Service**
The `SocialAuthService.java` has been created with:
- ✅ OAuth2 URL building
- ✅ Token exchange logic
- ✅ User info retrieval
- ✅ Social login/signup handling

#### ✅ **Step 3.4: Create Social Auth Controller**
The `SocialAuthController.java` has been created with:
- ✅ Social login endpoint
- ✅ Social signup endpoint
- ✅ OAuth2 authorization endpoint

#### ✅ **Step 3.5: Update Configuration**
The `application.yml` has been updated with:
- ✅ Social login configuration
- ✅ WebClient configuration

### **Phase 4: Environment Setup (10 minutes)**

#### ✅ **Step 4.1: Update Environment Variables**
Add to your `.env` file or `docker-compose.yml`:

```bash
# Social Login Environment Variables
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
LINKEDIN_CLIENT_ID=your-linkedin-client-id
LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret
```

#### ✅ **Step 4.2: Update docker-compose.yml**
Add to `authentication-service` environment:

```yaml
authentication-service:
  environment:
    # Existing variables...
    - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
    - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
    - LINKEDIN_CLIENT_ID=${LINKEDIN_CLIENT_ID}
    - LINKEDIN_CLIENT_SECRET=${LINKEDIN_CLIENT_SECRET}
```

### **Phase 5: Testing (30 minutes)**

#### ✅ **Step 5.1: Build and Start Services**
```bash
# Build authentication service
cd authentication-service
mvn clean install

# Start all services
docker-compose up -d
```

#### ✅ **Step 5.2: Test OAuth2 Authorization URLs**
```bash
# Test Google OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/google?redirect_uri=http://localhost:4200/auth/callback&state=test123"

# Test LinkedIn OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/linkedin?redirect_uri=http://localhost:4200/auth/callback&state=test123"
```

#### ✅ **Step 5.3: Test Frontend Integration**
1. **Start frontend:**
   ```bash
   cd onified-web
   ng serve
   ```

2. **Test social login:**
   - Go to http://localhost:4200/login
   - Click "Continue with Google" or "Continue with LinkedIn"
   - Complete OAuth flow
   - Verify successful login

3. **Test social signup:**
   - Go to http://localhost:4200/create-platform-admin
   - Click "Sign up with Google" or "Sign up with LinkedIn"
   - Complete OAuth flow
   - Verify successful signup

## 🚀 **Quick Start Commands**

### **1. Setup OAuth2 Providers (One-time setup)**
```bash
# Follow Phase 1 steps above
# Get Google and LinkedIn client IDs and secrets
```

### **2. Configure Keycloak (One-time setup)**
```bash
# Follow Phase 2 steps above
# Add identity providers in Keycloak admin console
```

### **3. Deploy Backend**
```bash
# Build and start services
cd authentication-service
mvn clean install
docker-compose up -d
```

### **4. Test Integration**
```bash
# Start frontend
cd onified-web
ng serve

# Test in browser
# http://localhost:4200/login
# http://localhost:4200/create-platform-admin
```

## 🔧 **Troubleshooting Guide**

### **Common Issues & Solutions**

#### **1. CORS Errors**
**Problem:** Browser console shows CORS errors
**Solution:**
```bash
# Check Keycloak CORS configuration
# Ensure proper origins are configured in Google/LinkedIn apps
```

#### **2. Invalid Redirect URI**
**Problem:** "Invalid redirect URI" error
**Solution:**
```bash
# Verify redirect URIs match exactly in:
# - Google Cloud Console
# - LinkedIn Developer Console
# - Keycloak configuration
# - Frontend callback URL
```

#### **3. Token Exchange Failures**
**Problem:** Authorization code exchange fails
**Solution:**
```bash
# Check client secrets are correct
# Verify authorization code hasn't expired
# Check Keycloak logs
docker-compose logs -f keycloak
```

#### **4. User Creation Failures**
**Problem:** Social signup fails to create user
**Solution:**
```bash
# Check User Management Service logs
docker-compose logs -f user-management-service

# Check database connectivity
docker-compose exec postgres psql -U postgres -d user_mgmt_db
```

### **Debug Commands**
```bash
# Check all service logs
docker-compose logs -f

# Test Keycloak health
curl http://localhost:9090/health

# Test authentication service health
curl http://localhost:9083/actuator/health

# Test user management service health
curl http://localhost:9085/actuator/health
```

## 📋 **Verification Checklist**

### **Backend Verification**
- [ ] Authentication service builds successfully
- [ ] Social login endpoints are accessible
- [ ] OAuth2 authorization URLs work
- [ ] Token exchange works
- [ ] User creation works
- [ ] Keycloak integration works

### **Frontend Verification**
- [ ] Social login buttons are visible
- [ ] OAuth2 flow initiates correctly
- [ ] Callback handling works
- [ ] User is logged in after social login
- [ ] User is created after social signup
- [ ] Proper error handling

### **Integration Verification**
- [ ] Complete social login flow works
- [ ] Complete social signup flow works
- [ ] User data is properly mapped
- [ ] Roles are assigned correctly
- [ ] Tokens are generated and stored
- [ ] Session management works

## 🎉 **Success Criteria**

Your social login implementation is complete when:

1. ✅ Users can sign up using Google or LinkedIn
2. ✅ Users can log in using Google or LinkedIn
3. ✅ User data is properly synchronized between services
4. ✅ Roles are assigned correctly during signup
5. ✅ Authentication tokens are generated and managed
6. ✅ Error handling works for all scenarios
7. ✅ Security measures are in place (state validation, etc.)

## 🔄 **Next Steps**

After successful implementation:

1. **Add error handling and validation**
2. **Implement token refresh logic**
3. **Add social account linking**
4. **Implement social login analytics**
5. **Add social login to other signup flows**
6. **Configure production settings**
7. **Add unit and integration tests** 