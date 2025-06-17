package com.onified.ai.appConfig.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Custom error response structure, now with Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {
    private String errorCode;
    private String errorMessage;
}
