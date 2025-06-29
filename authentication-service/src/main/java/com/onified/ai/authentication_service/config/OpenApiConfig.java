package com.onified.ai.authentication_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for Authentication Service
 * Provides Swagger documentation configuration and customization
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .description("""
                                RESTful API for user authentication and authorization in the Onified platform.
                                
                                This service provides endpoints for:
                                - User authentication and login
                                - Token refresh and management
                                - User registration
                                - Health monitoring
                                
                                ## Features
                                - **Keycloak Integration**: Secure authentication using Keycloak
                                - **JWT Token Management**: Access and refresh token handling
                                - **User Registration**: New user account creation
                                - **Token Refresh**: Automatic token renewal
                                - **Health Monitoring**: Service status and health checks
                                
                                ## Authentication
                                This service integrates with Keycloak for secure authentication.
                                Users can authenticate using username/password and receive JWT tokens.
                                
                                ## Security
                                - All sensitive endpoints require proper authentication
                                - JWT tokens are used for session management
                                - Passwords are securely handled and never stored in plain text
                                
                                ## Rate Limiting
                                API calls are subject to rate limiting to prevent abuse.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Onified Development Team")
                                .email("dev@onified.ai")
                                .url("https://onified.ai"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9083")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.onified.ai")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")));
    }
} 