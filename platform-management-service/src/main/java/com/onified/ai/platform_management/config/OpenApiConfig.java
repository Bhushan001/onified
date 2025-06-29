package com.onified.ai.platform_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI platformManagementOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:9081");
        localServer.setDescription("Local Development Server");

        Server dockerServer = new Server();
        dockerServer.setUrl("http://platform-management-service:9081");
        dockerServer.setDescription("Docker Container Server");

        Contact contact = new Contact();
        contact.setName("Onified Platform Team");
        contact.setEmail("support@onified.com");
        contact.setUrl("https://onified.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Platform Management Service API")
                .version("1.0.0")
                .description("""
                        ## Platform Management Service API Documentation
                        
                        This service provides comprehensive platform management capabilities for the Onified platform, including:
                        
                        ### 🔐 Password Policy Management
                        - Create, update, and manage password policies
                        - Set default password policies
                        - Configure password complexity requirements
                        - Manage password history and age restrictions
                        
                        ### 🏢 Tenant Management
                        - Create and manage multi-tenant configurations
                        - Handle tenant lifecycle operations
                        - Configure tenant-specific settings
                        
                        ### 🔧 Platform Configuration
                        - Platform-wide settings and configurations
                        - System health and status monitoring
                        
                        ### Authentication
                        Most endpoints require authentication. Use the appropriate authentication method:
                        - Bearer Token (JWT)
                        - API Key
                        - OAuth2 (if configured)
                        
                        ### Rate Limiting
                        API calls are rate-limited to ensure fair usage and system stability.
                        
                        ### Error Handling
                        The API returns standardized error responses with appropriate HTTP status codes.
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, dockerServer));
    }
} 