package com.onified.ai.authentication_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class TestController {

    @GetMapping("/test")
    public String publicTest() {
        return "Authentication Service is reachable (public)";
    }
} 