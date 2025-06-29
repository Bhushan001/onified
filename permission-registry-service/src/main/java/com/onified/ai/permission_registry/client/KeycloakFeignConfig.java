package com.onified.ai.permission_registry.client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakFeignConfig {

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new KeycloakErrorDecoder();
    }

    /**
     * Custom error decoder for Keycloak API errors
     */
    public static class KeycloakErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            switch (response.status()) {
                case 400:
                    return new RuntimeException("Bad request to Keycloak: " + methodKey);
                case 401:
                    return new RuntimeException("Unauthorized access to Keycloak: " + methodKey);
                case 403:
                    return new RuntimeException("Forbidden access to Keycloak: " + methodKey);
                case 404:
                    return new RuntimeException("Resource not found in Keycloak: " + methodKey);
                case 409:
                    return new RuntimeException("Conflict in Keycloak (e.g., role already exists): " + methodKey);
                case 500:
                    return new RuntimeException("Internal server error in Keycloak: " + methodKey);
                default:
                    return new RuntimeException("Unexpected error from Keycloak: " + methodKey + ", status: " + response.status());
            }
        }
    }
} 