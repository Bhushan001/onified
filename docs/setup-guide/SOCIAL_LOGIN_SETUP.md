# Social Login Setup Guide - Google & LinkedIn

ClientId: 1017240346181-t1a36bue2362kccgc2fuf0gtjt79nmc8.apps.googleusercontent.com
secret: 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

## Overview
This guide explains how to configure Google and LinkedIn OAuth2 identity providers in Keycloak for the Onified platform.

## Prerequisites
- Keycloak server running (http://localhost:9090)
- Google Cloud Console access
- LinkedIn Developer Console access
- Admin access to Keycloak

## Step 1: Google OAuth2 Configuration

### 1.1 Create Google Cloud Project

1. **Go to Google Cloud Console:**
   - Visit: https://console.cloud.google.com/
   - Create a new project or select existing one

2. **Enable Google+ API:**
   - Go to "APIs & Services" → "Library"
   - Search for "Google+ API" and enable it

3. **Create OAuth2 Credentials:**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth 2.0 Client IDs"
   - Choose "Web application"
   - Configure:
     - **Name**: `Onified Social Login`
     - **Authorized JavaScript origins**: 
       - `http://localhost:4200`
       - `http://localhost:9090`
     - **Authorized redirect URIs**:
       - `http://localhost:9090/realms/onified/broker/google/endpoint`
       - `http://localhost:4200/auth/callback`

4. **Save Credentials:**
   - Copy the **Client ID** and **Client Secret**
   - Keep these secure for Keycloak configuration

### 1.2 Configure Google Identity Provider in Keycloak

1. **Access Keycloak Admin Console:**
   - URL: http://localhost:9090
   - Login: admin/admin123
   - Select realm: `onified`

2. **Navigate to Identity Providers:**
   - Go to "Identity Providers" in left sidebar
   - Click "Add provider" → "Google"

3. **Configure General Settings:**
   ```
   Alias: google
   Display Name: Google
   Provider ID: google
   ```

4. **Configure Settings Tab:**
   ```
   Client ID: [Your Google Client ID]
   Client Secret: [Your Google Client Secret]
   Default Scopes: openid profile email
   ```

5. **Advanced Settings:**
   - **Request Refresh Token**: `ON`
   - **Use User Info Param**: `ON`
   - **Accepts Prompts**: `none`
   - **Prompt**: `select_account`
   - **Store Tokens**: `true`
   - **Accepts Prompt=none Forward from Client**: `true`
   - **Disable User Info**: `false`
   - **Trust Email**: `true`
   - **Account Linking Only**: `false`
   - **Hide on Login Page**: `false`
   - **Verify Essential Claim**: `true`
   - **First Login Flow Override**: `None`
   - **Post Login Flow**: `None`
   - **Sync Mode**: `Import`
   - **Case Sensitive Username**: `false`

6. **Configure Mappers Tab:**
   
   **Mapper 1: Username**
   ```
   Name: username
   Mapper Type: Attribute Importer
   Attribute Name: preferred_username
   User Attribute Name: username
   ```

   **Mapper 2: Email**
   ```
   Name: email
   Mapper Type: Attribute Importer
   Attribute Name: email
   User Attribute Name: email
   ```

   **Mapper 3: First Name**
   ```
   Name: firstName
   Mapper Type: Attribute Importer
   Attribute Name: given_name
   User Attribute Name: firstName
   ```

   **Mapper 4: Last Name**
   ```
   Name: lastName
   Mapper Type: Attribute Importer
   Attribute Name: family_name
   User Attribute Name: lastName
   ```

7. **Save Configuration**

## Step 2: LinkedIn OAuth2 Configuration

### 2.1 Create LinkedIn Application

1. **Go to LinkedIn Developer Console:**
   - Visit: https://www.linkedin.com/developers/
   - Click "Create App"

2. **Configure Application:**
   - **App Name**: `Onified Social Login`
   - **LinkedIn Page**: Select your company page
   - **App Logo**: Upload appropriate logo

3. **Configure OAuth2 Settings:**
   - Go to "Auth" tab
   - **Redirect URLs**:
     - `http://localhost:9090/realms/onified/broker/linkedin/endpoint`
     - `http://localhost:4200/auth/callback`
   - **Application Permissions**:
     - `r_liteprofile` (Read basic profile)
     - `r_emailaddress` (Read email address)

4. **Save Application:**
   - Copy the **Client ID** and **Client Secret**
   - Keep these secure for Keycloak configuration

### 2.2 Configure LinkedIn Identity Provider in Keycloak

1. **Add LinkedIn Provider:**
   - Go to "Identity Providers" in Keycloak
   - Click "Add provider" → "LinkedIn"

2. **Configure General Settings:**
   ```
   Alias: linkedin
   Display Name: LinkedIn
   Provider ID: linkedin
   ```

3. **Configure Settings Tab:**
   ```
   Client ID: [Your LinkedIn Client ID]
   Client Secret: [Your LinkedIn Client Secret]
   Default Scopes: r_liteprofile r_emailaddress
   ```

4. **Advanced Settings:**
   - **Request Refresh Token**: `ON`
   - **Use User Info Param**: `ON`
   - **Accepts Prompts**: `consent`
   - **Store Tokens**: `true`
   - **Accepts Prompt=none Forward from Client**: `true`
   - **Disable User Info**: `false`
   - **Trust Email**: `true`
   - **Account Linking Only**: `false`
   - **Hide on Login Page**: `false`
   - **Verify Essential Claim**: `true`
   - **First Login Flow Override**: `None`
   - **Post Login Flow**: `None`
   - **Sync Mode**: `Import`
   - **Case Sensitive Username**: `false`

5. **Configure Mappers Tab:**
   
   **Mapper 1: Username**
   ```
   Name: username
   Mapper Type: Attribute Importer
   Attribute Name: id
   User Attribute Name: username
   ```

   **Mapper 2: Email**
   ```
   Name: email
   Mapper Type: Attribute Importer
   Attribute Name: email-address
   User Attribute Name: email
   ```

   **Mapper 3: First Name**
   ```
   Name: firstName
   Mapper Type: Attribute Importer
   Attribute Name: first-name
   User Attribute Name: firstName
   ```

   **Mapper 4: Last Name**
   ```
   Name: lastName
   Mapper Type: Attribute Importer
   Attribute Name: last-name
   User Attribute Name: lastName
   ```

6. **Save Configuration**

### **Advanced Settings Explanation**

#### **Token Management:**
- **Store Tokens**: `true` - Stores OAuth2 tokens for automatic refresh and session management
- **Request Refresh Token**: `ON` - Requests refresh tokens from OAuth provider
- **Use User Info Param**: `ON` - Uses user info endpoint for better data retrieval

#### **User Experience:**
- **Accepts Prompt=none Forward from Client**: `true` - Enables silent authentication for returning users
- **Prompt**: `select_account` - Shows account selection screen
- **Accepts Prompts**: `none` - Allows client to control prompt behavior

#### **Data Management:**
- **Disable User Info**: `false` - Fetches user profile data from OAuth provider
- **Trust Email**: `true` - Automatically verifies email addresses from trusted providers
- **Verify Essential Claim**: `true` - Ensures required user data is present

#### **Access Control:**
- **Account Linking Only**: `false` - Allows new user registration (not just account linking)
- **Hide on Login Page**: `false` - Shows social login buttons on login page

#### **Flow Control:**
- **First Login Flow Override**: `None` - Uses default first login flow
- **Post Login Flow**: `None` - Uses default post-login behavior

#### **Synchronization:**
- **Sync Mode**: `Import` - Imports user data on login (good balance of freshness and performance)
- **Case Sensitive Username**: `false` - Treats usernames as case-insensitive

## Step 3: Configure Authentication Flow

### 3.1 Option 1: Use Default Browser Flow (Recommended)

1. **Go to Authentication:**
   - Select `onified` realm
   - Go to "Authentication" in left sidebar

2. **Verify Default Flow:**
   - The default "browser" flow should work with social login
   - No additional configuration needed if identity providers are set up correctly

### 3.2 Option 2: Create Custom Authentication Flow

1. **Go to Authentication:**
   - Select `onified` realm
   - Go to "Authentication" in left sidebar

2. **Create New Flow:**
   - Click "Create" button
   - **Flow Type**: Select **"Basic"**
   - **Name**: `Social Login Flow`
   - **Description**: `Authentication flow for social login with Google and LinkedIn`
   - Click "OK"

3. **Configure Flow Steps:**
   
   **Step 1: Add Identity Provider Redirector**
   - Click "Add execution"
   - Select "Identity Provider Redirector"
   - Click "Add"
   - Set as "REQUIRED"

   **Step 2: Add Username Password Form**
   - Click "Add execution"
   - Select "Username Password Form"
   - Click "Add"
   - Set as "REQUIRED"

   **Step 3: Add Conditional User Role (Optional)**
   - Click "Add execution"
   - Look for "Conditional User Role" or "Conditional Role"
   - If available, select it and click "Add"
   - Set as "ALTERNATIVE"

   **Step 4: Add User Attribute Role Mapper (Optional)**
   - Click "Add execution"
   - Look for "User Attribute Role Mapper" or "User Attribute Mapper"
   - If available, select it and click "Add"
   - Set as "ALTERNATIVE"

   **Note**: If these optional steps are not available, the flow will work with just the first two steps.

4. **Set as Default:**
   - Go to "Bindings" tab
   - Set "Browser Flow" to "Social Login Flow"
   - Click "Save"

### 3.3 Configure Identity Providers

**Note**: Identity Providers are configured in a separate section, not in User Federation.

1. **Go to Identity Providers:**
   - Select `onified` realm
   - Go to "Identity Providers" in left sidebar (NOT "User Federation")

2. **Configure Google Identity Provider:**
   - Click "Add provider" → "Google"
   - Follow the Google configuration steps from Step 1 above

3. **Configure LinkedIn Identity Provider:**
   - Click "Add provider" → "LinkedIn"
   - Follow the LinkedIn configuration steps from Step 2 above

## Step 4: Update Authentication Service

### 4.1 Add Social Login Dependencies

Add to `authentication-service/pom.xml`:

```xml
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
    <version>23.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### 4.2 Create Social Login Controller

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/controller/SocialAuthController.java`:

```java
@RestController
@RequestMapping("/api/auth/social")
@Slf4j
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
            @RequestBody SocialLoginRequest request) {
        try {
            SocialLoginResponse response = socialAuthService.handleSocialLogin(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Social login failed", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Social login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialSignup(
            @RequestBody SocialSignupRequest request) {
        try {
            SocialLoginResponse response = socialAuthService.handleSocialSignup(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Social signup failed", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Social signup failed: " + e.getMessage()));
        }
    }

    @GetMapping("/oauth2/authorize/{provider}")
    public ResponseEntity<Void> initiateOAuth2Flow(
            @PathVariable String provider,
            @RequestParam String redirectUri,
            @RequestParam String state) {
        
        String authUrl = socialAuthService.buildOAuth2Url(provider, redirectUri, state);
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", authUrl)
            .build();
    }
}
```

### 4.3 Create Social Auth Service

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/service/SocialAuthService.java`:

```java
@Service
@Slf4j
public class SocialAuthService {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private UserManagementServiceClient userManagementClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String buildOAuth2Url(String provider, String redirectUri, String state) {
        return String.format("%s/realms/%s/protocol/openid-connect/auth" +
            "?client_id=%s" +
            "&response_type=code" +
            "&scope=openid profile email" +
            "&redirect_uri=%s" +
            "&state=%s" +
            "&kc_idp_hint=%s",
            keycloakUrl, realm, clientId, redirectUri, state, provider);
    }

    public SocialLoginResponse handleSocialLogin(SocialLoginRequest request) {
        // Exchange authorization code for tokens
        String accessToken = exchangeCodeForToken(request.getCode(), request.getRedirectUri());
        
        // Get user info from Keycloak
        UserInfo userInfo = getUserInfoFromKeycloak(accessToken);
        
        // Check if user exists
        UserDto existingUser = findExistingUser(userInfo);
        
        if (existingUser != null) {
            // User exists - return login response
            return createLoginResponse(existingUser, accessToken, false);
        } else {
            // User doesn't exist - this should be a signup
            throw new UserNotFoundException("User not found. Please use signup endpoint.");
        }
    }

    public SocialLoginResponse handleSocialSignup(SocialSignupRequest request) {
        // Exchange authorization code for tokens
        String accessToken = exchangeCodeForToken(request.getCode(), request.getRedirectUri());
        
        // Get user info from Keycloak
        UserInfo userInfo = getUserInfoFromKeycloak(accessToken);
        
        // Create user in User Management Service
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
            .username(userInfo.getPreferredUsername())
            .email(userInfo.getEmail())
            .firstName(userInfo.getGivenName())
            .lastName(userInfo.getFamilyName())
            .role(request.getRole())
            .build();
        
        UserDto newUser = userManagementClient.createUser(createUserRequest).getBody();
        
        // Create user in Keycloak if not exists
        keycloakUserService.createUserInKeycloak(newUser);
        
        return createLoginResponse(newUser, accessToken, true);
    }

    private String exchangeCodeForToken(String code, String redirectUri) {
        // Implementation to exchange authorization code for access token
        // This will call Keycloak token endpoint
        return null; // TODO: Implement
    }

    private UserInfo getUserInfoFromKeycloak(String accessToken) {
        // Implementation to get user info from Keycloak
        // This will call Keycloak userinfo endpoint
        return null; // TODO: Implement
    }

    private UserDto findExistingUser(UserInfo userInfo) {
        try {
            return userManagementClient.getUserByUsername(userInfo.getPreferredUsername()).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private SocialLoginResponse createLoginResponse(UserDto user, String accessToken, boolean isNewUser) {
        return SocialLoginResponse.builder()
            .accessToken(accessToken)
            .username(user.getUsername())
            .user(user)
            .isNewUser(isNewUser)
            .build();
    }
}
```

### 4.4 Create Data Models

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/SocialLoginRequest.java`:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginRequest {
    private String provider;
    private String code;
    private String state;
    private String redirectUri;
}
```

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/SocialSignupRequest.java`:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialSignupRequest {
    private String provider;
    private String code;
    private String state;
    private String redirectUri;
    private String role;
    private UserInfo userInfo;
}
```

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/SocialLoginResponse.java`:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private UserDto user;
    private boolean isNewUser;
}
```

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/UserInfo.java`:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String sub;
    private String preferredUsername;
    private String email;
    private String givenName;
    private String familyName;
    private String picture;
}
```

## Step 5: Update Environment Configuration

### 5.1 Update docker-compose.yml

Add social login environment variables:

```yaml
authentication-service:
  environment:
    # Existing variables...
    - GOOGLE_CLIENT_ID=your-google-client-id
    - GOOGLE_CLIENT_SECRET=your-google-client-secret
    - LINKEDIN_CLIENT_ID=your-linkedin-client-id
    - LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret
```

### 5.2 Update application.yml

Add social login configuration:

```yaml
social:
  google:
    client-id: ${GOOGLE_CLIENT_ID:}
    client-secret: ${GOOGLE_CLIENT_SECRET:}
  linkedin:
    client-id: ${LINKEDIN_CLIENT_ID:}
    client-secret: ${LINKEDIN_CLIENT_SECRET:}
```

## Step 6: Testing

### 6.1 Test OAuth2 Authorization URLs

```bash
# Test Google OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/google?redirect_uri=http://localhost:4200/auth/callback&state=test123"

# Test LinkedIn OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/linkedin?redirect_uri=http://localhost:4200/auth/callback&state=test123"
```

### 6.2 Test Social Login Endpoint

```bash
# Test social login (after getting authorization code)
curl -X POST http://localhost:9083/api/auth/social/login \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "code": "authorization_code_here",
    "state": "state_here",
    "redirectUri": "http://localhost:4200/auth/callback"
  }'
