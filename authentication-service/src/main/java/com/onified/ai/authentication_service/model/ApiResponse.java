package com.onified.ai.authentication_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized API response wrapper.
 * Contains status code, status message, and the actual response body.
 *
 * @param <T> The type of the data/body in the response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String status; // Corresponds to your "status" field (e.g., "SUCCESS", "FAILURE")
    private T body; // Corresponds to your "body" field (the actual data)
    private String error;

    /**
     * Sets the body as a String value, casting it to the generic type T.
     * This is useful when T is expected to be a String or compatible type.
     *
     * @param value The String value to set as the body.
     */
    @SuppressWarnings("unchecked")
    public void setBodyAsString(String value) {
        this.body = (T) value;
    }

    // Helper methods for convenience, aligning with direct constructor call pattern
    public static <T> ApiResponse<T> success(int statusCode, String statusMessage, T data) {
        return new ApiResponse<T>(statusCode, statusMessage, data, null);
    }

    public static <T> ApiResponse<T> success(int statusCode, String statusMessage) {
        return new ApiResponse<T>(statusCode, statusMessage, null, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<T>(400, "FAILURE", null, error);
    }

    // Constructor for backward compatibility
    public ApiResponse(int statusCode, String status, T body) {
        this.statusCode = statusCode;
        this.status = status;
        this.body = body;
        this.error = null;
    }
}
