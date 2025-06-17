package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    private String roleId;
    private String displayName;
    private String appCode;
    private String moduleCode;
    private String roleFunction;
    private Boolean isActive;
    private Integer inheritanceDepth;
    private Boolean tenantCustomizable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}