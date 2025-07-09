package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserManagementFeignClient userManagementFeignClient;
    private final KeycloakUserService keycloakUserService;

    public UserResponse registerUser(UserCreateRequest request) {
        ResponseEntity<Object> response = userManagementFeignClient.createUser(request);
        if (response == null || response.getStatusCode().value() != 201) {
            throw new RuntimeException("User creation failed in user-management-service");
        }
        
        Object responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("User Management Service returned null response body");
        }
        
        // Extract UserResponse from response body
        UserResponse userResponse = extractUserResponseFromResponse(responseBody);
        if (userResponse == null) {
            throw new RuntimeException("Failed to extract user data from User Management Service response");
        }
        
        // Create user in Keycloak
        keycloakUserService.createUserInKeycloak(request);
        return userResponse;
    }

    public UserResponse registerUserWithRole(UserCreateRequest request, String role) {
        // Override roles with the provided role
        request.setRoles(Set.of(role));
        return registerUser(request);
    }

    private UserResponse extractUserResponseFromResponse(Object responseBody) {
        try {
            if (responseBody instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> responseMap = (java.util.Map<String, Object>) responseBody;
                Object body = responseMap.get("body");
                if (body instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) body;
                    
                    java.util.List<String> roles = (java.util.List<String>) userMap.get("roles");
                    
                    return UserResponse.builder()
                        .id(java.util.UUID.fromString((String) userMap.get("id")))
                        .username((String) userMap.get("username"))
                        .email((String) userMap.get("email"))
                        .firstName((String) userMap.get("firstName"))
                        .lastName((String) userMap.get("lastName"))
                        .status((String) userMap.get("status"))
                        .roles(new java.util.HashSet<>(roles))
                        .build();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse UserResponse from response body: " + e.getMessage());
        }
        return null;
    }
} 