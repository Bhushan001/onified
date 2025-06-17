package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldConstraintResponseDTO {
    private String constraintId;
    private String entityName;
    private String fieldName;
    private String accessType;
    private String conditionLogic; // JSON string
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}