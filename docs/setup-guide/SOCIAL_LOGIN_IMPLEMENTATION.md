# Social Login Backend Implementation Guide

## Overview
This guide provides step-by-step instructions to implement social login backend integration for Google and LinkedIn in the Onified platform.

## Prerequisites
- Keycloak server running
- Authentication service codebase
- User Management service codebase
- Google Cloud Console access
- LinkedIn Developer Console access

## Step 1: Configure OAuth2 Applications

### 1.1 Google OAuth2 Setup

1. **Create Google Cloud Project:**
   ```bash
   # Go to https://console.cloud.google.com/
   # Create new project: "Onified Social Login"
   ```

2. **Enable APIs:**
   ```bash
   # Enable Google+ API
   # Enable Google Identity API
   ```

3. **Create OAuth2 Credentials:**
   ```
   Application Type: Web application
   Name: Onified Social Login
   Authorized JavaScript origins:
   - http://localhost:4200
   - http://localhost:9090
   
   Authorized redirect URIs:
   - http://localhost:9090/realms/onified/broker/google/endpoint
   - http://localhost:4200/auth/callback
   ```

4. **Save Credentials:**
   - Copy Client ID and Client Secret
   - Store securely for later use

### 1.2 LinkedIn OAuth2 Setup

1. **Create LinkedIn Application:**
   ```bash
   # Go to https://www.linkedin.com/developers/
   # Create new app: "Onified Social Login"
   ```

2. **Configure OAuth2 Settings:**
   ```
   Redirect URLs:
   - http://localhost:9090/realms/onified/broker/linkedin/endpoint
   - http://localhost:4200/auth/callback
   
   Application Permissions:
   - r_liteprofile
   - r_emailaddress
   ```

3. **Save Credentials:**
   - Copy Client ID and Client Secret
   - Store securely for later use

## Step 2: Configure Keycloak Identity Providers

### 2.1 Add Google Identity Provider

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

### 2.2 Add LinkedIn Identity Provider

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

## Step 3: Update Authentication Service

### 3.1 Add Dependencies

Update `authentication-service/pom.xml`:

```xml
<!-- Add these dependencies -->
<dependency>
    <groupId>org.keycloak</groupId>
    <artifactId>keycloak-spring-boot-starter</artifactId>
    <version>23.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### 3.2 Create Social Login Models

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/SocialLoginRequest.java`:

```java
package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

### 3.3 Create Social Auth Service

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/service/SocialAuthService.java`:

```java
package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.model.*;
import com.onified.ai.authentication_service.client.UserManagementServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

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

    private final WebClient webClient = WebClient.builder().build();

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
        log.info("Handling social login for provider: {}", request.getProvider());
        
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
            throw new RuntimeException("User not found. Please use signup endpoint.");
        }
    }

    public SocialLoginResponse handleSocialSignup(SocialSignupRequest request) {
        log.info("Handling social signup for provider: {}", request.getProvider());
        
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
        
        ResponseEntity<UserDto> userResponse = userManagementClient.createUser(createUserRequest);
        UserDto newUser = userResponse.getBody();
        
        if (newUser == null) {
            throw new RuntimeException("Failed to create user in User Management Service");
        }
        
        // Create user in Keycloak if not exists
        keycloakUserService.createUserInKeycloak(newUser);
        
        return createLoginResponse(newUser, accessToken, true);
    }

    private String exchangeCodeForToken(String code, String redirectUri) {
        log.info("Exchanging authorization code for token");
        
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);
        
        Map<String, String> tokenRequest = Map.of(
            "grant_type", "authorization_code",
            "client_id", clientId,
            "client_secret", clientSecret,
            "code", code,
            "redirect_uri", redirectUri
        );
        
        TokenResponse response = webClient.post()
            .uri(tokenUrl)
            .bodyValue(tokenRequest)
            .retrieve()
            .bodyToMono(TokenResponse.class)
            .block();
        
        if (response == null || response.getAccessToken() == null) {
            throw new RuntimeException("Failed to exchange code for token");
        }
        
        return response.getAccessToken();
    }

    private UserInfo getUserInfoFromKeycloak(String accessToken) {
        log.info("Getting user info from Keycloak");
        
        String userInfoUrl = String.format("%s/realms/%s/protocol/openid-connect/userinfo", keycloakUrl, realm);
        
        UserInfo userInfo = webClient.get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(UserInfo.class)
            .block();
        
        if (userInfo == null) {
            throw new RuntimeException("Failed to get user info from Keycloak");
        }
        
        return userInfo;
    }

    private UserDto findExistingUser(UserInfo userInfo) {
        try {
            ResponseEntity<UserDto> response = userManagementClient.getUserByUsername(userInfo.getPreferredUsername());
            return response.getBody();
        } catch (Exception e) {
            log.debug("User not found: {}", userInfo.getPreferredUsername());
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

### 3.4 Create Token Response Model

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/model/TokenResponse.java`:

```java
package com.onified.ai.authentication_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("expires_in")
    private Integer expiresIn;
    
    @JsonProperty("scope")
    private String scope;
}
```

### 3.5 Create Social Auth Controller

