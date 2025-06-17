package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionBundleUnitResponseDTO {
    private String pbuId;
    private String displayName;
    private String apiEndpoint;
    private String actionCode;
    private String scopeCode;
    private Boolean isActive;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
