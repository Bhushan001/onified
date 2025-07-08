package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialSignupRequest {
    private String provider;
    private String code;
    private String state;
    private String redirectUri;
    private String role;
    private String signupFlow; // e.g., "platform-admin", "tenant-admin", "user"
    private UserInfo userInfo;
} 