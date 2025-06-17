package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO can be reused for General, Field, and Contextual Role overrides
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleConstraintOverrideResponseDTO {
    private String roleId;
    private String constraintId; // For General and Field Constraints
    private String behaviorId; // For Contextual Behaviors
    private String overrideType; // e.g., "GENERAL_CONSTRAINT", "FIELD_CONSTRAINT", "CONTEXTUAL_BEHAVIOR"
    private String status; // e.g., "OVERRIDDEN", "REMOVED"
}
