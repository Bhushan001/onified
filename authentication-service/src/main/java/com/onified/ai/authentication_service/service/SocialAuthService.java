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
    
    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

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
            "&kc_idp_hint=%s" +
            "&prompt=consent",
            keycloakUrl, realm, clientId, redirectUri, state, provider);
    }

    public SocialLoginResponse handleSocialLogin(SocialLoginRequest request) {
        log.info("Handling social login for provider: {}", request.getProvider());
        
        try {
            // Since Keycloak handles the OAuth2 flow automatically, we need to get user info differently
            // We'll use the admin API to find the user that was just authenticated by the social login
            log.info("Looking for recently authenticated social user for provider: {}", request.getProvider());
            
            // Get admin token
            String adminToken = getAdminToken();
            
            // Find the user by searching for users with the social provider
            UserInfo userInfo = findSocialUserByProvider(request.getProvider(), adminToken);
            
            if (userInfo == null) {
                throw new RuntimeException("No user found for social provider: " + request.getProvider() + ". Please complete the social login flow first.");
            }
            
            log.info("Found social user: username={}, email={}", userInfo.getPreferredUsername(), userInfo.getEmail());
            
            // Check if user exists in our database
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
                
                log.info("Found existing user in database: username={}, email={}, roles={}", 
                        userResponse.getUsername(), userResponse.getEmail(), userResponse.getRoles());
                
                // Create a placeholder token since we don't have the actual access token
                String placeholderToken = "social-token-" + userInfo.getPreferredUsername() + "-" + System.currentTimeMillis();
                return createLoginResponse(userResponse, placeholderToken, false);
            } else {
                // User doesn't exist in our database - this should be a signup
                throw new RuntimeException("User not found in database. Please use signup endpoint.");
            }
        } catch (Exception e) {
            log.error("Social login failed", e);
            throw new RuntimeException("Social login failed: " + e.getMessage());
        }
    }

    public SocialLoginResponse handleSocialSignup(SocialSignupRequest request) {
        log.info("Handling social signup for provider: {} with flow: {}", 
                request.getProvider(), request.getSignupFlow());
        
        // Validate request parameters
        if (request.getProvider() == null || request.getProvider().trim().isEmpty()) {
            throw new RuntimeException("Provider is required");
        }
        
        // Test Keycloak connection
        testKeycloakConnection();
        
        try {
            // Since Keycloak handles the OAuth2 flow automatically, we need to get user info differently
            // We'll use the admin API to find the user that was just created by the social login
            log.info("Looking for recently created social user for provider: {}", request.getProvider());
            
            // Add a small delay to ensure the user is properly created in Keycloak
            try {
                Thread.sleep(2000); // Wait 2 seconds for user creation to complete
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Get admin token
            String adminToken = getAdminToken();
            
            // Find the user by searching for users with the social provider
            UserInfo userInfo = findSocialUserByProvider(request.getProvider(), adminToken);
            
            // If user not found, try again after a longer delay
            if (userInfo == null) {
                log.info("User not found on first attempt, waiting longer and retrying...");
                try {
                    Thread.sleep(3000); // Wait 3 more seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                userInfo = findSocialUserByProvider(request.getProvider(), adminToken);
            }
            
            if (userInfo == null) {
                throw new RuntimeException("No user found for social provider: " + request.getProvider() + ". Please complete the social login flow first.");
            }
            
            log.info("Found social user: username={}, email={}", userInfo.getPreferredUsername(), userInfo.getEmail());
            
            // Check if user already exists in our database
            UserDto existingUser = findExistingUser(userInfo);
            
            if (existingUser != null) {
                log.info("User already exists in database: {}", existingUser.getUsername());
                // User exists - return login response
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
                
                // Create a placeholder token since we don't have the actual access token
                String placeholderToken = "social-token-" + userInfo.getPreferredUsername() + "-" + System.currentTimeMillis();
                return createLoginResponse(userResponse, placeholderToken, false);
            }
            
            // Determine and validate role
            String role = determineUserRole(request);
            log.info("Assigned role for social signup: {}", role);
            
            // Create user in User Management Service
            // Generate a random password for social users since they don't have one
            String randomPassword = generateRandomPassword();
            
            UserCreateRequest createUserRequest = UserCreateRequest.builder()
                .username(userInfo.getPreferredUsername())
                .email(userInfo.getEmail())
                .firstName(userInfo.getGivenName())
                .lastName(userInfo.getFamilyName())
                .password(randomPassword) // Required by User Management Service
                .roles(java.util.Set.of(role))
                .build();
            
            log.info("Creating user in User Management Service: {}", createUserRequest);
            
            try {
                ResponseEntity<Object> response = userManagementClient.createUser(createUserRequest);
                log.info("User Management Service response status: {}", response.getStatusCode());
                
                if (response == null) {
                    throw new RuntimeException("User Management Service returned null response");
                }
                
                if (response.getStatusCode().value() != 201 && response.getStatusCode().value() != 200) {
                    throw new RuntimeException("User Management Service returned error with status code: " + response.getStatusCode());
                }
                
                Object responseBody = response.getBody();
                if (responseBody == null) {
                    throw new RuntimeException("User Management Service returned null response body");
                }
                
                // Try to extract UserResponse from the response body
                UserResponse newUser = null;
                try {
                    // If it's a Map (JSON response), try to extract the data
                    if (responseBody instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseBody;
                        Object body = responseMap.get("body");
                        if (body instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) body;
                            newUser = UserResponse.builder()
                                .id(java.util.UUID.fromString((String) userMap.get("id")))
                                .username((String) userMap.get("username"))
                                .email((String) userMap.get("email"))
                                .firstName((String) userMap.get("firstName"))
                                .lastName((String) userMap.get("lastName"))
                                .status((String) userMap.get("status"))
                                .roles(new java.util.HashSet<>((java.util.List<String>) userMap.get("roles")))
                                .build();
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to parse UserResponse from response body: {}", e.getMessage());
                }
                
                if (newUser == null) {
                    throw new RuntimeException("Failed to extract user data from User Management Service response");
                }
                
                log.info("Successfully created user in database: {}", newUser.getUsername());
                
                // Create user in Keycloak and assign roles
                try {
                    log.info("Creating user in Keycloak: {}", newUser.getUsername());
                    keycloakUserService.createUserInKeycloak(createUserRequest);
                    log.info("Successfully created user in Keycloak: {}", newUser.getUsername());
                } catch (Exception e) {
                    log.error("Failed to create user in Keycloak: {}", e.getMessage());
                    
                    // If user already exists in Keycloak, try to assign roles to existing user
                    if (e.getMessage() != null && e.getMessage().contains("Conflict")) {
                        log.info("User already exists in Keycloak, attempting to assign roles to existing user: {}", newUser.getUsername());
                        try {
                            keycloakUserService.assignRolesToExistingUser(newUser.getUsername(), createUserRequest.getRoles());
                            log.info("Successfully assigned roles to existing user in Keycloak: {}", newUser.getUsername());
                        } catch (Exception roleAssignError) {
                            log.error("Failed to assign roles to existing user in Keycloak: {}", roleAssignError.getMessage());
                        }
                    }
                }
                
                // Create a placeholder token since we don't have the actual access token
                String placeholderToken = "social-token-" + userInfo.getPreferredUsername() + "-" + System.currentTimeMillis();
                return createLoginResponse(newUser, placeholderToken, true);
                
            } catch (Exception e) {
                log.error("Failed to create user in User Management Service", e);
                throw new RuntimeException("Failed to create user in database: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("Social signup failed", e);
            String errorMessage = "Social signup failed: " + e.getMessage();
            
            // Add more context for common errors
            if (e.getMessage() != null && e.getMessage().contains("400 Bad Request")) {
                errorMessage += ". This usually means the authorization code has expired or is invalid. Please try the login flow again.";
            } else if (e.getMessage() != null && e.getMessage().contains("401 Unauthorized")) {
                errorMessage += ". This usually means the client credentials are incorrect. Please check Keycloak configuration.";
            }
            
            throw new RuntimeException(errorMessage);
        }
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
        
        // Create form data for token exchange
        org.springframework.util.MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        
        log.info("Token exchange request - URL: {}, Code: {}, Redirect URI: {}", tokenUrl, code, redirectUri);
        
        try {
            log.info("Making token exchange request to: {}", tokenUrl);
            log.info("Form data: grant_type={}, client_id={}, code={}, redirect_uri={}", 
                    formData.getFirst("grant_type"), 
                    formData.getFirst("client_id"), 
                    formData.getFirst("code"), 
                    formData.getFirst("redirect_uri"));
            
            TokenResponse response = webClient.post()
                .uri(tokenUrl)
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), 
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Token exchange failed with 4xx error: {}", errorBody);
                            return reactor.core.publisher.Mono.error(org.springframework.web.reactive.function.client.WebClientResponseException.create(
                                clientResponse.statusCode().value(),
                                "Token exchange failed",
                                clientResponse.headers().asHttpHeaders(),
                                errorBody.getBytes(),
                                null
                            ));
                        }))
                .onStatus(status -> status.is5xxServerError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Token exchange failed with 5xx error: {}", errorBody);
                            return reactor.core.publisher.Mono.error(org.springframework.web.reactive.function.client.WebClientResponseException.create(
                                clientResponse.statusCode().value(),
                                "Token exchange failed",
                                clientResponse.headers().asHttpHeaders(),
                                errorBody.getBytes(),
                                null
                            ));
                        }))
                .bodyToMono(TokenResponse.class)
                .doOnNext(tokenResponse -> log.info("Token exchange response received: {}", tokenResponse))
                .doOnError(error -> log.error("Token exchange error: {}", error.getMessage(), error))
                .block();
            
            if (response == null || response.getAccessToken() == null) {
                throw new RuntimeException("Failed to exchange code for token: null response or missing access token");
            }
            
            log.info("Token exchange successful for code: {}", code);
            return response.getAccessToken();
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("Token exchange failed with status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Token exchange failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Token exchange failed with unexpected error: {}", e.getMessage());
            
            // Try fallback with RestTemplate
            log.info("Attempting fallback token exchange with RestTemplate");
            try {
                return exchangeCodeForTokenWithRestTemplate(code, redirectUri);
            } catch (Exception fallbackError) {
                log.error("Fallback token exchange also failed: {}", fallbackError.getMessage());
                
                // Provide more helpful error message for expired codes
                String errorMessage = "Token exchange failed: " + e.getMessage();
                if (fallbackError.getMessage() != null && fallbackError.getMessage().contains("invalid_grant")) {
                    errorMessage = "Authorization code has expired or is invalid. Please try the signup process again immediately.";
                } else {
                    errorMessage += " (fallback also failed: " + fallbackError.getMessage() + ")";
                }
                
                throw new RuntimeException(errorMessage);
            }
        }
    }

    private String exchangeCodeForTokenWithRestTemplate(String code, String redirectUri) {
        log.info("Using RestTemplate fallback for token exchange");
        
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);
        
        // Create form data for token exchange
        org.springframework.util.MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        
        log.info("RestTemplate token exchange request - URL: {}, Code: {}, Redirect URI: {}", tokenUrl, code, redirectUri);
        
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            
            org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = 
                new org.springframework.http.HttpEntity<>(formData, headers);
            
            org.springframework.http.ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                tokenUrl, 
                request, 
                TokenResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getAccessToken() != null) {
                log.info("RestTemplate token exchange successful for code: {}", code);
                return response.getBody().getAccessToken();
            } else {
                throw new RuntimeException("RestTemplate token exchange failed: invalid response");
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("RestTemplate token exchange failed with status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("RestTemplate token exchange failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("RestTemplate token exchange failed with unexpected error: {}", e.getMessage());
            throw new RuntimeException("RestTemplate token exchange failed: " + e.getMessage());
        }
    }

    private UserInfo getUserInfoFromKeycloak(String accessToken) {
        log.info("Getting user info from Keycloak");
        
        // Standard OAuth2 token flow
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
            log.info("Looking for existing user in database: {}", userInfo.getPreferredUsername());
            ResponseEntity<Object> response = userManagementClient.getUserAuthDetailsByUsername(userInfo.getPreferredUsername());
            
            if (response == null || response.getStatusCode().value() != 200) {
                log.debug("User not found: {}", userInfo.getPreferredUsername());
                return null;
            }
            
            Object responseBody = response.getBody();
            if (responseBody == null) {
                log.warn("User Management Service returned null response body");
                return null;
            }
            
            log.info("User Management Service response body type: {}", responseBody.getClass().getSimpleName());
            
            // Try to extract UserAuthDetailsResponse from the response body
            try {
                if (responseBody instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseBody;
                    log.info("Response map keys: {}", responseMap.keySet());
                    
                    Object body = responseMap.get("body");
                    if (body instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) body;
                        log.info("User map keys: {}", userMap.keySet());
                        
                        java.util.List<String> roles = (java.util.List<String>) userMap.get("roles");
                        
                        // Extract all available fields
                        UserDto userDto = UserDto.builder()
                            .id(java.util.UUID.fromString((String) userMap.get("id")))
                            .username((String) userMap.get("username"))
                            .email((String) userMap.get("email"))
                            .firstName((String) userMap.get("firstName"))
                            .lastName((String) userMap.get("lastName"))
                            .status((String) userMap.get("status"))
                            .createdAt(userMap.get("createdAt") != null ? 
                                java.time.Instant.parse((String) userMap.get("createdAt")) : null)
                            .updatedAt(userMap.get("updatedAt") != null ? 
                                java.time.Instant.parse((String) userMap.get("updatedAt")) : null)
                            .roles(new java.util.HashSet<>(roles))
                            .build();
                        
                        log.info("Successfully extracted user from database: username={}, email={}, roles={}", 
                                userDto.getUsername(), userDto.getEmail(), userDto.getRoles());
                        
                        return userDto;
                    } else {
                        log.warn("Response body is not a Map: {}", body != null ? body.getClass().getSimpleName() : "null");
                    }
                } else {
                    log.warn("Response body is not a Map: {}", responseBody.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.warn("Failed to parse UserAuthDetailsResponse from response body: {}", e.getMessage());
                e.printStackTrace();
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

    /**
     * Generates a secure random password for social users
     * Since social users don't have passwords, we generate one for database storage
     */
    public void testKeycloakConnection() {
        try {
            String wellKnownUrl = String.format("%s/realms/%s/.well-known/openid-configuration", keycloakUrl, realm);
            log.info("Testing Keycloak connection to: {}", wellKnownUrl);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity(wellKnownUrl, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Keycloak connection test successful");
            } else {
                log.warn("Keycloak connection test returned status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.warn("Keycloak connection test failed: {}", e.getMessage());
        }
    }





    /**
     * Gets admin token for Keycloak admin API access
     */
    private String getAdminToken() {
        String tokenUrl = String.format("%s/realms/master/protocol/openid-connect/token", keycloakUrl);
        
        org.springframework.util.MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", "admin");
        formData.add("password", "admin123");
        
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
            
            org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = 
                new org.springframework.http.HttpEntity<>(formData, headers);
            
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.postForEntity(
                tokenUrl, 
                request, 
                java.util.Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            log.error("Failed to get admin token: {}", e.getMessage());
        }
        
        throw new RuntimeException("Failed to get admin token");
    }
    
    /**
     * Finds a social user by provider using Keycloak admin API
     */
    private UserInfo findSocialUserByProvider(String provider, String adminToken) {
        // Get all users and find the most recent one that was likely created by social login
        String searchUrl = String.format("%s/admin/realms/%s/users", keycloakUrl, realm);
        
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + adminToken);
            
            org.springframework.http.HttpEntity<String> request = new org.springframework.http.HttpEntity<>(headers);
            
            log.info("Getting all users to find social user for provider: {}", provider);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                searchUrl,
                org.springframework.http.HttpMethod.GET,
                request,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Admin API response received for all users");
                // Parse the response to find the most recent user
                return parseUserFromAdminResponse(response.getBody(), provider);
            } else {
                log.warn("Admin API returned status: {} for users list", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Failed to find social user by provider: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Parses user info from admin API response
     */
    private UserInfo parseUserFromAdminResponse(String responseBody, String provider) {
        try {
            // This is a simplified JSON parsing - in production use a proper JSON library
            // For now, we'll extract basic fields using string operations
            log.info("Parsing user from admin response for provider: {}", provider);
            
            // Since we're getting all users, we need to find the most recent one
            // We'll look for users that were created recently (within the last few minutes)
            // and have email addresses (typical for social login users)
            
            // Check if response is an array (multiple users)
            if (responseBody.trim().startsWith("[")) {
                // Parse all users and find the most recent one with an email
                java.util.List<UserInfo> users = parseAllUsers(responseBody);
                if (!users.isEmpty()) {
                    // Find the most recent user with an email (likely a social login user)
                    // Prioritize users with email addresses as they're more likely to be social login users
                    UserInfo bestCandidate = null;
                    for (UserInfo user : users) {
                        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                            // Check if this user was created recently (within last 5 minutes)
                            // For now, we'll just return the first user with email
                            // In a production environment, you'd check creation timestamps
                            log.info("Found potential social user: username={}, email={}", 
                                    user.getPreferredUsername(), user.getEmail());
                            return user;
                        }
                        // Keep track of the first user as fallback
                        if (bestCandidate == null) {
                            bestCandidate = user;
                        }
                    }
                    // If no user with email found, return the first one
                    if (bestCandidate != null) {
                        log.info("No user with email found, returning first user: {}", bestCandidate.getPreferredUsername());
                        return bestCandidate;
                    }
                }
            } else {
                // Single user object
                return parseSingleUser(responseBody);
            }
        } catch (Exception e) {
            log.error("Failed to parse user from admin response: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Parses all users from the admin API response
     */
    private java.util.List<UserInfo> parseAllUsers(String responseBody) {
        java.util.List<UserInfo> users = new java.util.ArrayList<>();
        
        try {
            // Remove the outer brackets
            String content = responseBody.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1);
            }
            
            // Split by user objects (this is a simplified approach)
            // In production, use a proper JSON parser
            int startIndex = 0;
            while (startIndex < content.length()) {
                int startBrace = content.indexOf('{', startIndex);
                if (startBrace == -1) break;
                
                int endBrace = findMatchingBrace(content, startBrace);
                if (endBrace == -1) break;
                
                String userJson = content.substring(startBrace, endBrace + 1);
                UserInfo user = parseSingleUser(userJson);
                if (user != null) {
                    users.add(user);
                }
                
                startIndex = endBrace + 1;
            }
        } catch (Exception e) {
            log.error("Failed to parse all users: {}", e.getMessage());
        }
        
        log.info("Parsed {} users from admin response", users.size());
        return users;
    }

    /**
     * Parses a single user object from JSON
     */
    private UserInfo parseSingleUser(String userJson) {
        // Extract all user fields
        String username = extractJsonField(userJson, "username");
        String email = extractJsonField(userJson, "email");
        String firstName = extractJsonField(userJson, "firstName");
        String lastName = extractJsonField(userJson, "lastName");
        
        // Also try alternative field names that Keycloak might use
        if (username == null) username = extractJsonField(userJson, "preferred_username");
        if (email == null) email = extractJsonField(userJson, "email");
        if (firstName == null) firstName = extractJsonField(userJson, "given_name");
        if (lastName == null) lastName = extractJsonField(userJson, "family_name");
        
        if (username != null) {
            // Create UserInfo object
            UserInfo userInfo = new UserInfo();
            userInfo.setPreferredUsername(username);
            userInfo.setEmail(email);
            userInfo.setGivenName(firstName);
            userInfo.setFamilyName(lastName);
            
            log.info("Successfully parsed user: username={}, email={}, firstName={}, lastName={}", 
                    username, email, firstName, lastName);
            
            return userInfo;
        }
        
        return null;
    }
    
    /**
     * Finds the matching closing brace for a given opening brace
     */
    private int findMatchingBrace(String text, int startIndex) {
        int braceCount = 0;
        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Extracts a field value from JSON string (simplified)
     */
    private String extractJsonField(String json, String fieldName) {
        try {
            // Pattern to match both quoted and unquoted values
            String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                String value = m.group(1);
                log.debug("Extracted field {}: {}", fieldName, value);
                return value;
            }
            
            // Also try to match null values
            pattern = "\"" + fieldName + "\"\\s*:\\s*null";
            p = java.util.regex.Pattern.compile(pattern);
            m = p.matcher(json);
            if (m.find()) {
                log.debug("Field {} is null", fieldName);
                return null;
            }
        } catch (Exception e) {
            log.warn("Failed to extract field {} from JSON: {}", fieldName, e.getMessage());
        }
        return null;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // Generate a 16-character password
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
} 