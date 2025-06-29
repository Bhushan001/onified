package com.onified.ai.appConfig.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for Application Config Service
 * Provides Swagger documentation configuration and customization
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Application Config Service API")
                        .description("""
                                RESTful API for managing application configurations and modules in the Onified platform.
                                
                                This service provides endpoints for:
                                - Creating, reading, updating, and deleting applications
                                - Managing application modules
                                - Application configuration management
                                
                                ## Features
                                - **Application Management**: Full CRUD operations for applications
                                - **Module Management**: Create and manage modules within applications
                                - **Active/Inactive Status**: Toggle application and module status
                                - **Standardized Responses**: Consistent API response format
                                
                                ## Authentication
                                This service requires proper authentication and authorization.
                                
                                ## Rate Limiting
                                API calls are subject to rate limiting to ensure service stability.
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
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.onified.ai")
                                .description("Production Server")
                ));
    }
} 