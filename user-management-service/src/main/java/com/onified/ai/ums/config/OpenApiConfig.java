package com.onified.ai.ums.config;

import io.swagger.v3.oas.models.Components;
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
 * OpenAPI Configuration for User Management Service
 * Provides comprehensive API documentation with Swagger UI
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management Service API")
                        .description("""
                                **User Management Service** - Comprehensive User Management System for Onified.ai
                                
                                This service provides a complete user management system with the following capabilities:
                                
                                ## Core Features
                                - **User Management**: Create, read, update, and delete users
                                - **Authentication Details**: Retrieve user authentication information
                                - **Role Management**: Assign and remove roles from users
                                - **User Attributes**: Manage custom user attributes for ABAC
                                - **User Status Management**: Handle user account status (ACTIVE, INACTIVE, SUSPENDED)
                                - **Validation**: Comprehensive input validation with detailed error messages
                                - **Integration**: Seamless integration with Permission Registry Service
                                
                                ## Key Components
                                
                                ### User Operations (`/api/users`)
                                - **User CRUD**: Complete user lifecycle management
                                - **Authentication Details**: Retrieve user auth information for login
                                - **Role Assignment**: Dynamic role management with permission validation
                                - **Attribute Management**: Custom attribute system for ABAC
                                
                                ### User Data Model
                                - **Core Information**: Username, email, first/last name
                                - **Security**: Password handling with validation
                                - **Status Tracking**: User account status management
                                - **Timestamps**: Creation and update tracking
                                - **Roles**: Dynamic role assignments
                                - **Attributes**: Custom key-value attributes
                                
                                ### Integration Features
                                - **Permission Registry**: Role validation and assignment
                                - **Feign Client**: Declarative REST client for service communication
                                - **Error Handling**: Comprehensive error handling with Feign integration
                                - **Validation**: Jakarta validation with custom error messages
                                
                                ## Authentication & Security
                                - Stateless session management
                                - CORS enabled for cross-origin requests
                                - CSRF protection disabled for API endpoints
                                - Method-level security support
                                - Password validation and security
                                
                                ## Response Format
                                All API responses follow a standardized format:
                                ```json
                                {
                                  "statusCode": 200,
                                  "status": "SUCCESS",
                                  "body": { ... }
                                }
                                ```
                                
                                ## Error Handling
                                - Consistent error response format
                                - HTTP status codes for different error types
                                - Detailed error messages for debugging
                                - Feign client error integration
                                - Validation error handling
                                
                                ## Development & Testing
                                - Health check endpoint: `/actuator/health`
                                - Public test endpoints: `/api/public/**`
                                - Comprehensive logging with trace IDs
                                - Database integration with PostgreSQL
                                
                                ## User Status Values
                                - **ACTIVE**: User account is active and can access the system
                                - **INACTIVE**: User account is inactive and cannot access the system
                                - **SUSPENDED**: User account is temporarily suspended
                                
                                ## Validation Rules
                                - **Username**: 3-50 characters, required
                                - **Password**: Minimum 8 characters, required
                                - **Email**: Valid email format, required
                                - **First Name**: Maximum 100 characters
                                - **Last Name**: Maximum 100 characters
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Onified.ai Development Team")
                                .email("dev@onified.ai")
                                .url("https://onified.ai"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://onified.ai/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9085")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.onified.ai/user-management")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")));
    }
} 