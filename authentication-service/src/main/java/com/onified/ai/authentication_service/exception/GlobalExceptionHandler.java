package com.onified.ai.authentication_service.exception;

import com.onified.ai.authentication_service.constants.ErrorConstants;
import com.onified.ai.authentication_service.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                String.valueOf(status.value()),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401 Unauthorized
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                String.valueOf(status.value()),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        CustomErrorResponse errorResponse = new CustomErrorResponse(
                String.valueOf(status.value()),
                ErrorConstants.VALIDATION_FAILED + validationErrors
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                String.valueOf(status.value()),
                ErrorConstants.UNEXPECTED_ERROR_OCCURRED + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
