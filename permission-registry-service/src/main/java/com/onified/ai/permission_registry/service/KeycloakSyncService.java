package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.client.KeycloakFeignClient;
import com.onified.ai.permission_registry.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KeycloakSyncService {

    private final KeycloakFeignClient keycloakFeignClient;
    
    @Value("${keycloak.realm:master}")
    private String adminRealm;
    
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password:admin123}")
    private String adminPassword;
    
    @Value("${keycloak.client-id:admin-cli}")
    private String clientId;

    @Autowired
    public KeycloakSyncService(KeycloakFeignClient keycloakFeignClient) {
        this.keycloakFeignClient = keycloakFeignClient;
    }

    /**
     * Sync a role to Keycloak when it's created in permission-registry
     */
    public boolean syncRoleToKeycloak(Role role) {
        if (role == null || role.getRoleId() == null) {
            return false;
        }

        try {
            String accessToken = getAdminAccessToken();
            String authorization = "Bearer " + accessToken;
            
            KeycloakFeignClient.RoleRequest roleRequest = new KeycloakFeignClient.RoleRequest(
                role.getRoleId(), 
                buildRoleDescription(role)
            );
            
            // Use onified realm for role operations, but master realm for admin token
            ResponseEntity<Void> response = keycloakFeignClient.createRole(
                authorization, 
                "onified", // Target realm for roles
                roleRequest
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Successfully synced role '" + role.getRoleId() + "' to Keycloak");
                return true;
            } else {
                System.err.println("Failed to sync role '" + role.getRoleId() + "' to Keycloak");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error syncing role '" + role.getRoleId() + "' to Keycloak: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update a role in Keycloak when it's updated in permission-registry
     */
    public boolean updateRoleInKeycloak(Role role) {
        if (role == null || role.getRoleId() == null) {
            return false;
        }

        try {
            String accessToken = getAdminAccessToken();
            String authorization = "Bearer " + accessToken;
            
            // Check if role exists in Keycloak first
            if (!roleExistsInKeycloak(role.getRoleId(), authorization)) {
                // If role doesn't exist, create it
                return syncRoleToKeycloak(role);
            }

            KeycloakFeignClient.RoleRequest roleRequest = new KeycloakFeignClient.RoleRequest(
                role.getRoleId(), 
                buildRoleDescription(role)
            );
            
            ResponseEntity<Void> response = keycloakFeignClient.updateRole(authorization, "onified", role.getRoleId(), roleRequest);
            System.out.println("Successfully updated role '" + role.getRoleId() + "' in Keycloak");
            return true;
        } catch (Exception e) {
            System.err.println("Error updating role '" + role.getRoleId() + "' in Keycloak: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a role from Keycloak when it's deleted from permission-registry
     */
    public boolean deleteRoleFromKeycloak(String roleId) {
        if (roleId == null) {
            return false;
        }

        try {
            String accessToken = getAdminAccessToken();
            String authorization = "Bearer " + accessToken;
            
            ResponseEntity<Void> response = keycloakFeignClient.deleteRole(authorization, "onified", roleId);
            System.out.println("Successfully deleted role '" + roleId + "' from Keycloak");
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting role '" + roleId + "' from Keycloak: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if a role exists in Keycloak
     */
    private boolean roleExistsInKeycloak(String roleName, String authorization) {
        try {
            ResponseEntity<Map<String, Object>> response = keycloakFeignClient.getRole(authorization, "onified", roleName);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get admin access token for Keycloak API calls
     * Using master realm with admin-cli client
     */
    private String getAdminAccessToken() {
        try {
            System.out.println("Getting admin token from Keycloak...");
            System.out.println("Realm: " + adminRealm + ", Client: " + clientId + ", Username: " + adminUsername);
            
            // Build form data string for admin-cli client (no client_secret needed)
            String formData = String.format("grant_type=password&client_id=%s&username=%s&password=%s",
                    clientId, adminUsername, adminPassword);
            
            ResponseEntity<Map<String, Object>> response = keycloakFeignClient.getAccessToken(
                adminRealm, // Use master realm for admin authentication
                formData
            );
            
            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse != null && tokenResponse.containsKey("access_token")) {
                String token = (String) tokenResponse.get("access_token");
                System.out.println("Successfully obtained admin token from Keycloak");
                return token;
            } else {
                System.err.println("Invalid token response from Keycloak: " + tokenResponse);
                throw new RuntimeException("Invalid token response from Keycloak");
            }
        } catch (Exception e) {
            System.err.println("Failed to get Keycloak admin token: " + e.getMessage());
            throw new RuntimeException("Failed to get Keycloak admin token: " + e.getMessage(), e);
        }
    }

    /**
     * Build a descriptive string for the role based on its properties
     */
    private String buildRoleDescription(Role role) {
        StringBuilder description = new StringBuilder();
        
        if (role.getDisplayName() != null) {
            description.append("Display: ").append(role.getDisplayName());
        }
        
        if (role.getAppCode() != null) {
            if (description.length() > 0) description.append(" | ");
            description.append("App: ").append(role.getAppCode());
        }
        
        if (role.getModuleCode() != null) {
            if (description.length() > 0) description.append(" | ");
            description.append("Module: ").append(role.getModuleCode());
        }
        
        if (role.getRoleFunction() != null) {
            if (description.length() > 0) description.append(" | ");
            description.append("Function: ").append(role.getRoleFunction());
        }
        
        if (role.getIsActive() != null) {
            if (description.length() > 0) description.append(" | ");
            description.append("Active: ").append(role.getIsActive());
        }
        
        if (role.getTenantCustomizable() != null) {
            if (description.length() > 0) description.append(" | ");
            description.append("Tenant Customizable: ").append(role.getTenantCustomizable());
        }
        
        return description.toString();
    }

    /**
     * Sync all existing roles to Keycloak (useful for initial setup)
     */
    public void syncAllRolesToKeycloak(java.util.List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            System.out.println("No roles to sync to Keycloak");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (Role role : roles) {
            if (syncRoleToKeycloak(role)) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        System.out.println("Role sync completed: " + successCount + " successful, " + failureCount + " failed");
    }
} 