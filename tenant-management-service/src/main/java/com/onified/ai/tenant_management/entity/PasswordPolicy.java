package com.onified.ai.tenant_management.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import java.util.List;

@Data
@Embeddable
public class PasswordPolicy {
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
} 