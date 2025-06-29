package com.onified.ai.appConfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for application data")
public class ApplicationResponseDTO {
    
    @Schema(description = "Unique application code identifier", 
            example = "APP001")
    private String appCode;
    
    @Schema(description = "Display name for the application", 
            example = "User Management System")
    private String displayName;
    
    @Schema(description = "Whether the application is active", 
            example = "true")
    private Boolean isActive;
}
