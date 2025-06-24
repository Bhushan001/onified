package com.onified.ai.platform_management.exception;

import com.onified.ai.platform_management.dto.CustomErrorResponse;
import com.onified.ai.platform_management.constants.MessageConstants;
import com.onified.ai.platform_management.constants.ErrorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TenantNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleTenantNotFound(TenantNotFoundException ex, WebRequest request) {
        CustomErrorResponse error = new CustomErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                MessageConstants.STATUS_ERROR,
                ErrorConstants.TENANT_NOT_FOUND,
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        CustomErrorResponse error = new CustomErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                MessageConstants.STATUS_ERROR,
                ErrorConstants.INTERNAL_SERVER_ERROR,
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 