package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.client.ApplicationConfigServiceClient;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final ApplicationConfigServiceClient appConfigServiceClient;
    private final KeycloakSyncService keycloakSyncService;

    // Updated Regex for Role naming convention: {APP}.{MODULE}.{ROLE_FUNCTION}
    // Now allows alphanumeric characters and underscores for MODULE and ROLE_FUNCTION.
    private static final Pattern ROLE_NAMING_PATTERN = Pattern.compile("^[A-Z]+\\.[A-Za-z0-9_]+\\.[A-Za-z0-9_]+$");

    @Autowired
    public RoleService(RoleRepository roleRepository, 
                      ApplicationConfigServiceClient appConfigServiceClient,
                      KeycloakSyncService keycloakSyncService) {
        this.roleRepository = roleRepository;
        this.appConfigServiceClient = appConfigServiceClient;
        this.keycloakSyncService = keycloakSyncService;
    }

    public Role createRole(Role role) {
        if (!validateRoleNamingConvention(role.getRoleId())) {
            return null; // Invalid naming convention
        }

        if (roleRepository.existsById(role.getRoleId())) {
            return null; // Role already exists
        }

        if (!validateAppAndModuleExistence(role.getAppCode(), role.getModuleCode())) {
            return null; // Invalid app/module
        }

        if (role.getInheritanceDepth() == null) {
            role.setInheritanceDepth(0);
        }

        // Save role to database
        Role savedRole = roleRepository.save(role);
        
        // Sync to Keycloak (non-blocking - don't fail if Keycloak sync fails)
        try {
            keycloakSyncService.syncRoleToKeycloak(savedRole);
        } catch (Exception e) {
            System.err.println("Warning: Failed to sync role to Keycloak, but role was saved to database: " + e.getMessage());
        }

        return savedRole;
    }

    public Role getRoleById(String roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role updateRole(String roleId, Role updatedRole) {
        return roleRepository.findById(roleId).map(existingRole -> {
            if (!Objects.equals(existingRole.getAppCode(), updatedRole.getAppCode()) ||
                    !Objects.equals(existingRole.getModuleCode(), updatedRole.getModuleCode())) {
                if (!validateAppAndModuleExistence(updatedRole.getAppCode(), updatedRole.getModuleCode())) {
                    return null; // Invalid app/module
                }
            }

            existingRole.setDisplayName(updatedRole.getDisplayName());
            existingRole.setAppCode(updatedRole.getAppCode());
            existingRole.setModuleCode(updatedRole.getModuleCode());
            existingRole.setRoleFunction(updatedRole.getRoleFunction());
            existingRole.setIsActive(updatedRole.getIsActive());
            existingRole.setTenantCustomizable(updatedRole.getTenantCustomizable());
            
            // Save updated role to database
            Role savedRole = roleRepository.save(existingRole);
            
            // Sync to Keycloak (non-blocking)
            try {
                keycloakSyncService.updateRoleInKeycloak(savedRole);
            } catch (Exception e) {
                System.err.println("Warning: Failed to sync role update to Keycloak: " + e.getMessage());
            }
            
            return savedRole;
        }).orElse(null);
    }

    public boolean deleteRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            return false;
        }
        
        // TODO: Add robust checks for dependent entities before deletion:
        // - Check role_inheritance (if it's a parent or child)
        // - Check role_general_constraints, role_field_constraints, role_contextual_behaviors
        // - Check user_role_map
        // - Check delegations (if relevant)
        
        // Delete from database
        roleRepository.deleteById(roleId);
        
        // Delete from Keycloak (non-blocking)
        try {
            keycloakSyncService.deleteRoleFromKeycloak(roleId);
        } catch (Exception e) {
            System.err.println("Warning: Failed to delete role from Keycloak: " + e.getMessage());
        }
        
        return true;
    }

    private boolean validateRoleNamingConvention(String roleId) {
        return ROLE_NAMING_PATTERN.matcher(roleId).matches();
    }

    private boolean validateAppAndModuleExistence(String appCode, String moduleCode) {
        try {
            ApiResponse<ApplicationConfigServiceClient.ApplicationResponseForClient> appApiResponse = appConfigServiceClient.getApplicationByAppCode(appCode);
            if (appApiResponse == null || appApiResponse.getBody() == null || !appApiResponse.getBody().getIsActive()) {
                return false;
            }

            ApiResponse<List<ApplicationConfigServiceClient.ModuleResponseForClient>> modulesApiResponse = appConfigServiceClient.getAppModulesByAppCode(appCode);
            List<ApplicationConfigServiceClient.ModuleResponseForClient> modules = modulesApiResponse != null ? modulesApiResponse.getBody() : null;

            if (modules == null || modules.isEmpty()) {
                return false;
            }

            boolean moduleFoundAndActive = modules.stream()
                    .anyMatch(module -> module.getModuleCode().equals(moduleCode) && module.getIsActive());

            return moduleFoundAndActive;

        } catch (Exception e) {
            // If the external service is not available, we'll allow the role creation
            // This makes the service more resilient when running independently
            return true;
        }
    }

    public Role updateRoleInheritanceDepth(String roleId, int newDepth) {
        Role role = getRoleById(roleId);
        if (role == null) {
            return null;
        }
        role.setInheritanceDepth(newDepth);
        
        // Save to database
        Role savedRole = roleRepository.save(role);
        
        // Sync to Keycloak (non-blocking)
        try {
            keycloakSyncService.updateRoleInKeycloak(savedRole);
        } catch (Exception e) {
            System.err.println("Warning: Failed to sync role inheritance depth update to Keycloak: " + e.getMessage());
        }
        
        return savedRole;
    }

    /**
     * Sync all existing roles to Keycloak (useful for initial setup or recovery)
     */
    public void syncAllRolesToKeycloak() {
        List<Role> allRoles = getAllRoles();
        keycloakSyncService.syncAllRolesToKeycloak(allRoles);
    }
}
