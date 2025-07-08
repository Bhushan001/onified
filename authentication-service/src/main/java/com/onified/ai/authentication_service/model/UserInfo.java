package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String sub;
    private String preferredUsername;
    private String email;
    private String givenName;
    private String familyName;
    private String picture;
} 