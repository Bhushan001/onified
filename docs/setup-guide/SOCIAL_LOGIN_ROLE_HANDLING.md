# Social Login Role Handling Guide

## Overview
This guide explains how roles are handled when users sign up using Google or LinkedIn OAuth2 providers in the Onified platform.

## 🔍 **Role Handling Architecture**

### **Role Assignment Strategy**

The system uses a **three-tier role assignment strategy**:

1. **Explicit Role** (Highest Priority)
   - Direct role specification from frontend
   - Validated against allowed roles
   - Overrides all other role assignments

2. **Signup Flow Mapping** (Medium Priority)
   - Role determined by signup flow type
   - Mapped automatically based on signup context
   - Fallback when explicit role is not provided

3. **Default Role** (Lowest Priority)
   - Default role for social signup
   - Used when no other role is specified
   - Ensures all users have a valid role

### **Role Mapping Configuration**

```java
// In SocialAuthService.java
private static final Map<String, String> SIGNUP_FLOW_ROLE_MAP = Map.of(
    "platform-admin", "PLATFORM.Management.Admin",
    "tenant-admin", "PLATFORM.Management.TenantAdmin", 
    "user", "PLATFORM.Management.User"
);
```

## 🎯 **Role Assignment Flow**

### **Step 1: Frontend Role Storage**
```typescript
// In signup-platform-admin.component.ts
onSocialSignup(provider: SocialProvider): void {
  // Store the signup flow for social signup
  localStorage.setItem('socialSignupFlow', 'platform-admin');
  
  this.authService.initiateSocialLogin(provider).subscribe({
    // OAuth flow initiation
  });
}
```

### **Step 2: OAuth2 Flow**
1. User clicks social signup button
2. Frontend stores signup flow type in localStorage
3. User is redirected to OAuth provider (Google/LinkedIn)
4. User authenticates with provider
5. Provider redirects back with authorization code

### **Step 3: Backend Role Processing**
```java
// In SocialAuthService.handleSocialSignup()
public SocialLoginResponse handleSocialSignup(SocialSignupRequest request) {
    // 1. Exchange authorization code for tokens
    String accessToken = exchangeCodeForToken(request.getCode(), request.getRedirectUri());
    
    // 2. Get user info from Keycloak
    UserInfo userInfo = getUserInfoFromKeycloak(accessToken);
    
    // 3. Determine and validate role
    String role = determineUserRole(request);
    log.info("Assigned role for social signup: {}", role);
    
    // 4. Create user with determined role
    CreateUserRequest createUserRequest = CreateUserRequest.builder()
        .username(userInfo.getPreferredUsername())
        .email(userInfo.getEmail())
        .firstName(userInfo.getGivenName())
        .lastName(userInfo.getFamilyName())
        .role(role)  // Assigned role
        .build();
    
    // 5. Create user in User Management Service
    UserDto newUser = userManagementClient.createUser(createUserRequest).getBody();
    
    // 6. Create user in Keycloak
    keycloakUserService.createUserInKeycloak(newUser);
    
    return createLoginResponse(newUser, accessToken, true);
}
```

### **Step 4: Role Determination Logic**
```java
private String determineUserRole(SocialSignupRequest request) {
    // Priority 1: Explicit role validation
    if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
        if (isValidRole(request.getRole())) {
            return request.getRole();
        } else {
            log.warn("Invalid explicit role provided: {}. Using signup flow mapping.", request.getRole());
        }
    }
    
    // Priority 2: Signup flow mapping
    if (request.getSignupFlow() != null && SIGNUP_FLOW_ROLE_MAP.containsKey(request.getSignupFlow())) {
        return SIGNUP_FLOW_ROLE_MAP.get(request.getSignupFlow());
    }
    
    // Priority 3: Default role
    log.info("No valid role found, using default role for social signup");
    return "PLATFORM.Management.User";
}
```

## 🔐 **Role Validation**

### **Valid Role Patterns**
```java
private boolean isValidRole(String role) {
    return SIGNUP_FLOW_ROLE_MAP.containsValue(role) || 
           role.startsWith("PLATFORM.Management.") ||
           role.startsWith("TENANT.") ||
           role.startsWith("APPLICATION.");
}
```

### **Supported Role Types**
- **Platform Management Roles:**
  - `PLATFORM.Management.Admin` - Platform administrator
  - `PLATFORM.Management.TenantAdmin` - Tenant administrator
  - `PLATFORM.Management.User` - Platform user

- **Tenant Roles:**
  - `TENANT.*` - Any tenant-specific role

- **Application Roles:**
  - `APPLICATION.*` - Any application-specific role

## 📋 **Signup Flow Types**

### **1. Platform Admin Signup**
```typescript
// Flow: platform-admin
// Assigned Role: PLATFORM.Management.Admin
// Use Case: Platform administrators, system operators

localStorage.setItem('socialSignupFlow', 'platform-admin');
```

### **2. Tenant Admin Signup**
```typescript
// Flow: tenant-admin
// Assigned Role: PLATFORM.Management.TenantAdmin
// Use Case: Organization administrators, tenant managers

localStorage.setItem('socialSignupFlow', 'tenant-admin');
```

### **3. User Signup**
```typescript
// Flow: user
// Assigned Role: PLATFORM.Management.User
// Use Case: Regular users, end users

localStorage.setItem('socialSignupFlow', 'user');
```

## 🔄 **Frontend Integration**

