package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeRequestDTO {
    private String scopeCode;
    private String displayName;
    private String description;
    private Boolean isActive;
}