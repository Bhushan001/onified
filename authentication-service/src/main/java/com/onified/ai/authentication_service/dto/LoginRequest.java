package com.onified.ai.authentication_service.dto;

import com.onified.ai.authentication_service.constants.ErrorConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = ErrorConstants.USERNAME_NOT_BLANK)
    private String username;

    @NotBlank(message = ErrorConstants.PASSWORD_NOT_BLANK)
    private String password;
}