package com.onified.ai.ums.dto;

import com.onified.ai.ums.ErrorConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Size(min = 3, max = 50, message = ErrorConstants.USERNAME_SIZE_CONSTRAINT)
    private String username;

    @Email(message = ErrorConstants.EMAIL_INVALID)
    private String email;

    @Size(min = 8, message = ErrorConstants.PASSWORD_MIN_SIZE)
    private String password;

    @Size(max = 100, message = ErrorConstants.FIRST_NAME_SIZE_CONSTRAINT)
    private String firstName;

    @Size(max = 100, message = ErrorConstants.LAST_NAME_SIZE_CONSTRAINT)
    private String lastName;
}
