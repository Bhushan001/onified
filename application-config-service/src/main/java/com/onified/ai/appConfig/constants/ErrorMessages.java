package com.onified.ai.appConfig.constants;

public final class ErrorMessages {

    public static final String APPLICATION_ALREADY_EXISTS = "Application with appCode %s already exists.";
    public static final String APPLICATION_NOT_FOUND = "Application with appCode %s not found.";

    public static final String MODULE_ALREADY_EXISTS = "Module with appCode %s and moduleCode %s already exists.";
    public static final String MODULE_APP_NOT_FOUND = "Associated application with appCode %s does not exist.";
    public static final String MODULE_NOT_FOUND = "Module with ID %d not found.";

    private ErrorMessages() {
    }
}

