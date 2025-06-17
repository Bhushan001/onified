package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInheritanceRequestDTO {
    private String parentRoleId;
    private String childRoleId;
    private String approvedBy; // Optional, can be null for system/auto-approved
    // approvalDate is set by service
}