package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextualBehaviorResponseDTO {
    private Integer id; // Database generated ID
    private String behaviorId;
    private String behaviorCode;
    private String displayName;
    private String conditionLogic; // JSON string
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}