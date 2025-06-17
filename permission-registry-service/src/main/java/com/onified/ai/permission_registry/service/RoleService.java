package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.client.ApplicationConfigServiceClient;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.repository.RoleRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final ApplicationConfigServiceClient appConfigServiceClient;

    // Updated Regex for Role naming convention: {APP}.{MODULE}.{ROLE_FUNCTION}
    // Now allows alphanumeric characters and underscores for MODULE and ROLE_FUNCTION.
    private static final Pattern ROLE_NAMING_PATTERN = Pattern.compile("^[A-Z]+\\.[A-Za-z0-9_]+\\.[A-Za-z0-9_]+$");

    @Autowired
    public RoleService(RoleRepository roleRepository, ApplicationConfigServiceClient appConfigServiceClient) {
        this.roleRepository = roleRepository;
        this.appConfigServiceClient = appConfigServiceClient;
    }

    public Role createRole(Role role) {
        validateRoleNamingConvention(role.getRoleId());

        if (roleRepository.existsById(role.getRoleId())) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_ALREADY_EXISTS, role.getRoleId()));
        }

        validateAppAndModuleExistence(role.getAppCode(), role.getModuleCode());

        if (role.getInheritanceDepth() == null) {
            role.setInheritanceDepth(0);
        }

        return roleRepository.save(role);
    }

    public Role getRoleById(String roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId)));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role updateRole(String roleId, Role updatedRole) {
        return roleRepository.findById(roleId).map(existingRole -> {
            if (!Objects.equals(existingRole.getAppCode(), updatedRole.getAppCode()) ||
                    !Objects.equals(existingRole.getModuleCode(), updatedRole.getModuleCode())) {
                validateAppAndModuleExistence(updatedRole.getAppCode(), updatedRole.getModuleCode());
            }

            existingRole.setDisplayName(updatedRole.getDisplayName());
            existingRole.setAppCode(updatedRole.getAppCode());
            existingRole.setModuleCode(updatedRole.getModuleCode());
            existingRole.setRoleFunction(updatedRole.getRoleFunction());
            existingRole.setIsActive(updatedRole.getIsActive());
            existingRole.setTenantCustomizable(updatedRole.getTenantCustomizable());
            return roleRepository.save(existingRole);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId)));
    }

    public void deleteRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        // TODO: Add robust checks for dependent entities before deletion:
        // - Check role_inheritance (if it's a parent or child)
        // - Check role_general_constraints, role_field_constraints, role_contextual_behaviors
        // - Check user_role_map
        // - Check delegations (if relevant)
        roleRepository.deleteById(roleId);
    }

    private void validateRoleNamingConvention(String roleId) {
        if (!ROLE_NAMING_PATTERN.matcher(roleId).matches()) {
            throw new BadRequestException(String.format(ErrorMessages.INVALID_ROLE_NAMING_CONVENTION, roleId));
        }
    }

    private void validateAppAndModuleExistence(String appCode, String moduleCode) {
        try {
            ApiResponse<ApplicationConfigServiceClient.ApplicationResponseForClient> appApiResponse = appConfigServiceClient.getApplicationByAppCode(appCode);
            if (appApiResponse == null || appApiResponse.getBody() == null || !appApiResponse.getBody().getIsActive()) {
                throw new BadRequestException(String.format(ErrorMessages.ROLE_APP_MODULE_INVALID, appCode, moduleCode));
            }

            ApiResponse<List<ApplicationConfigServiceClient.ModuleResponseForClient>> modulesApiResponse = appConfigServiceClient.getAppModulesByAppCode(appCode);
            List<ApplicationConfigServiceClient.ModuleResponseForClient> modules = modulesApiResponse != null ? modulesApiResponse.getBody() : null;

            if (modules == null || modules.isEmpty()) {
                throw new BadRequestException(String.format(ErrorMessages.ROLE_APP_MODULE_INVALID, appCode, moduleCode));
            }

            boolean moduleFoundAndActive = modules.stream()
                    .anyMatch(module -> module.getModuleCode().equals(moduleCode) && module.getIsActive());

            if (!moduleFoundAndActive) {
                throw new BadRequestException(String.format(ErrorMessages.ROLE_APP_MODULE_INVALID, appCode, moduleCode));
            }

        } catch (FeignException.NotFound e) {
            throw new BadRequestException(String.format(ErrorMessages.ROLE_APP_MODULE_INVALID, appCode, moduleCode) + ". Details: " + e.getMessage());
        } catch (FeignException e) {
            throw new RuntimeException("Failed to communicate with Application Config Service for App/Module validation: " + e.getMessage(), e);
        }
    }

    public Role updateRoleInheritanceDepth(String roleId, int newDepth) {
        Role role = getRoleById(roleId);
        role.setInheritanceDepth(newDepth);
        return roleRepository.save(role);
    }
}
