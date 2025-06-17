package com.onified.ai.appConfig.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleRequestDTO {
    private String appCode;
    private String moduleCode;
    private Boolean isActive;
}
