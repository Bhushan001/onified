package com.onified.ai.authentication_service.controller;

import com.onified.ai.authentication_service.dto.SocialLoginRequest;
import com.onified.ai.authentication_service.dto.SocialSignupRequest;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.model.SocialLoginResponse;
import com.onified.ai.authentication_service.service.SocialAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/social")
@Slf4j
public class SocialAuthController {

    @Autowired
    private SocialAuthService socialAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
            @RequestBody SocialLoginRequest request) {
        try {
            log.info("Social login request received for provider: {}", request.getProvider());
            SocialLoginResponse response = socialAuthService.handleSocialLogin(request);
            return ResponseEntity.ok(new ApiResponse<SocialLoginResponse>(200, "SUCCESS", response));
        } catch (Exception e) {
            log.error("Social login failed", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Social login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialSignup(
            @RequestBody SocialSignupRequest request) {
        try {
            log.info("Social signup request received for provider: {}", request.getProvider());
            SocialLoginResponse response = socialAuthService.handleSocialSignup(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<SocialLoginResponse>(201, "SUCCESS", response));
        } catch (Exception e) {
            log.error("Social signup failed", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Social signup failed: " + e.getMessage()));
        }
    }

    @GetMapping("/oauth2/authorize/{provider}")
    public ResponseEntity<Void> initiateOAuth2Flow(
            @PathVariable String provider,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam String state) {
        
        log.info("Initiating OAuth2 flow for provider: {}", provider);
        String authUrl = socialAuthService.buildOAuth2Url(provider, redirectUri, state);
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", authUrl)
            .build();
    }

    @GetMapping("/test-connection")
    public ResponseEntity<ApiResponse<String>> testKeycloakConnection() {
        try {
            socialAuthService.testKeycloakConnection();
            return ResponseEntity.ok(new ApiResponse<String>(200, "SUCCESS", "Keycloak connection successful"));
        } catch (Exception e) {
            log.error("Keycloak connection test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Keycloak connection failed: " + e.getMessage()));
        }
    }
} 