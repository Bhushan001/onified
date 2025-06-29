package com.onified.ai.platform_management.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "password_policy")
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Password policy configuration for security requirements")
public class PasswordPolicy extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the password policy")
    private Long id;
    
    @Column(name = "policy_name", nullable = false, unique = true)
    @Schema(description = "Name of the password policy", example = "Standard Policy", required = true)
    private String policyName;
    
    @Column(name = "description")
    @Schema(description = "Description of the password policy", example = "Standard password policy for all users")
    private String description;
    
    @Column(name = "min_length", nullable = false)
    @Schema(description = "Minimum password length", example = "10", defaultValue = "10")
    private int minLength = 10;
    
    @Column(name = "max_password_age")
    @Schema(description = "Maximum password age in days", example = "90", defaultValue = "90")
    private int maxPasswordAge = 90;
    
    @Column(name = "min_password_age")
    @Schema(description = "Minimum password age in days", example = "0", defaultValue = "0")
    private int minPasswordAge = 0;
    
    @Column(name = "password_history")
    @Schema(description = "Number of previous passwords to remember", example = "4", defaultValue = "4")
    private int passwordHistory = 4;
    
    @Column(name = "require_uppercase", nullable = false)
    @Schema(description = "Whether uppercase letters are required", example = "true", defaultValue = "true")
    private boolean requireUppercase = true;
    
    @Column(name = "require_lowercase", nullable = false)
    @Schema(description = "Whether lowercase letters are required", example = "true", defaultValue = "true")
    private boolean requireLowercase = true;
    
    @Column(name = "require_number", nullable = false)
    @Schema(description = "Whether numbers are required", example = "true", defaultValue = "true")
    private boolean requireNumber = true;
    
    @Column(name = "require_special", nullable = false)
    @Schema(description = "Whether special characters are required", example = "true", defaultValue = "true")
    private boolean requireSpecial = true;
    
    @Column(name = "initial_password_format")
    @Schema(description = "Format for generating initial passwords", example = "[FirstInitial][LastInitial][Random]", defaultValue = "[FirstInitial][LastInitial][Random]")
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    
    @Column(name = "banned_patterns", columnDefinition = "TEXT")
    @Schema(description = "Comma-separated list of banned password patterns", example = "password,123456,qwerty,admin", defaultValue = "password,123456,qwerty,admin")
    private String bannedPatterns = "password,123456,qwerty,admin";
    
    @Column(name = "is_active", nullable = false)
    @Schema(description = "Whether the policy is active", example = "true", defaultValue = "true")
    private boolean isActive = true;
    
    @Column(name = "is_default", nullable = false)
    @Schema(description = "Whether this is the default policy", example = "false", defaultValue = "false")
    private boolean isDefault = false;
} 