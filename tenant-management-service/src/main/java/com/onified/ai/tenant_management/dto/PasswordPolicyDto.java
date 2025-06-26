package com.onified.ai.tenant_management.dto;

import lombok.Data;

@Data
public class PasswordPolicyDto {
    private Long id;
    private String policyName;
    private String description;
    private int minLength = 10;
    private int maxPasswordAge = 90;
    private int minPasswordAge = 0;
    private int passwordHistory = 4;
    private boolean requireUppercase = true;
    private boolean requireLowercase = true;
    private boolean requireNumber = true;
    private boolean requireSpecial = true;
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    private String bannedPatterns = "password,123456,qwerty,admin";
    private boolean isActive = true;
    private boolean isDefault = false;
} 