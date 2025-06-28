package com.onified.eureka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicTestController {
    @GetMapping("/test")
    public String publicTest() {
        return "Eureka Server is reachable (public)";
    }
} 