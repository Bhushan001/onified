package com.onified.ai.appConfig.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponseDTO {
    private Integer moduleId;
    private String appCode;
    private String moduleCode;
    private Boolean isActive;
}
