package com.onified.ai.appConfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating an application")
public class ApplicationRequestDTO {
    
    @Schema(description = "Unique application code identifier", 
            example = "APP001", 
            required = true,
            minLength = 1,
            maxLength = 50)
    private String appCode;
    
    @Schema(description = "Display name for the application", 
            example = "User Management System", 
            required = true,
            minLength = 1,
            maxLength = 100)
    private String displayName;
    
    @Schema(description = "Whether the application is active", 
            example = "true", 
            required = true)
    private Boolean isActive;
}

