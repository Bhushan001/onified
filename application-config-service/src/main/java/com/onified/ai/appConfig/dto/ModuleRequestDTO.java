package com.onified.ai.appConfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating or updating an application module")
public class ModuleRequestDTO {
    
    @Schema(description = "Application code that this module belongs to", 
            example = "APP001", 
            required = true,
            minLength = 1,
            maxLength = 50)
    private String appCode;
    
    @Schema(description = "Unique module code identifier", 
            example = "MOD001", 
            required = true,
            minLength = 1,
            maxLength = 50)
    private String moduleCode;
    
    @Schema(description = "Whether the module is active", 
            example = "true", 
            required = true)
    private Boolean isActive;
}
