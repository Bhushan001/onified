package com.onified.ai.authentication_service.constants;

public final class ErrorConstants {

    private ErrorConstants() {}

    public static final String USERNAME_NOT_BLANK = "Username cannot be empty";
    public static final String PASSWORD_NOT_BLANK = "Password cannot be empty";
    public static final String USER_NOT_FOUND_USERNAME = "User with username '%s' not found.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String UMS_COMMUNICATION_ERROR = "Failed to communicate with User Management Service: ";

    public static final String VALIDATION_FAILED = "Validation failed: ";
    public static final String UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred: ";
}