### **Auth Service Method**
```typescript
public handleSocialSignup(
  code: string, 
  state: string, 
  provider: SocialProvider, 
  signupFlow: 'platform-admin' | 'tenant-admin' | 'user',
  role?: 'PLATFORM.Management.Admin' | 'PLATFORM.Management.TenantAdmin' | 'PLATFORM.Management.User',
  userInfo?: { firstName?: string; lastName?: string; email?: string; avatar?: string }
): Observable<{ success: boolean; message?: string }>
```

### **Request Payload**
```typescript
interface SocialSignupRequest {
  provider: SocialProvider;
  code: string;
  state?: string;
  redirectUri: string;
  signupFlow: 'platform-admin' | 'tenant-admin' | 'user';
  role?: 'PLATFORM.Management.Admin' | 'PLATFORM.Management.TenantAdmin' | 'PLATFORM.Management.User';
  userInfo?: {
    firstName?: string;
    lastName?: string;
    email?: string;
    avatar?: string;
  };
}
```

### **Callback Handling**
```typescript
// In auth-callback.component.ts
const signupFlow = localStorage.getItem('socialSignupFlow');

if (signupFlow) {
  // This is a social signup
  localStorage.removeItem('socialSignupFlow');
  
  this.authService.handleSocialSignup(code, state, finalProvider, signupFlow as any).subscribe({
    // Handle signup response
  });
}
```

## 🛡️ **Security Considerations**

### **1. Role Validation**
- All roles are validated against allowed patterns
- Invalid roles are logged and rejected
- Default role is used as fallback

### **2. CSRF Protection**
- State parameter validation prevents CSRF attacks
- Stored signup flow is cleared after use
- No persistent role storage in localStorage

### **3. Role Escalation Prevention**
- Explicit roles are validated against allowed patterns
- Signup flow mapping prevents role manipulation
- Backend enforces role assignment logic

### **4. Audit Logging**
```java
log.info("Handling social signup for provider: {} with flow: {}", request.getProvider(), request.getSignupFlow());
log.info("Assigned role for social signup: {}", role);
```

## 🧪 **Testing Role Assignment**

### **Test Cases**

#### **1. Platform Admin Signup**
```bash
# Expected: PLATFORM.Management.Admin
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "code": "auth_code",
    "state": "state123",
    "redirectUri": "http://localhost:4200/auth/callback",
    "signupFlow": "platform-admin"
  }'
```

#### **2. Tenant Admin Signup**
```bash
# Expected: PLATFORM.Management.TenantAdmin
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "linkedin",
    "code": "auth_code",
    "state": "state123",
    "redirectUri": "http://localhost:4200/auth/callback",
    "signupFlow": "tenant-admin"
  }'
```

#### **3. User Signup**
```bash
# Expected: PLATFORM.Management.User
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "code": "auth_code",
    "state": "state123",
    "redirectUri": "http://localhost:4200/auth/callback",
    "signupFlow": "user"
  }'
```

#### **4. Explicit Role Override**
```bash
# Expected: PLATFORM.Management.Admin (explicit role overrides signupFlow)
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "code": "auth_code",
    "state": "state123",
    "redirectUri": "http://localhost:4200/auth/callback",
    "signupFlow": "user",
    "role": "PLATFORM.Management.Admin"
  }'
```

## 🔧 **Configuration**

### **Environment Variables**
```bash
# No additional environment variables needed for role handling
# Role mapping is hardcoded in SocialAuthService for security
```

### **Backend Configuration**
```yaml
# application.yml - No additional configuration needed
# Role handling is implemented in Java code
```

### **Frontend Configuration**
```typescript
// No additional configuration needed
// Role handling is implemented in TypeScript interfaces
```

## 🚨 **Troubleshooting**

### **Common Issues**

#### **1. Invalid Role Assignment**
**Problem:** User gets unexpected role
**Solution:**
```bash
# Check logs for role assignment
docker-compose logs -f authentication-service | grep "Assigned role"

# Verify signup flow mapping
# Check if explicit role is being used
```

#### **2. Role Validation Failures**
**Problem:** Social signup fails with role validation error
**Solution:**
```bash
# Check role validation logic
# Verify role patterns in isValidRole() method
# Check if role starts with allowed prefixes
```

#### **3. Missing Role Assignment**
**Problem:** User created without role
**Solution:**
```bash
# Check if determineUserRole() returns default role
# Verify SIGNUP_FLOW_ROLE_MAP contains required mappings
# Check if signupFlow parameter is being passed correctly
```

### **Debug Commands**
```bash
# Check role assignment logs
docker-compose logs -f authentication-service | grep -E "(role|signup)"

# Test role validation
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{"provider":"google","code":"test","signupFlow":"platform-admin"}'

# Check user creation in database
docker-compose exec postgres psql -U postgres -d user_mgmt_db -c "SELECT username, role FROM users WHERE username LIKE '%google%';"
```

## 📈 **Monitoring & Analytics**

### **Role Assignment Metrics**
- Track role assignment by signup flow
- Monitor role validation failures
- Log role escalation attempts

### **Audit Trail**
```java
// Log role assignment decisions
log.info("Role assignment - Provider: {}, Flow: {}, Final Role: {}", 
    request.getProvider(), request.getSignupFlow(), role);
```

## 🔄 **Future Enhancements**

### **1. Dynamic Role Mapping**
- Load role mappings from configuration
- Support for custom role patterns
- Role inheritance and hierarchy

### **2. Role-Based Onboarding**
- Different onboarding flows based on role
- Role-specific feature access
- Progressive role assignment

### **3. Multi-Role Support**
- Support for multiple roles per user
- Role combination validation
- Role conflict resolution

### **4. Role Analytics**
- Role assignment analytics
- Role usage patterns
- Role effectiveness metrics 