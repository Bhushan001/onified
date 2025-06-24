package com.onified.ai.authentication_service.config;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakConfig {

    private String authServerUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private Admin admin = new Admin();

    @Data
    public static class Admin {
        private String username;
        private String password;
        private String realm;
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(admin.getRealm())
                .clientId("admin-cli")
                .username(admin.getUsername())
                .password(admin.getPassword())
                .build();
    }
} 