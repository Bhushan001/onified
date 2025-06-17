package com.onified.ai.ums.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized API response wrapper.
 * Contains status code, status message, and the actual response body.
 *
 * @param <T> The type of the data/body in the response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String status; // Corresponds to your "status" field (e.g., "SUCCESS", "FAILURE")
    private T body; // Corresponds to your "body" field (the actual data)

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
        return new ApiResponse<>(statusCode, statusMessage, data);
    }

    public static <T> ApiResponse<T> success(int statusCode, String statusMessage) {
        return new ApiResponse<>(statusCode, statusMessage, null);
    }
}