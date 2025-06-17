package com.onified.ai.ums;

public final class ErrorConstants {

    private ErrorConstants() {
        // Private constructor to prevent instantiation
    }

    // User related errors
    public static final String USER_NOT_FOUND = "User with ID '%s' not found.";
    public static final String USER_NOT_FOUND_USERNAME = "User with username '%s' not found.";
    public static final String DUPLICATE_USERNAME = "Username '%s' already exists.";
    public static final String DUPLICATE_EMAIL = "Email '%s' already registered.";
    public static final String ROLE_NOT_FOUND_FOR_USER = "Role '%s' not found for user ID '%s'.";
    public static final String ATTRIBUTE_NOT_FOUND_FOR_USER = "Attribute '%s' not found for user ID '%s'.";

    // Validation errors (for DTOs)
    public static final String USERNAME_NOT_BLANK = "Username cannot be empty";
    public static final String USERNAME_SIZE_CONSTRAINT = "Username must be between 3 and 50 characters";
    public static final String PASSWORD_NOT_BLANK = "Password cannot be empty";
    public static final String PASSWORD_MIN_SIZE = "Password must be at least 8 characters long";
    public static final String EMAIL_NOT_BLANK = "Email cannot be empty";
    public static final String EMAIL_INVALID = "Email should be valid";
    public static final String FIRST_NAME_SIZE_CONSTRAINT = "First name cannot exceed 100 characters";
    public static final String LAST_NAME_SIZE_CONSTRAINT = "Last name cannot exceed 100 characters";
    public static final String ROLE_NAME_NOT_BLANK = "Role name cannot be empty";
    public static final String ATTRIBUTE_NAME_NOT_BLANK = "Attribute name cannot be empty";
    public static final String ATTRIBUTE_VALUE_NOT_BLANK = "Attribute value cannot be empty";

    // General error messages
    public static final String VALIDATION_FAILED = "Validation failed: ";
    public static final String UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred: ";
}