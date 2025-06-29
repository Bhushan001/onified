package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.constants.MessageConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.service.KeycloakAuthService;
import com.onified.ai.authentication_service.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.onified.ai.authentication_service.model.CustomErrorResponse;
import feign.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and authorization")
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user with username and password, returning JWT tokens for API access."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Authentication successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.authentication_service.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 200,
                        "status": "SUCCESS",
                        "body": {
                            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600,
                            "username": "john.doe",
                            "message": "Login successful",
                            "status": "SUCCESS"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest request) {
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
    @Operation(
        summary = "Refresh access token",
        description = "Refreshes an expired access token using a valid refresh token."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Token refreshed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.authentication_service.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid refresh token"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Parameter(description = "Refresh token", required = true, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestParam String refreshToken) {
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
    @Operation(
        summary = "Register new user",
        description = "Registers a new user account with the provided details. The user will be created in Keycloak and the user management service."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.authentication_service.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 201,
                        "status": "SUCCESS",
                        "body": {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "username": "john.doe",
                            "email": "john.doe@example.com",
                            "firstName": "John",
                            "lastName": "Doe",
                            "status": "ACTIVE",
                            "createdAt": "2024-01-15T10:30:00Z",
                            "updatedAt": "2024-01-15T10:30:00Z",
                            "roles": ["USER"]
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - User already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = registrationService.registerUser(request);
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                // fallback: return raw error body as message
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            CustomErrorResponse error = new CustomErrorResponse(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "Internal server error"
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Returns the health status of the authentication service."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Service is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.authentication_service.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 200,
                        "status": "SUCCESS",
                        "body": "Authentication Service is running"
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Service is unhealthy")
    })
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                "Authentication Service is running"
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
