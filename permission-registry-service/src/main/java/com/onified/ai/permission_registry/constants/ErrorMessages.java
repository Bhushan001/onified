package com.onified.ai.permission_registry.constants;

public final class ErrorMessages {

    // General Reusable Messages
    public static final String RESOURCE_NOT_FOUND = "Resource with ID %s not found.";
    public static final String CONFLICT_ALREADY_EXISTS = "Resource with identifier %s already exists.";
    public static final String BAD_REQUEST_INVALID_DATA = "Invalid request data: %s";
    public static final String RELATED_RESOURCE_NOT_FOUND = "Related resource %s (ID: %s) not found.";


    // Action specific
    public static final String ACTION_ALREADY_EXISTS = "Action with code %s already exists.";
    public static final String ACTION_NOT_FOUND = "Action with code %s not found.";

    // Scope specific
    public static final String SCOPE_ALREADY_EXISTS = "Scope with code %s already exists.";
    public static final String SCOPE_NOT_FOUND = "Scope with code %s not found.";

    // General Constraint specific
    public static final String GENERAL_CONSTRAINT_ALREADY_EXISTS = "General Constraint with ID %s already exists.";
    public static final String GENERAL_CONSTRAINT_NOT_FOUND = "General Constraint with ID %s not found.";
    public static final String GENERAL_CONSTRAINT_INVALID_JSON_RULE = "Invalid JSON rule_logic for General Constraint %s: %s";

    // Field Constraint specific
    public static final String FIELD_CONSTRAINT_ALREADY_EXISTS = "Field Constraint with ID %s already exists.";
    public static final String FIELD_CONSTRAINT_NOT_FOUND = "Field Constraint with ID %s not found.";
    public static final String FIELD_CONSTRAINT_INVALID_JSON_CONDITION = "Invalid JSON condition_logic for Field Constraint %s: %s";

    // Contextual Behavior specific
    public static final String CONTEXTUAL_BEHAVIOR_ALREADY_EXISTS = "Contextual Behavior with ID %s already exists.";
    public static final String CONTEXTUAL_BEHAVIOR_NOT_FOUND = "Contextual Behavior with ID %s not found.";
    public static final String CONTEXTUAL_BEHAVIOR_INVALID_JSON_CONDITION = "Invalid JSON condition_logic for Contextual Behavior %s: %s";


    // PBU (Permission Bundle Unit) specific
    public static final String PBU_ALREADY_EXISTS = "Permission Bundle Unit with ID %s already exists.";
    public static final String PBU_NOT_FOUND = "Permission Bundle Unit with ID %s not found.";
    public static final String PBU_ACTION_SCOPE_INVALID = "Associated Action code '%s' or Scope code '%s' for PBU not found or inactive.";
    public static final String PBU_CONSTRAINT_ASSOCIATION_ALREADY_EXISTS = "Association between PBU %s and Constraint %s already exists.";
    public static final String PBU_CONSTRAINT_ASSOCIATION_NOT_FOUND = "Association between PBU %s and Constraint %s not found.";
    public static final String PBU_BEHAVIOR_ASSOCIATION_ALREADY_EXISTS = "Association between PBU %s and Behavior %s already exists.";
    public static final String PBU_BEHAVIOR_ASSOCIATION_NOT_FOUND = "Association between PBU %s and Behavior %s not found.";
    public static final String PBU_NAMING_CONVENTION_VIOLATION = "PBU ID %s does not follow naming convention PBU_{RESOURCE}_{ACTION}_{SCOPE}.";

    // Role specific
    public static final String ROLE_ALREADY_EXISTS = "Role with ID %s already exists.";
    public static final String ROLE_NOT_FOUND = "Role with ID %s not found.";
    public static final String ROLE_APP_MODULE_INVALID = "Associated Application '%s' or Module '%s' for role not found or inactive.";
    public static final String ROLE_NAMING_CONVENTION_VIOLATION = "Role ID %s does not follow naming convention {APP}.{MODULE}.{ROLE_FUNCTION}.";
    public static final String SENSITIVE_ROLE_APPROVAL_REQUIRED = "Approval is required for sensitive role %s.";

    // Role Inheritance specific
    public static final String ROLE_INHERITANCE_ALREADY_EXISTS = "Role inheritance from parent %s to child %s already exists.";
    public static final String ROLE_INHERITANCE_NOT_FOUND = "Role inheritance from parent %s to child %s not found.";
    public static final String ROLE_INHERITANCE_CYCLIC = "Cyclic inheritance detected: Adding %s as child of %s would create a cycle.";
    public static final String ROLE_INHERITANCE_DEPTH_EXCEEDED = "Maximum inheritance depth (3) exceeded for child role %s.";
    public static final String ROLE_INHERITANCE_SAME_ROLE = "A role cannot inherit from itself: %s.";
    public static final String ROLE_INHERITANCE_SOD_VIOLATION = "Segregation of Duties violation: Role %s cannot inherit from %s.";

    // Role Constraint Override specific
    public static final String ROLE_CONSTRAINT_OVERRIDE_ALREADY_EXISTS = "Role %s already has an override for constraint %s.";
    public static final String ROLE_CONSTRAINT_OVERRIDE_NOT_FOUND = "Role %s does not have an override for constraint %s.";
    public static final String ROLE_BEHAVIOR_OVERRIDE_ALREADY_EXISTS = "Role %s already has an override for behavior %s.";
    public static final String ROLE_BEHAVIOR_OVERRIDE_NOT_FOUND = "Role %s does not have an override for behavior %s.";
    public static final String INVALID_ROLE_NAMING_CONVENTION = "Role ID %s does not follow the required naming convention.";

    private ErrorMessages() {
        // Private constructor to prevent instantiation
    }
}

