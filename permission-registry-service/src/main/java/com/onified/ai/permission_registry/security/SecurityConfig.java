package com.onified.ai.permission_registry.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/*
 * Security configuration class for the application.
 * It configures CORS, CSRF, session management, and authentication filters.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // Security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/keycloak-test/**").permitAll()
                        .requestMatchers("/api/actions/**").permitAll()
                        .requestMatchers("/api/scopes/**").permitAll()
                        .requestMatchers("/api/constraints/**").permitAll()
                        .requestMatchers("/api/behaviors/**").permitAll()
                        .requestMatchers("/api/pbus/**").permitAll()
                        .requestMatchers("/api/roles/**").permitAll()
                        .requestMatchers("/api/role-inheritance/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()// or permitAll if there is no security needed
                        .anyRequest().authenticated());
        return http.build();
    }
}


