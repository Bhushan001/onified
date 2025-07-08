package com.onified.ai.authentication_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialSignupRequest {
    private String code;
    private String redirectUri;
    private String provider; // "google" or "linkedin"
    private String role; // Optional role for new user
    private String signupFlow; // "admin", "user", etc.
} 