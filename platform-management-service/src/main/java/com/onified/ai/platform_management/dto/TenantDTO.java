package com.onified.ai.platform_management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TenantDTO {
    private String tenantId;
    private String name;
    private String status;
    private String extraConfig;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
} 