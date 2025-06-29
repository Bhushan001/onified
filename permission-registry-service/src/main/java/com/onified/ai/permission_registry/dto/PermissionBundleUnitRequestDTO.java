package com.onified.ai.permission_registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating a Permission Bundle Unit (PBU)")
public class PermissionBundleUnitRequestDTO {
    
    @Schema(description = "Unique identifier for the PBU", 
            example = "USER_CREATE_PBU", 
            required = true)
    private String pbuId;
    
    @Schema(description = "Human-readable display name for the PBU", 
            example = "User Creation Permission", 
            required = true)
    private String displayName;
    
    @Schema(description = "API endpoint that this PBU controls", 
            example = "/api/users", 
            required = true)
    private String apiEndpoint;
    
    @Schema(description = "Action code that this PBU represents", 
            example = "CREATE", 
            required = true)
    private String actionCode;
    
    @Schema(description = "Scope code for the resource being accessed", 
            example = "USER", 
            required = true)
    private String scopeCode;
    
    @Schema(description = "Whether the PBU is currently active", 
            example = "true", 
            defaultValue = "true")
    private Boolean isActive;
    
    @Schema(description = "Version number of the PBU", 
            example = "1.0", 
            defaultValue = "1.0")
    private String version;
}