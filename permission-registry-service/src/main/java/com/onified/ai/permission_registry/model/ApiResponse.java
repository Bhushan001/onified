package com.onified.ai.permission_registry.model;

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
    private String status;
    private T body;

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
}
