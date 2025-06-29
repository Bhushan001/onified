package com.onified.ai.ums.dto;

import com.onified.ai.ums.entity.User;
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
@Schema(description = "Response DTO for user operations")
public class UserResponse {
    
    @Schema(description = "Unique identifier for the user", 
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Unique username for the user", 
            example = "john.doe")
    private String username;
    
    @Schema(description = "User email address", 
            example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User's first name", 
            example = "John")
    private String firstName;
    
    @Schema(description = "User's last name", 
            example = "Doe")
    private String lastName;
    
    @Schema(description = "Current status of the user account", 
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private User.UserStatus status;
    
    @Schema(description = "Timestamp when the user was created", 
            example = "2024-01-15T10:30:00Z")
    private Instant createdAt;
    
    @Schema(description = "Timestamp when the user was last updated", 
            example = "2024-01-15T14:45:00Z")
    private Instant updatedAt;
    
    @Schema(description = "Set of role names assigned to the user", 
            example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;
    
    @Schema(description = "Set of custom attributes for the user")
    private Set<UserAttributeResponse> attributes;

    // Inner DTO for UserAttribute
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User attribute key-value pair")
    public static class UserAttributeResponse {
        
        @Schema(description = "Name of the attribute", 
                example = "department")
        private String attributeName;
        
        @Schema(description = "Value of the attribute", 
                example = "Engineering")
        private String attributeValue;
    }
}
