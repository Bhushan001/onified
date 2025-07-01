package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.constants.MessageConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.service.KeycloakAuthService;
import com.onified.ai.authentication_service.service.RegistrationService;
import com.onified.ai.authentication_service.service.KeycloakUserService;
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
    private final KeycloakUserService keycloakUserService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = keycloakAuthService.authenticateUser(loginRequest);
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    loginResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("AUTHENTICATION_FAILED", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/create-platform-admin")
    public ResponseEntity<ApiResponse<Object>> createPlatformAdmin(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.Admin");
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("REGISTRATION_FAILED", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-tenant-admin")
    public ResponseEntity<ApiResponse<Object>> createTenantAdmin(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.TenantAdmin");
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("REGISTRATION_FAILED", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-platform-user")
    public ResponseEntity<ApiResponse<Object>> createPlatformUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = registrationService.registerUserWithRole(request, "PLATFORM.Management.User");
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("REGISTRATION_FAILED", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestParam String refreshToken) {
        try {
            LoginResponse loginResponse = keycloakAuthService.refreshToken(refreshToken);
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    loginResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("TOKEN_REFRESH_FAILED", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.UNAUTHORIZED.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<Object>> getUserAuthDetails(@PathVariable String username) {
        try {
            ApiResponse<UserAuthDetailsResponse> response = userManagementFeignClient.getUserAuthDetailsByUsername(username);
            if (response != null && response.getStatusCode() == HttpStatus.OK.value()) {
                return new ResponseEntity<>(new ApiResponse<>(
                    response.getStatusCode(),
                    response.getStatus(),
                    response.getBody()
                ), HttpStatus.OK);
            } else {
                throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, username));
            }
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                ApiResponse<Object> response = new ApiResponse<>(
                        fe.status(),
                        "ERROR",
                        customError
                );
                return new ResponseEntity<>(response, HttpStatus.valueOf(fe.status()));
            } catch (Exception ex) {
                CustomErrorResponse errorResponse = new CustomErrorResponse(String.valueOf(fe.status()), errorBody);
                ApiResponse<Object> response = new ApiResponse<>(
                        fe.status(),
                        "ERROR",
                        errorResponse
                );
                return new ResponseEntity<>(response, HttpStatus.valueOf(fe.status()));
            }
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("USER_NOT_FOUND", e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a user from Keycloak
     * This endpoint is called by the User Management Service when a user is deleted
     */
    @DeleteMapping("/keycloak/user/{username}")
    public ResponseEntity<ApiResponse<Object>> deleteUserFromKeycloak(@PathVariable String username) {
        try {
            boolean deleted = keycloakUserService.deleteUserFromKeycloak(username);
            
            if (deleted) {
                ApiResponse<Object> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        MessageConstants.STATUS_SUCCESS,
                        "User '" + username + "' deleted from Keycloak successfully"
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                CustomErrorResponse errorResponse = new CustomErrorResponse("USER_NOT_FOUND", "User '" + username + "' not found in Keycloak");
                ApiResponse<Object> response = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        "ERROR",
                        errorResponse
                );
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("DELETION_FAILED", "Failed to delete user from Keycloak: " + e.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "ERROR",
                    errorResponse
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
