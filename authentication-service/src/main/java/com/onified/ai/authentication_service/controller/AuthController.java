package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.constants.MessageConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.service.KeycloakAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;

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

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                "Authentication Service is running"
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
