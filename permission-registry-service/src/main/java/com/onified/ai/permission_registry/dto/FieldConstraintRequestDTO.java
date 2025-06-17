package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldConstraintRequestDTO {
    private String constraintId;
    private String entityName;
    private String fieldName;
    private String accessType;
    private String conditionLogic; // JSON string
    private Boolean isActive;
}