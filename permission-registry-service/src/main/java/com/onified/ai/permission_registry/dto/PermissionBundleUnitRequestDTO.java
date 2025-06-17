package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionBundleUnitRequestDTO {
    private String pbuId;
    private String displayName;
    private String apiEndpoint;
    private String actionCode;
    private String scopeCode;
    private Boolean isActive;
    private String version;
}