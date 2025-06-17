package com.onified.ai.ums.dto;

import com.onified.ai.ums.ErrorConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    @NotBlank(message = ErrorConstants.ROLE_NAME_NOT_BLANK)
    private String roleId;
}
