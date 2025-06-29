package com.onified.ai.authentication_service.dto;

import com.onified.ai.authentication_service.constants.ErrorConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for user authentication")
public class LoginRequest {
    
    @Schema(description = "Username for authentication", 
            example = "john.doe", 
            required = true,
            minLength = 1,
            maxLength = 50)
    @NotBlank(message = ErrorConstants.USERNAME_NOT_BLANK)
    private String username;

    @Schema(description = "Password for authentication", 
            example = "securePassword123", 
            required = true,
            minLength = 6,
            maxLength = 100)
    @NotBlank(message = ErrorConstants.PASSWORD_NOT_BLANK)
    private String password;
}