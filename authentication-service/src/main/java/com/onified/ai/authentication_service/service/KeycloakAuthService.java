package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
import com.onified.ai.authentication_service.dto.UserAuthDetailsResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthService {

    private final RestTemplate restTemplate;
    private final UserManagementFeignClient userManagementFeignClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Prepare token request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
            tokenRequest.add("grant_type", "password");
            tokenRequest.add("client_id", clientId);
            tokenRequest.add("client_secret", clientSecret);
            tokenRequest.add("username", loginRequest.getUsername());
            tokenRequest.add("password", loginRequest.getPassword());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequest, headers);

            // Call Keycloak token endpoint
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);

            if (response != null && response.containsKey("access_token")) {
                // Fetch user profile from UMS
                ApiResponse<UserAuthDetailsResponse> umsResponse = userManagementFeignClient.getUserAuthDetailsByUsername(loginRequest.getUsername());
                UserAuthDetailsResponse userProfile = umsResponse != null ? umsResponse.getBody() : null;
                return LoginResponse.builder()
                        .accessToken((String) response.get("access_token"))
                        .refreshToken((String) response.get("refresh_token"))
                        .tokenType((String) response.get("token_type"))
                        .expiresIn((Integer) response.get("expires_in"))
                        .username(loginRequest.getUsername())
                        .userProfile(userProfile)
                        .build();
            } else {
                throw new RuntimeException("Authentication failed: Invalid credentials");
            }

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
            tokenRequest.add("grant_type", "refresh_token");
            tokenRequest.add("client_id", clientId);
            tokenRequest.add("client_secret", clientSecret);
            tokenRequest.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(tokenRequest, headers);

            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);

            if (response != null && response.containsKey("access_token")) {
                return LoginResponse.builder()
                        .accessToken((String) response.get("access_token"))
                        .refreshToken((String) response.get("refresh_token"))
                        .tokenType((String) response.get("token_type"))
                        .expiresIn((Integer) response.get("expires_in"))
                        .build();
            } else {
                throw new RuntimeException("Token refresh failed");
            }

        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
} 