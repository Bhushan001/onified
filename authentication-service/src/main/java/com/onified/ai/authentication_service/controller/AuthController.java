package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.constants.MessageConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.loginUser(request);
        ApiResponse<LoginResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                loginResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
