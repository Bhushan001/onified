package com.onified.ai.permission_registry.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Standardized API response wrapper for all endpoints")
public class ApiResponse<T> {
    
    @Schema(description = "HTTP status code", 
            example = "200", 
            required = true)
    private int statusCode;
    
    @Schema(description = "Status message indicating success or error type", 
            example = "SUCCESS", 
            required = true,
            allowableValues = {"SUCCESS", "CONFLICT", "NOT_FOUND", "BAD_REQUEST", "UNAUTHORIZED", "FORBIDDEN", "INTERNAL_SERVER_ERROR"})
    private String status;
    
    @Schema(description = "Response body containing the actual data or error details", 
            required = true)
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
