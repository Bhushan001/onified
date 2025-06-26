package com.onified.ai.platform_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {
    private int statusCode;
    private String status;
    private String message;
    private String details;
} 