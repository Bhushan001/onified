package com.onified.ai.permission_registry.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
    name = "keycloak-client",
    url = "${keycloak.auth-server-url:http://localhost:9090}",
    configuration = KeycloakFeignConfig.class
)
public interface KeycloakFeignClient {

    /**
     * Get admin access token
     */
    @PostMapping(value = "/realms/{realm}/protocol/openid-connect/token", 
                consumes = "application/x-www-form-urlencoded")
    ResponseEntity<Map<String, Object>> getAccessToken(
        @PathVariable("realm") String realm,
        @RequestBody String formData
    );

    /**
     * Create a role in Keycloak
     */
    @PostMapping("/admin/realms/{realm}/roles")
    ResponseEntity<Void> createRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("realm") String realm,
        @RequestBody RoleRequest roleRequest
    );

    /**
     * Update a role in Keycloak
     */
    @PutMapping("/admin/realms/{realm}/roles/{roleName}")
    ResponseEntity<Void> updateRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("realm") String realm,
        @PathVariable("roleName") String roleName,
        @RequestBody RoleRequest roleRequest
    );

    /**
     * Delete a role from Keycloak
     */
    @DeleteMapping("/admin/realms/{realm}/roles/{roleName}")
    ResponseEntity<Void> deleteRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("realm") String realm,
        @PathVariable("roleName") String roleName
    );

    /**
     * Check if a role exists in Keycloak
     */
    @GetMapping("/admin/realms/{realm}/roles/{roleName}")
    ResponseEntity<Map<String, Object>> getRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable("realm") String realm,
        @PathVariable("roleName") String roleName
    );

    /**
     * Role request DTO
     */
    class RoleRequest {
        private String name;
        private String description;
        private boolean composite;
        private boolean clientRole;

        // Constructors
        public RoleRequest() {}

        public RoleRequest(String name, String description) {
            this.name = name;
            this.description = description;
            this.composite = false;
            this.clientRole = false;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isComposite() { return composite; }
        public void setComposite(boolean composite) { this.composite = composite; }

        public boolean isClientRole() { return clientRole; }
        public void setClientRole(boolean clientRole) { this.clientRole = clientRole; }
    }
} 