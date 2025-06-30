package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.constants.MessageConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.service.KeycloakAuthService;
import com.onified.ai.authentication_service.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.onified.ai.authentication_service.model.CustomErrorResponse;
import feign.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
import com.onified.ai.authentication_service.dto.UserAuthDetailsResponse;
import com.onified.ai.authentication_service.exception.UserNotFoundException;
import com.onified.ai.authentication_service.constants.ErrorConstants;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;
    private final RegistrationService registrationService;
    private final UserManagementFeignClient userManagementFeignClient;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = keycloakAuthService.authenticateUser(request);
            loginResponse.setStatus("SUCCESS");
            loginResponse.setMessage("Login successful");
            
            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    loginResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LoginResponse errorResponse = LoginResponse.builder()
                    .status("ERROR")
                    .message(e.getMessage())
                    .build();
            
            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestParam String refreshToken) {
        try {
            LoginResponse loginResponse = keycloakAuthService.refreshToken(refreshToken);
            loginResponse.setStatus("SUCCESS");
            loginResponse.setMessage("Token refreshed successfully");
            
            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    loginResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LoginResponse errorResponse = LoginResponse.builder()
                    .status("ERROR")
                    .message(e.getMessage())
                    .build();
            
            ApiResponse<LoginResponse> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = registrationService.registerUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create-platform-admin")
    public ResponseEntity<?> createPlatformAdmin(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.Admin");
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create-tenant-admin")
    public ResponseEntity<?> createTenantAdmin(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.TenantAdmin");
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create-platform-user")
    public ResponseEntity<?> createPlatformUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.User");
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                "Logout successful"
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                "Authentication Service is running"
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<ApiResponse<UserAuthDetailsResponse>> getUserProfile(@PathVariable String username) {
        try {
            ApiResponse<UserAuthDetailsResponse> umsResponse = userManagementFeignClient.getUserAuthDetailsByUsername(username);
            if (umsResponse == null || umsResponse.getStatusCode() != HttpStatus.OK.value() || umsResponse.getBody() == null) {
                throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, username));
            }
            return new ResponseEntity<>(umsResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<UserAuthDetailsResponse> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                "ERROR",
                null
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
