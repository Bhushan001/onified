package com.onified.ai.appConfig.exception;

import com.onified.ai.appConfig.model.ApiResponse;
import com.onified.ai.appConfig.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
        ApiResponse<CustomErrorResponse> apiResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleConflictException(ConflictException ex, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse("DATA_CONFLICT", ex.getMessage());
        ApiResponse<CustomErrorResponse> apiResponse = new ApiResponse<>(HttpStatus.CONFLICT.value(), "CONFLICT", errorResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse("BAD_REQUEST", ex.getMessage());
        ApiResponse<CustomErrorResponse> apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "BAD_REQUEST", errorResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleGlobalException(Exception ex, WebRequest request) {
        CustomErrorResponse errorResponse = new CustomErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred: " + ex.getMessage());
        ApiResponse<CustomErrorResponse> apiResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", errorResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

