package com.onified.ai.platform_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Test", description = "Public endpoints for testing service connectivity")
public class PublicTestController {
    
    @GetMapping("/test")
    @Operation(
        summary = "Test Service Connectivity",
        description = "Public endpoint to verify that the Platform Management Service is reachable and responding",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Service is reachable and responding"
            )
        }
    )
    public String publicTest() {
        return "Platform Management Service is reachable (public)";
    }
} 