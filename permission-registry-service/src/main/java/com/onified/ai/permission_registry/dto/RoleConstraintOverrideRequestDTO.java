package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO can be reused for General, Field, and Contextual Role overrides
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleConstraintOverrideRequestDTO {
    private String roleId;
    private String constraintId; // For General and Field Constraints
    private String behaviorId; // For Contextual Behaviors (use one or the other based on context)
}
