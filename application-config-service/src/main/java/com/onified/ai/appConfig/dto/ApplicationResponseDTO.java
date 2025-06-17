package com.onified.ai.appConfig.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponseDTO {
    private String appCode;
    private String displayName;
    private Boolean isActive;
}
