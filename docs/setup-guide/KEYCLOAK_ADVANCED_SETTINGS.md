# Keycloak Identity Provider Advanced Settings Guide

## Overview
This guide provides detailed explanations and recommended configurations for all advanced settings in Keycloak identity providers for Google and LinkedIn OAuth2 integration.

## 🔧 **Advanced Settings Categories**

### **1. Token Management Settings**

#### **Store Tokens: `true` (Recommended)**
- **Purpose**: Stores OAuth2 tokens (access token, refresh token) in Keycloak
- **Benefits**: 
  - Automatic token refresh
  - Better session management
  - Reduced re-authentication
- **Impact**: Improved user experience, longer sessions
- **Security**: Tokens are encrypted and stored securely

#### **Request Refresh Token: `ON` (Recommended)**
- **Purpose**: Requests refresh tokens from the OAuth provider
- **Benefits**: 
  - Automatic token renewal
  - Seamless user experience
  - Reduced login prompts
- **Impact**: Users stay logged in longer
- **Note**: Not all providers support refresh tokens

#### **Use User Info Param: `ON` (Recommended)**
- **Purpose**: Uses the user info endpoint for better data retrieval
- **Benefits**: 
  - More reliable user data fetching
  - Better error handling
  - Consistent data format
- **Impact**: More reliable user profile creation

### **2. User Experience Settings**

#### **Accepts Prompt=none Forward from Client: `true` (Recommended)**
- **Purpose**: Allows clients to pass `prompt=none` parameter
- **Benefits**: 
  - Silent authentication for returning users
  - Faster login experience
  - Better UX for embedded applications
- **Impact**: Reduced login friction
- **Use Case**: Single-page applications, embedded widgets

#### **Prompt: `select_account` (Google) / `consent` (LinkedIn)**
- **Purpose**: Controls the OAuth provider's prompt behavior
- **Google Options**:
  - `select_account`: Shows account selection screen
  - `consent`: Shows consent screen
  - `none`: No prompts (silent auth)
- **LinkedIn Options**:
  - `consent`: Shows consent screen
  - `none`: No prompts
- **Impact**: Controls user interaction with OAuth provider

#### **Accepts Prompts: `none` (Google) / `consent` (LinkedIn)**
- **Purpose**: Specifies which prompts the provider accepts
- **Google**: `none` - Accepts no prompts from client
- **LinkedIn**: `consent` - Accepts consent prompts
- **Impact**: Controls client-side prompt behavior

### **3. Data Management Settings**

#### **Disable User Info: `false` (Recommended)**
- **Purpose**: Controls whether Keycloak fetches user info from OAuth provider
- **Why disable**: We need user profile data for account creation
- **Impact**: User data mapping won't work if enabled
- **Required for**: Username, email, name mapping

#### **Trust Email: `true` (Recommended)**
- **Purpose**: Automatically verifies email addresses from trusted providers
- **Benefits**: 
  - No manual email verification required
  - Faster onboarding
  - Better user experience
- **Security**: Only enable for trusted providers (Google, LinkedIn)
- **Impact**: Users can immediately use the application

#### **Verify Essential Claim: `true` (Recommended)**
- **Purpose**: Ensures required user data is present in the token
- **Benefits**: 
  - Data integrity
  - Prevents incomplete profiles
  - Better error handling
- **Impact**: More reliable user creation
- **Required Claims**: email, sub (subject)

### **4. Access Control Settings**

#### **Account Linking Only: `false` (Recommended)**
- **Purpose**: Restricts provider to only link existing accounts
- **Why disable**: We want to allow new user registration
- **Impact**: New users can sign up via social login
- **Use Case**: New user onboarding

#### **Hide on Login Page: `false` (Recommended)**
- **Purpose**: Hides social login buttons on the login page
- **Why disable**: We want users to see social login options
- **Impact**: Social login buttons will be visible
- **Use Case**: Public login page

### **5. Flow Control Settings**

#### **First Login Flow Override: `None` (Default)**
- **Purpose**: Custom authentication flow for first-time social login users
- **Options**: 
  - `None`: Use default flow
  - Custom flow name: Use specific authentication flow
