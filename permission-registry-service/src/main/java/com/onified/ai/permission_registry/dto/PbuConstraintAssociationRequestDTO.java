package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO can be reused for General, Field, and Contextual PBU associations
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PbuConstraintAssociationRequestDTO {
    private String pbuId;
    private String constraintId; // For General and Field Constraints
    private String behaviorId; // For Contextual Behaviors (use one or the other based on context)
}

