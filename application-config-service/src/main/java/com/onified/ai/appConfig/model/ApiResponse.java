package com.onified.ai.appConfig.model;

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
@Schema(description = "Standardized API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "HTTP status code", 
            example = "200")
    private int statusCode;
    
    @Schema(description = "Status message", 
            example = "SUCCESS")
    private String status;
    
    @Schema(description = "Response body containing the actual data")
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