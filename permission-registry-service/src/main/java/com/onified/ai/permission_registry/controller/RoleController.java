package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.RoleRequestDTO;
import com.onified.ai.permission_registry.dto.RoleResponseDTO;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.onified.ai.permission_registry.model.CustomErrorResponse;
import feign.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        try {
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
            ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdRole));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
    }

    /**
     * Retrieves a Role by its roleId.
     * GET /api/roles/{roleId}
     * @param roleId The ID of the role to retrieve.
     * @return ResponseEntity with ApiResponse containing the RoleResponseDTO.
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleResponseDTO>> getRoleById(@PathVariable String roleId) {
        Role role = roleService.getRoleById(roleId);
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
        try {
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
            ApiResponse<RoleResponseDTO> response = new ApiResponse<>(
                    HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedRole));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
    }

    /**
     * Deletes a Role by its roleId.
     * DELETE /api/roles/{roleId}
     * @param roleId The ID of the role to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable String roleId) {
        roleService.deleteRole(roleId);
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

