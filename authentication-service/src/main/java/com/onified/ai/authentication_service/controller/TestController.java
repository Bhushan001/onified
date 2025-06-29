package com.onified.ai.authentication_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Endpoints", description = "Public endpoints for testing and health checks")
public class TestController {

    @GetMapping("/test")
    @Operation(
        summary = "Public test endpoint",
        description = "A public endpoint to test if the Authentication Service is reachable and responding."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Service is reachable and responding"
        )
    })
    public String publicTest() {
        return "Authentication Service is reachable (public)";
    }
} 