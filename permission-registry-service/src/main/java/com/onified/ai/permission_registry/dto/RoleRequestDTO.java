package com.onified.ai.permission_registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating a role")
public class RoleRequestDTO {
    
    @Schema(description = "Unique identifier for the role", 
            example = "ADMIN_ROLE", 
            required = true)
    private String roleId;
    
    @Schema(description = "Human-readable display name for the role", 
            example = "Administrator Role", 
            required = true)
    private String displayName;
    
    @Schema(description = "Application code that this role belongs to", 
            example = "USER_MGMT", 
            required = true)
    private String appCode;
    
    @Schema(description = "Module code within the application", 
            example = "AUTH", 
            required = true)
    private String moduleCode;
    
    @Schema(description = "Functional purpose of the role", 
            example = "USER_ADMINISTRATION", 
            required = true)
    private String roleFunction;
    
    @Schema(description = "Whether the role is currently active", 
            example = "true", 
            defaultValue = "true")
    private Boolean isActive;
    
    @Schema(description = "Inheritance depth in the role hierarchy (calculated by service)", 
            example = "0", 
            defaultValue = "0")
    private Integer inheritanceDepth;
    
    @Schema(description = "Whether this role can be customized by tenants", 
            example = "false", 
            defaultValue = "false")
    private Boolean tenantCustomizable;
}