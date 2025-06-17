package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralConstraintRequestDTO {
    private String constraintId;
    private String constraintName;
    private String tableName;
    private String columnName;
    private String valueType;
    private String tableValue; // JSON string
    private String customValue; // JSON string
    private String ruleLogic; // JSON string
    private Boolean isActive;
}