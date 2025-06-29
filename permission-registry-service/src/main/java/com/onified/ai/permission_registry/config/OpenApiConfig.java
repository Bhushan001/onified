package com.onified.ai.permission_registry.config;

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
 * OpenAPI Configuration for Permission Registry Service
 * Provides comprehensive API documentation with Swagger UI
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Permission Registry Service API")
                        .description("""
                                **Permission Registry Service** - Comprehensive RBAC/ABAC Framework for Onified.ai
                                
                                This service provides a complete permission management system with the following capabilities:
                                
                                ## Core Features
                                - **Role Management**: Create, read, update, and delete roles with inheritance support
                                - **Permission Bundle Units (PBUs)**: Manage granular permissions with API endpoints and actions
                                - **Scopes**: Define resource scopes for fine-grained access control
                                - **Actions**: Manage available actions that can be performed on resources
                                - **Constraints**: Implement both general and field-level constraints for ABAC
                                - **Contextual Behaviors**: Define context-aware permission behaviors
                                - **Role Inheritance**: Support for hierarchical role structures
                                - **Constraint Overrides**: Allow role-specific constraint modifications
                                
                                ## Key Components
                                
                                ### Roles (`/api/roles`)
                                - Core role definitions with inheritance depth tracking
                                - Support for tenant customization
                                - Role function categorization
                                
                                ### Permission Bundle Units (`/api/pbus`)
                                - Granular permission definitions
                                - API endpoint mapping
                                - Action and scope associations
                                - Version control support
                                
                                ### Scopes (`/api/scopes`)
                                - Resource scope definitions
                                - Hierarchical scope structures
                                - Active/inactive state management
                                
                                ### Actions (`/api/actions`)
                                - Available action definitions
                                - Action categorization
                                - State management
                                
                                ### Constraints
                                - **General Constraints** (`/api/constraints/general`): Application-wide constraint rules
                                - **Field Constraints** (`/api/constraints/field`): Field-level access control
                                
                                ### Behaviors (`/api/behaviors`)
                                - Contextual behavior definitions
                                - Dynamic permission evaluation
                                - Behavior state management
                                
                                ### Associations
                                - **PBU-Constraint Associations** (`/api/pbus/constraints`): Link constraints to PBUs
                                - **Role Inheritance** (`/api/role-inheritance`): Define role hierarchies
                                - **Role Constraint Overrides** (`/api/role-constraint-overrides`): Role-specific constraint modifications
                                
                                ## Authentication & Security
                                - Stateless session management
                                - CORS enabled for cross-origin requests
                                - CSRF protection disabled for API endpoints
                                - Method-level security support
                                
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
                                
                                ## Development & Testing
                                - Health check endpoint: `/actuator/health`
                                - Public test endpoints: `/api/public/**`
                                - Comprehensive logging with trace IDs
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
                                .url("http://localhost:9084")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.onified.ai/permission-registry")
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