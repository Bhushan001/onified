package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.SocialLoginRequest;
import com.onified.ai.authentication_service.dto.SocialSignupRequest;
import com.onified.ai.authentication_service.dto.UserDto;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.dto.UserAuthDetailsResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.model.SocialLoginResponse;
import com.onified.ai.authentication_service.model.TokenResponse;
import com.onified.ai.authentication_service.model.UserInfo;
import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
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
    private UserManagementFeignClient userManagementClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final WebClient webClient = WebClient.builder().build();

    // Role mapping for different signup flows
    private static final Map<String, String> SIGNUP_FLOW_ROLE_MAP = Map.of(
        "platform-admin", "PLATFORM.Management.Admin",
        "tenant-admin", "PLATFORM.Management.TenantAdmin", 
        "user", "PLATFORM.Management.User"
    );

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
            // Convert UserDto to UserResponse for consistency
            UserResponse userResponse = UserResponse.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .status(existingUser.getStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(existingUser.getUpdatedAt())
                .roles(existingUser.getRoles())
                .build();
            return createLoginResponse(userResponse, accessToken, false);
        } else {
            // User doesn't exist - this should be a signup
            throw new RuntimeException("User not found. Please use signup endpoint.");
        }
    }

    public SocialLoginResponse handleSocialSignup(SocialSignupRequest request) {
        log.info("Handling social signup for provider: {} with flow: {}", request.getProvider(), request.getSignupFlow());
        
        // Exchange authorization code for tokens
        String accessToken = exchangeCodeForToken(request.getCode(), request.getRedirectUri());
        
        // Get user info from Keycloak
        UserInfo userInfo = getUserInfoFromKeycloak(accessToken);
        
        // Determine and validate role
        String role = determineUserRole(request);
        log.info("Assigned role for social signup: {}", role);
        
        // Create user in User Management Service
        UserCreateRequest createUserRequest = UserCreateRequest.builder()
            .username(userInfo.getPreferredUsername())
            .email(userInfo.getEmail())
            .firstName(userInfo.getGivenName())
            .lastName(userInfo.getFamilyName())
            .roles(java.util.Set.of(role))
            .build();
        
        ApiResponse<UserResponse> userResponse = userManagementClient.createUser(createUserRequest);
        UserResponse newUser = userResponse.getBody();
        
        if (newUser == null) {
            throw new RuntimeException("Failed to create user in User Management Service");
        }
        
        // Note: For social login, user is already created in Keycloak through identity provider
        // No need to create user in Keycloak separately
        
        return createLoginResponse(newUser, accessToken, true);
    }

    /**
     * Determines the appropriate role for social signup based on signup flow and provider
     */
    private String determineUserRole(SocialSignupRequest request) {
        // Priority: explicit role > signup flow mapping > default role
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            // Validate explicit role
            if (isValidRole(request.getRole())) {
                return request.getRole();
            } else {
                log.warn("Invalid explicit role provided: {}. Using signup flow mapping.", request.getRole());
            }
        }
        
        // Use signup flow mapping
        if (request.getSignupFlow() != null && SIGNUP_FLOW_ROLE_MAP.containsKey(request.getSignupFlow())) {
            return SIGNUP_FLOW_ROLE_MAP.get(request.getSignupFlow());
        }
        
        // Default role for social signup
        log.info("No valid role found, using default role for social signup");
        return "PLATFORM.Management.User";
    }

    /**
     * Validates if the provided role is valid
     */
    private boolean isValidRole(String role) {
        return SIGNUP_FLOW_ROLE_MAP.containsValue(role) || 
               role.startsWith("PLATFORM.Management.") ||
               role.startsWith("TENANT.") ||
               role.startsWith("APPLICATION.");
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
            ApiResponse<UserAuthDetailsResponse> response = userManagementClient.getUserAuthDetailsByUsername(userInfo.getPreferredUsername());
            UserAuthDetailsResponse userAuthDetails = response.getBody();
            if (userAuthDetails != null) {
                return UserDto.builder()
                    .id(userAuthDetails.getId())
                    .username(userAuthDetails.getUsername())
                    .roles(new java.util.HashSet<>(userAuthDetails.getRoles()))
                    .build();
            }
            return null;
        } catch (Exception e) {
            log.debug("User not found: {}", userInfo.getPreferredUsername());
            return null;
        }
    }

    private SocialLoginResponse createLoginResponse(UserResponse user, String accessToken, boolean isNewUser) {
        UserDto userDto = UserDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .roles(user.getRoles())
            .build();
            
        return SocialLoginResponse.builder()
            .accessToken(accessToken)
            .username(user.getUsername())
            .user(userDto)
            .isNewUser(isNewUser)
            .build();
    }
} 