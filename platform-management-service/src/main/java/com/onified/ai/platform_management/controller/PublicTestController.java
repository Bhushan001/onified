package com.onified.ai.platform_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicTestController {
    @GetMapping("/test")
    public String publicTest() {
        return "Platform Management Service is reachable (public)";
    }
} 