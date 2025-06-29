package com.onified.ai.appConfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for application module data")
public class ModuleResponseDTO {
    
    @Schema(description = "Unique module identifier", 
            example = "1")
    private Integer moduleId;
    
    @Schema(description = "Application code that this module belongs to", 
            example = "APP001")
    private String appCode;
    
    @Schema(description = "Unique module code identifier", 
            example = "MOD001")
    private String moduleCode;
    
    @Schema(description = "Whether the module is active", 
            example = "true")
    private Boolean isActive;
}
