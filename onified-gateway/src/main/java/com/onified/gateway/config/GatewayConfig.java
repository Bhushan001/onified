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
                .build();
    }
} 