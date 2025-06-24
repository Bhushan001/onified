package com.onified.ai.authentication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private String username;
    private String message;
    private String status;
    // You might add userId, roles, expiration date here as well
}
