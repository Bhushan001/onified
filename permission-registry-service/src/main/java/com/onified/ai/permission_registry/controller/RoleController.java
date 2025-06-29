package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.RoleRequestDTO;
import com.onified.ai.permission_registry.dto.RoleResponseDTO;
import com.onified.ai.permission_registry.entity.Role;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.model.CustomErrorResponse;
import com.onified.ai.permission_registry.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing roles in the RBAC/ABAC framework")
public class RoleController {

    private final RoleService roleService;

    /**
     * Creates a new Role.
     * POST /api/roles
     * @param requestDTO The RoleRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created RoleResponseDTO.
     */
    @PostMapping
    @Operation(
        summary = "Create a new role",
        description = "Creates a new role with the specified properties. The role will be assigned an inheritance depth of 0 initially.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role creation request",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleRequestDTO.class),
                examples = @ExampleObject(
                    name = "Admin Role Example",
                    value = """
                    {
                      "roleId": "ADMIN_ROLE",
                      "displayName": "Administrator Role",
                      "appCode": "USER_MGMT",
                      "moduleCode": "AUTH",
                      "roleFunction": "USER_ADMINISTRATION",
                      "isActive": true,
                      "tenantCustomizable": false
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Role created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                      "statusCode": 201,
                      "status": "SUCCESS",
                      "body": {
                        "roleId": "ADMIN_ROLE",
                        "displayName": "Administrator Role",
                        "appCode": "USER_MGMT",
                        "moduleCode": "AUTH",
                        "roleFunction": "USER_ADMINISTRATION",
                        "isActive": true,
                        "inheritanceDepth": 0,
                        "tenantCustomizable": false,
                        "createdAt": "2024-01-15T10:30:00",
                        "updatedAt": "2024-01-15T10:30:00"
                      }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Role with this ID already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Conflict Response",
                    value = """
                    {
                      "statusCode": 409,
                      "status": "CONFLICT",
                      "body": {
                        "errorCode": "CONFLICT",
                        "message": "Role with this ID already exists"
                      }
                    }
                    """
                )
            )
        )
    })
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
    @Operation(
        summary = "Get role by ID",
        description = "Retrieves a specific role by its unique identifier"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Role found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Role not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Not Found Response",
                    value = """
                    {
                      "statusCode": 404,
                      "status": "NOT_FOUND",
                      "body": {
                        "errorCode": "NOT_FOUND",
                        "message": "Role not found"
                      }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<?> getRoleById(
            @Parameter(description = "Unique identifier of the role", 
                      example = "ADMIN_ROLE", 
                      required = true)
            @PathVariable String roleId) {
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
    @Operation(
        summary = "Get all roles",
        description = "Retrieves all roles in the system"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Roles retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                      "statusCode": 200,
                      "status": "SUCCESS",
                      "body": [
                        {
                          "roleId": "ADMIN_ROLE",
                          "displayName": "Administrator Role",
                          "appCode": "USER_MGMT",
                          "moduleCode": "AUTH",
                          "roleFunction": "USER_ADMINISTRATION",
                          "isActive": true,
                          "inheritanceDepth": 0,
                          "tenantCustomizable": false,
                          "createdAt": "2024-01-15T10:30:00",
                          "updatedAt": "2024-01-15T10:30:00"
                        }
                      ]
                    }
                    """
                )
            )
        )
    })
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
    @Operation(
        summary = "Update an existing role",
        description = "Updates an existing role with new properties. The inheritance depth will be preserved."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Role updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Role not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<?> updateRole(
            @Parameter(description = "Unique identifier of the role to update", 
                      example = "ADMIN_ROLE", 
                      required = true)
            @PathVariable String roleId, 
            @RequestBody RoleRequestDTO requestDTO) {
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
    @Operation(
        summary = "Delete a role",
        description = "Deletes a role by its unique identifier"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Role deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                      "statusCode": 204,
                      "status": "SUCCESS",
                      "body": "Role deleted successfully."
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Role not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    public ResponseEntity<?> deleteRole(
            @Parameter(description = "Unique identifier of the role to delete", 
                      example = "ADMIN_ROLE", 
                      required = true)
            @PathVariable String roleId) {
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

