package com.onified.ai.authentication_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for authentication")
public class LoginResponse {
    
    @Schema(description = "JWT access token for API authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "JWT refresh token for obtaining new access tokens", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "Type of token", 
            example = "Bearer")
    private String tokenType;
    
    @Schema(description = "Token expiration time in seconds", 
            example = "3600")
    private Integer expiresIn;
    
    @Schema(description = "Authenticated username", 
            example = "john.doe")
    private String username;
    
    @Schema(description = "Response message", 
            example = "Login successful")
    private String message;
    
    @Schema(description = "Response status", 
            example = "SUCCESS",
            allowableValues = {"SUCCESS", "ERROR"})
    private String status;
    // You might add userId, roles, expiration date here as well
}
