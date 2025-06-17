package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {
    private String roleId;
    private String displayName;
    private String appCode;
    private String moduleCode;
    private String roleFunction;
    private Boolean isActive;
    private Integer inheritanceDepth; // Should usually be calculated by service, but included for initial creation/override
    private Boolean tenantCustomizable;
}