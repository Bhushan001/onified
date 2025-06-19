package com.onified.ai.ums.dto;

import com.onified.ai.ums.constants.ErrorConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeRequest {
    @NotBlank(message = ErrorConstants.ATTRIBUTE_NAME_NOT_BLANK)
    private String attributeName;

    @NotBlank(message = ErrorConstants.ATTRIBUTE_VALUE_NOT_BLANK)
    private String attributeValue;
}
