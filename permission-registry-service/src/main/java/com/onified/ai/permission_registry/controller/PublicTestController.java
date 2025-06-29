package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Test Endpoints", description = "Public endpoints for testing and health checks")
public class PublicTestController {

    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = "Simple health check to verify the service is running"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Health Response",
                    value = """
                    {
                      "statusCode": 200,
                      "status": "SUCCESS",
                      "body": {
                        "service": "permission-registry-service",
                        "status": "UP",
                        "timestamp": "2024-01-15T10:30:00"
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("service", "permission-registry-service");
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", healthData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/info")
    @Operation(
        summary = "Service information",
        description = "Returns basic information about the permission registry service"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Service information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Info Response",
                    value = """
                    {
                      "statusCode": 200,
                      "status": "SUCCESS",
                      "body": {
                        "name": "Permission Registry Service",
                        "version": "1.0.0",
                        "description": "Comprehensive RBAC/ABAC Framework for Onified.ai",
                        "features": [
                          "Role Management",
                          "Permission Bundle Units",
                          "Scopes and Actions",
                          "Constraints (General and Field)",
                          "Contextual Behaviors",
                          "Role Inheritance"
                        ]
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> infoData = new HashMap<>();
        infoData.put("name", "Permission Registry Service");
        infoData.put("version", "1.0.0");
        infoData.put("description", "Comprehensive RBAC/ABAC Framework for Onified.ai");
        infoData.put("features", new String[]{
            "Role Management",
            "Permission Bundle Units", 
            "Scopes and Actions",
            "Constraints (General and Field)",
            "Contextual Behaviors",
            "Role Inheritance"
        });
        
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", infoData);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/echo")
    @Operation(
        summary = "Echo endpoint",
        description = "Echoes back the provided data for testing purposes"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Data echoed back successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<Object>> echo(@RequestBody Object data) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
} 