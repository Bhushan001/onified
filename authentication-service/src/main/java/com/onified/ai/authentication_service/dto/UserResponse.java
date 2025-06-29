package com.onified.ai.authentication_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for user data")
public class UserResponse {
    
    @Schema(description = "Unique identifier for the user", 
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Username of the user", 
            example = "john.doe")
    private String username;
    
    @Schema(description = "Email address of the user", 
            example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "First name of the user", 
            example = "John")
    private String firstName;
    
    @Schema(description = "Last name of the user", 
            example = "Doe")
    private String lastName;
    
    @Schema(description = "Current status of the user", 
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;
    
    @Schema(description = "Timestamp when the user was created", 
            example = "2024-01-15T10:30:00Z")
    private Instant createdAt;
    
    @Schema(description = "Timestamp when the user was last updated", 
            example = "2024-01-15T10:30:00Z")
    private Instant updatedAt;
    
    @Schema(description = "Set of roles assigned to the user", 
            example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;
    // Add attributes if needed
} 