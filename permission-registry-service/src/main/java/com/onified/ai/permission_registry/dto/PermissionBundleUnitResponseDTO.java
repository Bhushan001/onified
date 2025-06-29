package com.onified.ai.permission_registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for Permission Bundle Unit (PBU) operations")
public class PermissionBundleUnitResponseDTO {
    
    @Schema(description = "Unique identifier for the PBU", 
            example = "USER_CREATE_PBU")
    private String pbuId;
    
    @Schema(description = "Human-readable display name for the PBU", 
            example = "User Creation Permission")
    private String displayName;
    
    @Schema(description = "API endpoint that this PBU controls", 
            example = "/api/users")
    private String apiEndpoint;
    
    @Schema(description = "Action code that this PBU represents", 
            example = "CREATE")
    private String actionCode;
    
    @Schema(description = "Scope code for the resource being accessed", 
            example = "USER")
    private String scopeCode;
    
    @Schema(description = "Whether the PBU is currently active", 
            example = "true")
    private Boolean isActive;
    
    @Schema(description = "Version number of the PBU", 
            example = "1.0")
    private String version;
    
    @Schema(description = "Timestamp when the PBU was created", 
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the PBU was last updated", 
            example = "2024-01-15T14:45:00")
    private LocalDateTime updatedAt;
}
