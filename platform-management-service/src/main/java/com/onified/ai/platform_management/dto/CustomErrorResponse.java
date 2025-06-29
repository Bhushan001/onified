package com.onified.ai.platform_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response structure")
public class CustomErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error status message", example = "ERROR")
    private String message;
    
    @Schema(description = "Detailed error message", example = "Invalid request parameters")
    private String details;
    
    @Schema(description = "Additional error information or stack trace")
    private String additionalInfo;
} 