Create `authentication-service/src/main/java/com/onified/ai/authentication_service/controller/SocialAuthController.java`:

```java
package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.model.*;
import com.onified.ai.authentication_service.service.SocialAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            log.info("Social login request received for provider: {}", request.getProvider());
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
            log.info("Social signup request received for provider: {}", request.getProvider());
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
        
        log.info("Initiating OAuth2 flow for provider: {}", provider);
        String authUrl = socialAuthService.buildOAuth2Url(provider, redirectUri, state);
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", authUrl)
            .build();
    }
}
```

## Step 4: Update Configuration

### 4.1 Update application.yml

Add to `authentication-service/src/main/resources/application.yml`:

```yaml
# Social Login Configuration
social:
  google:
    client-id: ${GOOGLE_CLIENT_ID:}
    client-secret: ${GOOGLE_CLIENT_SECRET:}
  linkedin:
    client-id: ${LINKEDIN_CLIENT_ID:}
    client-secret: ${LINKEDIN_CLIENT_SECRET:}

# WebClient Configuration
spring:
  webflux:
    base-path: /api
```

### 4.2 Update docker-compose.yml

Add environment variables to `docker-compose.yml`:

```yaml
authentication-service:
  environment:
    # Existing variables...
    - GOOGLE_CLIENT_ID=your-google-client-id
    - GOOGLE_CLIENT_SECRET=your-google-client-secret
    - LINKEDIN_CLIENT_ID=your-linkedin-client-id
    - LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret
```

## Step 5: Update User Management Service

### 5.1 Add Create User Request Model

Create `user-management-service/src/main/java/com/onified/ai/ums/model/CreateUserRequest.java`:

```java
package com.onified.ai.ums.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String password; // Optional for social login
}
```

### 5.2 Update User Service

Add method to `user-management-service/src/main/java/com/onified/ai/ums/service/UserService.java`:

```java
@Transactional
public UserDto createUserFromSocialLogin(CreateUserRequest request) {
    // Validate request
    if (request.getUsername() == null || request.getEmail() == null) {
        throw new IllegalArgumentException("Username and email are required");
    }
    
    // Check if user already exists
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
        throw new RuntimeException("User with username " + request.getUsername() + " already exists");
    }
    
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new RuntimeException("User with email " + request.getEmail() + " already exists");
    }
    
    // Create user entity
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(request.getRole())
        .enabled(true)
        .build();
    
    // Save user
    User savedUser = userRepository.save(user);
    
    // Convert to DTO and return
    return convertToDto(savedUser);
}
```

### 5.3 Update User Controller

Add endpoint to `user-management-service/src/main/java/com/onified/ai/ums/controller/UserController.java`:

```java
@PostMapping("/social")
public ResponseEntity<UserDto> createUserFromSocialLogin(@RequestBody CreateUserRequest request) {
    try {
        UserDto user = userService.createUserFromSocialLogin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    } catch (Exception e) {
        log.error("Failed to create user from social login", e);
        return ResponseEntity.badRequest().build();
    }
}
```

## Step 6: Testing

### 6.1 Build and Start Services

```bash
# Build authentication service
cd authentication-service
mvn clean install

# Build user management service
cd ../user-management-service
mvn clean install

# Start services
docker-compose up -d
```

### 6.2 Test OAuth2 Authorization URLs

```bash
# Test Google OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/google?redirect_uri=http://localhost:4200/auth/callback&state=test123"

# Test LinkedIn OAuth2 flow
curl "http://localhost:9083/api/auth/social/oauth2/authorize/linkedin?redirect_uri=http://localhost:4200/auth/callback&state=test123"
```

### 6.3 Test Social Login Endpoint

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

### 6.4 Test Social Signup Endpoint

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

## Step 7: Integration with Frontend

### 7.1 Update Frontend Auth Service

The frontend auth service methods we created earlier will now work with the backend:

```typescript
// These methods in auth.service.ts will call the backend endpoints
initiateSocialLogin(provider: SocialProvider)
handleSocialLoginCallback(code: string, state: string, provider: SocialProvider)
handleSocialSignup(code: string, state: string, provider: SocialProvider, role: string)
```

### 7.2 Test Complete Flow

1. **Start all services:**
   ```bash
   docker-compose up -d
   cd onified-web && ng serve
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
   - Verify successful signup and role assignment

## Troubleshooting

### Common Issues

1. **CORS Errors:**
   ```bash
   # Check CORS configuration in Keycloak
   # Ensure proper origins are configured
   ```

2. **Token Exchange Failures:**
   ```bash
   # Check client secrets
   # Verify authorization code hasn't expired
   # Check Keycloak logs
   docker-compose logs -f keycloak
   ```

3. **User Creation Failures:**
   ```bash
   # Check User Management Service logs
   docker-compose logs -f user-management-service
   
   # Check database connectivity
   docker-compose exec postgres psql -U postgres -d user_mgmt_db
   ```

### Debug Commands

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

## Next Steps

1. **Add error handling and validation**
2. **Implement token refresh logic**
3. **Add social account linking**
4. **Implement social login analytics**
5. **Add social login to other signup flows**
6. **Configure production settings** 