package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextualBehaviorRequestDTO {
    private String behaviorId;
    private String behaviorCode;
    private String displayName;
    private String conditionLogic; // JSON string
    private Boolean isActive;
}