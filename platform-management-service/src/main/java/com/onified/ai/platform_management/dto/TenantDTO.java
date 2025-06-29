package com.onified.ai.platform_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Tenant configuration and metadata")
public class TenantDTO {
    
    @Schema(description = "Unique identifier for the tenant", example = "tenant-001")
    private String tenantId;
    
    @Schema(description = "Display name of the tenant", example = "Acme Corporation")
    private String name;
    
    @Schema(description = "Current status of the tenant", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"})
    private String status;
    
    @Schema(description = "Additional configuration in JSON format", example = "{\"timezone\": \"UTC\", \"locale\": \"en-US\"}")
    private String extraConfig;
    
    @Schema(description = "Timestamp when the tenant was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the tenant was last updated")
    private LocalDateTime updatedAt;
    
    @Schema(description = "User who created the tenant", example = "admin@onified.com")
    private String createdBy;
    
    @Schema(description = "User who last updated the tenant", example = "admin@onified.com")
    private String updatedBy;
} 