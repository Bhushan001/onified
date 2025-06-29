package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.client.KeycloakFeignClient;
import com.onified.ai.permission_registry.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/keycloak-test")
public class KeycloakTestController {

    private final KeycloakFeignClient keycloakFeignClient;
    
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password:admin123}")
    private String adminPassword;
    
    @Value("${keycloak.client-id:admin-cli}")
    private String clientId;
    
    @Value("${keycloak.realm:master}")
    private String realm;

    @Autowired
    public KeycloakTestController(KeycloakFeignClient keycloakFeignClient) {
        this.keycloakFeignClient = keycloakFeignClient;
    }

    /**
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        ApiResponse<String> response = new ApiResponse<>(
            HttpStatus.OK.value(), "SUCCESS", "Keycloak test controller is working");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Test configuration values
     */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, String>>> getConfig() {
        Map<String, String> config = Map.of(
            "adminUsername", adminUsername,
            "clientId", clientId,
            "realm", realm,
            "keycloakUrl", "http://localhost:9090"
        );
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            HttpStatus.OK.value(), "SUCCESS", config);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Test Keycloak admin authentication
     */
    @GetMapping("/test-admin-auth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testAdminAuth() {
        try {
            System.out.println("Testing Keycloak admin authentication...");
            System.out.println("Realm: " + realm);
            System.out.println("Client: " + clientId);
            System.out.println("Username: " + adminUsername);
            System.out.println("Password: " + adminPassword);
            
            // Build form data string for admin-cli client (no client_secret needed)
            String formData = String.format("grant_type=password&client_id=%s&username=%s&password=%s",
                    clientId, adminUsername, adminPassword);
            
            ResponseEntity<Map<String, Object>> response = keycloakFeignClient.getAccessToken(
                realm,
                formData
            );
            
            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse != null && tokenResponse.containsKey("access_token")) {
                String token = (String) tokenResponse.get("access_token");
                // Mask the token for security
                String maskedToken = token.substring(0, Math.min(20, token.length())) + "...";
                tokenResponse.put("access_token", maskedToken);
                
                ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK.value(), "SUCCESS", tokenResponse);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(), "ERROR", 
                    Map.of("error", "Invalid token response", "response", tokenResponse));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.err.println("Admin auth test failed: " + e.getMessage());
            e.printStackTrace();
            
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", 
                Map.of("error", e.getMessage(), "type", e.getClass().getSimpleName()));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Test with different client IDs
     */
    @GetMapping("/test-client/{clientId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testClient(@PathVariable String clientId) {
        try {
            System.out.println("Testing with client: " + clientId);
            
            // Build form data string
            String formData = String.format("grant_type=password&client_id=%s&username=%s&password=%s",
                    clientId, adminUsername, adminPassword);
            
            ResponseEntity<Map<String, Object>> response = keycloakFeignClient.getAccessToken(
                realm,
                formData
            );
            
            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse != null && tokenResponse.containsKey("access_token")) {
                String token = (String) tokenResponse.get("access_token");
                String maskedToken = token.substring(0, Math.min(20, token.length())) + "...";
                tokenResponse.put("access_token", maskedToken);
                
                ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.OK.value(), "SUCCESS", tokenResponse);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                    HttpStatus.BAD_REQUEST.value(), "ERROR", 
                    Map.of("error", "Invalid token response", "response", tokenResponse));
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.err.println("Client test failed for " + clientId + ": " + e.getMessage());
            
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", 
                Map.of("error", e.getMessage(), "clientId", clientId));
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 