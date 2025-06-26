package com.onified.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Health check route
                .route("health-check", r -> r.path("/health")
                        .filters(f -> f.rewritePath("/health", "/actuator/health"))
                        .uri("http://localhost:8080"))
                
                // Platform Management Service routes
                .route("platform-management", r -> r.path("/api/platform-management/**")
                        .filters(f -> f.rewritePath("/api/platform-management/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://platform-management-service"))
                
                // Password Policy routes (platform level)
                .route("password-policies", r -> r.path("/api/password-policies/**")
                        .uri("lb://platform-management-service"))
                
                // Tenant Management Service routes
                .route("tenant-management", r -> r.path("/api/tenant/**")
                        .filters(f -> f.rewritePath("/api/tenant/(?<segment>.*)", "/api/tenant/${segment}"))
                        .uri("lb://tenant-management-service"))
                
                // User Management Service routes
                .route("user-management", r -> r.path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://user-management-service"))
                
                // Permission Registry Service routes
                .route("permission-registry", r -> r.path("/api/permissions/**")
                        .filters(f -> f.rewritePath("/api/permissions/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://permission-registry-service"))
                
                // Authentication Service routes
                .route("authentication", r -> r.path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://authentication-service"))
                
                // Application Config Service routes
                .route("application-config", r -> r.path("/api/config/**")
                        .filters(f -> f.rewritePath("/api/config/(?<segment>.*)", "/api/${segment}"))
                        .uri("lb://application-config-service"))
                
                .build();
    }
} 