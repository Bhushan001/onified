package com.onified.ai.authentication_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for user registration")
public class UserCreateRequest {
    
    @Schema(description = "Unique username for the user", 
            example = "john.doe", 
            required = true,
            minLength = 3,
            maxLength = 50)
    private String username;
    
    @Schema(description = "Secure password for the user", 
            example = "SecurePassword123!", 
            required = true,
            minLength = 8,
            maxLength = 100)
    private String password;
    
    @Schema(description = "Email address for the user", 
            example = "john.doe@example.com", 
            required = true,
            format = "email")
    private String email;
    
    @Schema(description = "First name of the user", 
            example = "John", 
            required = true,
            minLength = 1,
            maxLength = 50)
    private String firstName;
    
    @Schema(description = "Last name of the user", 
            example = "Doe", 
            required = true,
            minLength = 1,
            maxLength = 50)
    private String lastName;
    
    @Schema(description = "Set of roles assigned to the user", 
            example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;
} 