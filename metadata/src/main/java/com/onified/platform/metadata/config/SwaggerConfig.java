//package com.onified.platform.metadata.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.License;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI metadataServiceOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Onified Platform - Metadata Service API")
//                        .description("Pure metadata repository service for configurable platform entities")
//                        .version("v1.0.0")
//                        .contact(new Contact()
//                                .name("Onified Platform Team")
//                                .email("platform@onified.ai"))
//                        .license(new License()
//                                .name("Proprietary")
//                                .url("https://onified.ai/license")));
//    }
//}