- **Impact**: Controls first-time user experience
- **Recommendation**: Leave as None for simplicity

#### **Post Login Flow: `None` (Default)**
- **Purpose**: Custom actions after successful social login
- **Options**:
  - `None`: Use default post-login behavior
  - Custom flow name: Execute specific post-login actions
- **Impact**: Controls post-login redirects and actions
- **Recommendation**: Leave as None for standard behavior

### **6. Synchronization Settings**

#### **Sync Mode: `Import` (Recommended)**
- **Purpose**: Controls how user data is synchronized
- **Options**:
  - `Import`: Import user data on login (recommended)
  - `Force`: Always update user data from provider
  - `Legacy`: Legacy synchronization mode
- **Benefits**: 
  - Good balance of data freshness and performance
  - Updates user data when they log in
  - Doesn't overwrite local changes unnecessarily
- **Impact**: User data stays current

#### **Case Sensitive Username: `false` (Recommended)**
- **Purpose**: Controls whether usernames are case-sensitive
- **Why disable**: Most applications treat usernames as case-insensitive
- **Benefits**: 
  - Better user experience
  - Prevents username confusion
  - Standard web application behavior
- **Impact**: Usernames are case-insensitive

## 📋 **Complete Configuration Reference**

### **Google Identity Provider Advanced Settings**

```
Token Management:
├── Store Tokens: true
├── Request Refresh Token: ON
└── Use User Info Param: ON

User Experience:
├── Accepts Prompt=none Forward from Client: true
├── Prompt: select_account
└── Accepts Prompts: none

Data Management:
├── Disable User Info: false
├── Trust Email: true
└── Verify Essential Claim: true

Access Control:
├── Account Linking Only: false
└── Hide on Login Page: false

Flow Control:
├── First Login Flow Override: None
└── Post Login Flow: None

Synchronization:
├── Sync Mode: Import
└── Case Sensitive Username: false
```

### **LinkedIn Identity Provider Advanced Settings**

```
Token Management:
├── Store Tokens: true
├── Request Refresh Token: ON
└── Use User Info Param: ON

User Experience:
├── Accepts Prompt=none Forward from Client: true
├── Prompt: consent
└── Accepts Prompts: consent

Data Management:
├── Disable User Info: false
├── Trust Email: true
└── Verify Essential Claim: true

Access Control:
├── Account Linking Only: false
└── Hide on Login Page: false

Flow Control:
├── First Login Flow Override: None
└── Post Login Flow: None

Synchronization:
├── Sync Mode: Import
└── Case Sensitive Username: false
```

## 🛡️ **Security Considerations**

### **Critical Security Settings**

1. **Trust Email: `true`**
   - Only enable for trusted OAuth providers
   - Google and LinkedIn are considered trusted
   - Don't enable for unknown or untrusted providers

2. **Verify Essential Claim: `true`**
   - Ensures data integrity
   - Prevents incomplete user profiles
   - Required for secure user creation

3. **Store Tokens: `true`**
   - Enables proper token management
   - Improves security through token refresh
   - Reduces security risks of expired tokens

### **Performance Settings**

1. **Sync Mode: `Import`**
   - Good balance of data freshness and performance
   - Updates user data on login
   - Doesn't impact performance significantly

2. **Disable User Info: `false`**
   - Required for user data mapping
   - Minimal performance impact
   - Essential for user profile creation

## 🧪 **Testing Advanced Settings**

### **Test Case 1: Token Refresh**
```bash
# 1. Login via social login
# 2. Wait for token to expire (or check token expiration)
# 3. Perform an action that requires authentication
# 4. Verify user stays logged in (token refresh works)
```

### **Test Case 2: Silent Authentication**
```bash
# 1. Login via social login
# 2. Logout
# 3. Try to login again
# 4. Verify silent authentication works (no provider prompts)
```

### **Test Case 3: User Data Mapping**
```bash
# 1. Login via social login with new account
# 2. Check user profile in Keycloak Admin Console
# 3. Verify email, firstName, lastName are populated
# 4. Check if email is verified
```

