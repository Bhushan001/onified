package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.RoleRequestDTO;
import com.onified.ai.permission_registry.dto.RoleResponseDTO;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.model.CustomErrorResponse;
import com.onified.ai.permission_registry.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Creates a new Role.
     * POST /api/roles
     * @param requestDTO The RoleRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created RoleResponseDTO.
     */
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleRequestDTO requestDTO) {
        Role role = new Role(
                requestDTO.getRoleId(),
                requestDTO.getDisplayName(),
                requestDTO.getAppCode(),
                requestDTO.getModuleCode(),
                requestDTO.getRoleFunction(),
                requestDTO.getIsActive(),
                0, // Default depth on creation, updated by inheritance service
                requestDTO.getTenantCustomizable()
        );
        Role createdRole = roleService.createRole(role);
        
        if (createdRole == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("CONFLICT", "Role with this ID already exists");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), "CONFLICT", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdRole));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a Role by its roleId.
     * GET /api/roles/{roleId}
     * @param roleId The ID of the role to retrieve.
     * @return ResponseEntity with ApiResponse containing the RoleResponseDTO.
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable String roleId) {
        Role role = roleService.getRoleById(roleId);
        
        if (role == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Role not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(role));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all Roles.
     * GET /api/roles
     * @return ResponseEntity with ApiResponse containing a list of RoleResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponseDTO>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleResponseDTO> responseDTOs = roles.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<RoleResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing Role.
     * PUT /api/roles/{roleId}
     * @param roleId The ID of the role to update.
     * @param requestDTO The RoleRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated RoleResponseDTO.
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<?> updateRole(@PathVariable String roleId, @RequestBody RoleRequestDTO requestDTO) {
        Role role = new Role(
                requestDTO.getRoleId(),
                requestDTO.getDisplayName(),
                requestDTO.getAppCode(),
                requestDTO.getModuleCode(),
                requestDTO.getRoleFunction(),
                requestDTO.getIsActive(),
                null, // Will be ignored by service update method for depth
                requestDTO.getTenantCustomizable()
        );
        Role updatedRole = roleService.updateRole(roleId, role);
        
        if (updatedRole == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Role not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedRole));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a Role by its roleId.
     * DELETE /api/roles/{roleId}
     * @param roleId The ID of the role to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable String roleId) {
        boolean deleted = roleService.deleteRole(roleId);
        
        if (!deleted) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Role not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Role deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private RoleResponseDTO convertToResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getRoleId(),
                role.getDisplayName(),
                role.getAppCode(),
                role.getModuleCode(),
                role.getRoleFunction(),
                role.getIsActive(),
                role.getInheritanceDepth(),
                role.getTenantCustomizable(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}

