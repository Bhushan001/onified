package com.onified.ai.permission_registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for role operations")
public class RoleResponseDTO {
    
    @Schema(description = "Unique identifier for the role", 
            example = "ADMIN_ROLE")
    private String roleId;
    
    @Schema(description = "Human-readable display name for the role", 
            example = "Administrator Role")
    private String displayName;
    
    @Schema(description = "Application code that this role belongs to", 
            example = "USER_MGMT")
    private String appCode;
    
    @Schema(description = "Module code within the application", 
            example = "AUTH")
    private String moduleCode;
    
    @Schema(description = "Functional purpose of the role", 
            example = "USER_ADMINISTRATION")
    private String roleFunction;
    
    @Schema(description = "Whether the role is currently active", 
            example = "true")
    private Boolean isActive;
    
    @Schema(description = "Inheritance depth in the role hierarchy", 
            example = "0")
    private Integer inheritanceDepth;
    
    @Schema(description = "Whether this role can be customized by tenants", 
            example = "false")
    private Boolean tenantCustomizable;
    
    @Schema(description = "Timestamp when the role was created", 
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the role was last updated", 
            example = "2024-01-15T14:45:00")
    private LocalDateTime updatedAt;
}