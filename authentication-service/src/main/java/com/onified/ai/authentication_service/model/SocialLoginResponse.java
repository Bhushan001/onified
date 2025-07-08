package com.onified.ai.authentication_service.model;

import com.onified.ai.authentication_service.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private UserDto user;
    private boolean isNewUser;
} 