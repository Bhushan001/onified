package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserManagementFeignClient userManagementFeignClient;
    private final KeycloakUserService keycloakUserService;

    public UserResponse registerUser(UserCreateRequest request) {
        ApiResponse<UserResponse> response = userManagementFeignClient.createUser(request);
        if (response == null || response.getStatusCode() != 201 || response.getBody() == null) {
            throw new RuntimeException("User creation failed in user-management-service");
        }
        // Create user in Keycloak
        keycloakUserService.createUserInKeycloak(request);
        return response.getBody();
    }

    public UserResponse registerUserWithRole(UserCreateRequest request, String role) {
        // Override roles with the provided role
        request.setRoles(Set.of(role));
        return registerUser(request);
    }
} 