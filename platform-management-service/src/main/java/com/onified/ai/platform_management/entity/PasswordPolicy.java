package com.onified.ai.platform_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "password_policy")
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordPolicy extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_name", nullable = false, unique = true)
    private String policyName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "min_length", nullable = false)
    private int minLength = 10;
    
    @Column(name = "max_password_age")
    private int maxPasswordAge = 90;
    
    @Column(name = "min_password_age")
    private int minPasswordAge = 0;
    
    @Column(name = "password_history")
    private int passwordHistory = 4;
    
    @Column(name = "require_uppercase", nullable = false)
    private boolean requireUppercase = true;
    
    @Column(name = "require_lowercase", nullable = false)
    private boolean requireLowercase = true;
    
    @Column(name = "require_number", nullable = false)
    private boolean requireNumber = true;
    
    @Column(name = "require_special", nullable = false)
    private boolean requireSpecial = true;
    
    @Column(name = "initial_password_format")
    private String initialPasswordFormat = "[FirstInitial][LastInitial][Random]";
    
    @Column(name = "banned_patterns", columnDefinition = "TEXT")
    private String bannedPatterns = "password,123456,qwerty,admin";
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
} 