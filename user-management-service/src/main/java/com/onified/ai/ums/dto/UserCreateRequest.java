package com.onified.ai.ums.dto;

import com.onified.ai.ums.ErrorConstants;
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
public class UserCreateRequest {

    @NotBlank(message = ErrorConstants.USERNAME_NOT_BLANK)
    @Size(min = 3, max = 50, message = ErrorConstants.USERNAME_SIZE_CONSTRAINT)
    private String username;

    @NotBlank(message = ErrorConstants.PASSWORD_NOT_BLANK)
    @Size(min = 8, message = ErrorConstants.PASSWORD_MIN_SIZE)
    private String password;

    @Email(message = ErrorConstants.EMAIL_INVALID)
    @NotBlank(message = ErrorConstants.EMAIL_NOT_BLANK)
    private String email;

    @Size(max = 100, message = ErrorConstants.FIRST_NAME_SIZE_CONSTRAINT)
    private String firstName;

    @Size(max = 100, message = ErrorConstants.LAST_NAME_SIZE_CONSTRAINT)
    private String lastName;

    private Set<String> roles;
}