### **Test Case 4: Username Handling**
```bash
# 1. Create user with username "TestUser"
# 2. Try to create another user with username "testuser"
# 3. Verify behavior based on Case Sensitive Username setting
```

## 🔧 **Troubleshooting Advanced Settings**

### **Common Issues and Solutions**

#### **Issue 1: User Data Not Mapped**
**Symptoms**: User profile shows empty fields
**Root Cause**: `Disable User Info: true` or incorrect mappers
**Solution**:
```bash
# 1. Set Disable User Info: false
# 2. Verify mappers are configured correctly
# 3. Check Use User Info Param: ON
# 4. Test with a new social login
```

#### **Issue 2: Email Not Verified**
**Symptoms**: User email shows as unverified
**Root Cause**: `Trust Email: false`
**Solution**:
```bash
# 1. Set Trust Email: true
# 2. Verify OAuth provider is trusted
# 3. Check email claim in token
```

#### **Issue 3: Frequent Re-authentication**
**Symptoms**: Users need to login frequently
**Root Cause**: Token refresh not working
**Solution**:
```bash
# 1. Set Store Tokens: true
# 2. Set Request Refresh Token: ON
# 3. Check token expiration settings
# 4. Verify refresh token is received
```

#### **Issue 4: Silent Authentication Fails**
**Symptoms**: `prompt=none` parameter not working
**Root Cause**: `Accepts Prompt=none Forward from Client: false`
**Solution**:
```bash
# 1. Set Accepts Prompt=none Forward from Client: true
# 2. Verify user is authenticated with provider
# 3. Check provider supports silent auth
```

#### **Issue 5: Username Conflicts**
**Symptoms**: "Username already exists" errors
**Root Cause**: `Case Sensitive Username: true`
**Solution**:
```bash
# 1. Set Case Sensitive Username: false
# 2. Check username mapper configuration
# 3. Verify username generation logic
```

## 📈 **Monitoring and Logging**

### **Key Metrics to Monitor**

1. **Token Refresh Success Rate**
   - Monitor successful token refreshes
   - Track refresh token failures
   - Alert on high failure rates

2. **User Data Mapping Success**
   - Track successful user profile creation
   - Monitor missing user data
   - Alert on mapping failures

3. **Silent Authentication Success**
   - Monitor silent auth success rate
   - Track user experience improvements
   - Measure login friction reduction

### **Logging Configuration**

```yaml
# Keycloak logging for identity providers
logging:
  level:
    org.keycloak.broker: DEBUG
    org.keycloak.services.resources.IdentityBrokerService: DEBUG
```

### **Audit Trail**

```java
// Log identity provider events
log.info("Identity Provider Event - Provider: {}, Action: {}, User: {}", 
    provider, action, username);
```

## 🔄 **Best Practices**

### **1. Security Best Practices**
- Only enable `Trust Email` for trusted providers
- Always enable `Verify Essential Claim`
- Use `Store Tokens` for better security
- Monitor token refresh failures

### **2. Performance Best Practices**
- Use `Sync Mode: Import` for good balance
- Disable `Disable User Info` only if not needed
- Monitor user data mapping performance
- Cache user info when appropriate

### **3. User Experience Best Practices**
- Enable silent authentication for better UX
- Use appropriate prompt settings
- Ensure email verification works
- Provide clear error messages

### **4. Maintenance Best Practices**
- Regularly review and update settings
- Monitor provider changes and updates
- Test settings after Keycloak updates
- Document custom configurations

## 📚 **Additional Resources**

### **Keycloak Documentation**
- [Identity Brokering](https://www.keycloak.org/docs/latest/server_admin/#_identity_broker)
- [OIDC Identity Provider](https://www.keycloak.org/docs/latest/server_admin/#_oidc)
- [User Profile](https://www.keycloak.org/docs/latest/server_admin/#_user-profile)

### **OAuth2 Provider Documentation**
- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [LinkedIn OAuth2](https://developer.linkedin.com/docs/oauth2)

### **Testing Tools**
- [OAuth2 Playground](https://oauth2.thephpleague.com/playground/)
- [JWT Debugger](https://jwt.io/)
- [Keycloak Admin Console](http://localhost:9090) 