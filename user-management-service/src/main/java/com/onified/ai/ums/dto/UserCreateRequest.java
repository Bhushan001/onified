package com.onified.ai.ums.dto;

import com.onified.ai.ums.constants.ErrorConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating a new user")
public class UserCreateRequest {

    @Schema(description = "Unique username for the user (3-50 characters)", 
            example = "john.doe", 
            required = true,
            minLength = 3,
            maxLength = 50)
    @NotBlank(message = ErrorConstants.USERNAME_NOT_BLANK)
    @Size(min = 3, max = 50, message = ErrorConstants.USERNAME_SIZE_CONSTRAINT)
    private String username;

    @Schema(description = "User password (minimum 8 characters)", 
            example = "SecurePass123!", 
            required = true,
            minLength = 8)
    @NotBlank(message = ErrorConstants.PASSWORD_NOT_BLANK)
    @Size(min = 8, message = ErrorConstants.PASSWORD_MIN_SIZE)
    private String password;

    @Schema(description = "User email address", 
            example = "john.doe@example.com", 
            required = true,
            format = "email")
    @Email(message = ErrorConstants.EMAIL_INVALID)
    @NotBlank(message = ErrorConstants.EMAIL_NOT_BLANK)
    private String email;

    @Schema(description = "User's first name (maximum 100 characters)", 
            example = "John",
            maxLength = 100)
    @Size(max = 100, message = ErrorConstants.FIRST_NAME_SIZE_CONSTRAINT)
    private String firstName;

    @Schema(description = "User's last name (maximum 100 characters)", 
            example = "Doe",
            maxLength = 100)
    @Size(max = 100, message = ErrorConstants.LAST_NAME_SIZE_CONSTRAINT)
    private String lastName;

    @Schema(description = "Set of role names to assign to the user", 
            example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;
}