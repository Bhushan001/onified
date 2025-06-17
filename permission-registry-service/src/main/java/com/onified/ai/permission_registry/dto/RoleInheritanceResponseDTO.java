package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInheritanceResponseDTO {
    private String parentRoleId;
    private String childRoleId;
    private String approvedBy;
    private LocalDateTime approvalDate;
}