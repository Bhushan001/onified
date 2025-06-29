package com.onified.ai.platform_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                // ========================================
                // Swagger UI and OpenAPI Documentation
                // ========================================
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api-docs/**", "/api-docs.yaml", "/api-docs").permitAll()
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/v3/api-docs").permitAll()
                .requestMatchers("/swagger-resources/**", "/webjars/**").permitAll()
                .requestMatchers("/configuration/ui", "/configuration/security").permitAll()
                
                // ========================================
                // Actuator endpoints for monitoring
                // ========================================
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // ========================================
                // Public API endpoints
                // ========================================
                .requestMatchers("/api/public/**").permitAll()
                
                // ========================================
                // Password Policy endpoints (temporarily public for testing)
                // ========================================
                .requestMatchers("/api/password-policies/**").permitAll()
                
                // ========================================
                // Tenant endpoints (temporarily public for testing)
                // ========================================
                .requestMatchers("/api/tenants/**").permitAll()
                
                // ========================================
                // All other requests require authentication
                // ========================================
                .anyRequest().authenticated()
            );
        return http.build();
    }
} 