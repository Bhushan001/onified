package com.onified.ai.appConfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Endpoints", description = "Public endpoints for testing and health checks")
public class PublicTestController {
    
    @GetMapping("/test")
    @Operation(
        summary = "Public test endpoint",
        description = "A public endpoint to test if the Application Config Service is reachable and responding."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Service is reachable and responding"
        )
    })
    public String publicTest() {
        return "Application Config Service is reachable (public)";
    }
    
    @GetMapping("/health")
    @Operation(
        summary = "Health check endpoint",
        description = "A simple health check endpoint to verify service status."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Service is healthy"
        )
    })
    public String health() {
        return "Application Config Service is healthy";
    }
} 