```

### 6.3 Test Social Signup Endpoint

```bash
# Test social signup
curl -X POST http://localhost:9083/api/auth/social/signup \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "code": "authorization_code_here",
    "state": "state_here",
    "redirectUri": "http://localhost:4200/auth/callback",
    "role": "PLATFORM.Management.Admin"
  }'
```

## Step 7: Security Considerations

### 7.1 State Parameter Validation

Always validate the state parameter to prevent CSRF attacks:

```java
private void validateStateParameter(String receivedState, String expectedState) {
    if (!receivedState.equals(expectedState)) {
        throw new SecurityException("Invalid state parameter");
    }
}
```

### 7.2 Token Validation

Validate tokens with Keycloak:

```java
private boolean validateToken(String token) {
    // Call Keycloak introspection endpoint
    return true; // TODO: Implement
}
```

### 7.3 Error Handling

Implement proper error handling:

```java
@ExceptionHandler(SecurityException.class)
public ResponseEntity<ApiResponse<String>> handleSecurityException(SecurityException e) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(e.getMessage()));
}
```

## Troubleshooting

### Common Issues

1. **Invalid Redirect URI**
   - Ensure redirect URIs match exactly in Google/LinkedIn and Keycloak
   - Check for trailing slashes

2. **CORS Issues**
   - Configure CORS in Keycloak
   - Add proper origins to Google/LinkedIn applications

3. **Token Exchange Failures**
   - Verify client secrets
   - Check authorization code hasn't expired
   - Ensure proper scopes are requested

### Advanced Settings Issues

4. **User Data Not Mapped**
   - **Problem**: User profile data (name, email) not populated
   - **Solution**: Ensure `Disable User Info: false` and `Use User Info Param: ON`
   - **Check**: Verify mappers are configured correctly

5. **Email Not Verified**
   - **Problem**: User email shows as unverified
   - **Solution**: Set `Trust Email: true` for trusted providers (Google, LinkedIn)
   - **Check**: Verify OAuth provider is trusted

6. **Social Login Not Working**
   - **Problem**: Social login buttons don't work or fail
   - **Solution**: Ensure `Account Linking Only: false` and `Hide on Login Page: false`
   - **Check**: Verify redirect URIs and client credentials

7. **Username Conflicts**
   - **Problem**: Username already exists errors
   - **Solution**: Set `Case Sensitive Username: false`
   - **Check**: Verify username mapper configuration

8. **Token Refresh Issues**
   - **Problem**: Users need to re-authenticate frequently
   - **Solution**: Ensure `Store Tokens: true` and `Request Refresh Token: ON`
   - **Check**: Verify token storage configuration

9. **Silent Authentication Fails**
   - **Problem**: `prompt=none` parameter not working
   - **Solution**: Set `Accepts Prompt=none Forward from Client: true`
   - **Check**: Verify user is already authenticated with provider

### Advanced Settings Quick Reference

| Setting | Google | LinkedIn | Purpose |
|---------|--------|----------|---------|
| **Store Tokens** | `true` | `true` | Enable token refresh |
| **Request Refresh Token** | `ON` | `ON` | Get refresh tokens |
| **Use User Info Param** | `ON` | `ON` | Better data retrieval |
| **Accepts Prompt=none** | `true` | `true` | Silent auth support |
| **Disable User Info** | `false` | `false` | Need user data |
| **Trust Email** | `true` | `true` | Auto-verify email |
| **Account Linking Only** | `false` | `false` | Allow new signups |
| **Hide on Login Page** | `false` | `false` | Show social buttons |
| **Verify Essential Claim** | `true` | `true` | Data integrity |
| **First Login Flow** | `None` | `None` | Use default |
| **Post Login Flow** | `None` | `None` | Use default |
| **Sync Mode** | `Import` | `Import` | Balance performance |
| **Case Sensitive Username** | `false` | `false` | Better UX |

### Debug Commands

```bash
# Check Keycloak logs
docker-compose logs -f keycloak

# Check authentication service logs
docker-compose logs -f authentication-service

# Test Keycloak health
curl http://localhost:9090/health

# Test OAuth2 authorization URLs
curl "http://localhost:9083/api/auth/social/oauth2/authorize/google?redirect_uri=http://localhost:4200/auth/callback&state=test123"
```

## Next Steps

1. **Implement token exchange logic**
2. **Add user profile mapping**
3. **Configure user role assignment**
4. **Add social login to other signup flows**
5. **Implement social account linking**
6. **Add social login analytics** 