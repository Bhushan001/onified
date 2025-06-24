package com.onified.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/app-config")
    public Mono<ResponseEntity<Map<String, Object>>> appConfigFallback() {
        return createFallbackResponse("Application Config Service is currently unavailable");
    }

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return createFallbackResponse("Authentication Service is currently unavailable");
    }

    @GetMapping("/ums")
    public Mono<ResponseEntity<Map<String, Object>>> umsFallback() {
        return createFallbackResponse("User Management Service is currently unavailable");
    }

    @GetMapping("/permissions")
    public Mono<ResponseEntity<Map<String, Object>>> permissionsFallback() {
        return createFallbackResponse("Permission Registry Service is currently unavailable");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
} 