package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.RoleInheritanceRequestDTO;
import com.onified.ai.permission_registry.dto.RoleInheritanceResponseDTO;
import com.onified.ai.permission_registry.entity.RoleInheritance;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.RoleInheritanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role-inheritance")
@RequiredArgsConstructor
public class RoleInheritanceController {

    private final RoleInheritanceService roleInheritanceService;


    /**
     * Establishes a new role inheritance relationship.
     * POST /api/role-inheritance
     * @param requestDTO The RoleInheritanceRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created RoleInheritanceResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoleInheritanceResponseDTO>> createRoleInheritance(@RequestBody RoleInheritanceRequestDTO requestDTO) {
        RoleInheritance createdInheritance = roleInheritanceService.createRoleInheritance(
                requestDTO.getParentRoleId(),
                requestDTO.getChildRoleId(),
                requestDTO.getApprovedBy()
        );
        ApiResponse<RoleInheritanceResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdInheritance));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all role inheritance relationships.
     * GET /api/role-inheritance
     * @return ResponseEntity with ApiResponse containing a list of RoleInheritanceResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleInheritanceResponseDTO>>> getAllRoleInheritances() {
        List<RoleInheritance> inheritances = roleInheritanceService.getAllRoleInheritances();
        List<RoleInheritanceResponseDTO> responseDTOs = inheritances.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<RoleInheritanceResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves role inheritance relationships where a role is a parent.
     * GET /api/role-inheritance/children/{parentRoleId}
     * @param parentRoleId The ID of the parent role.
     * @return ResponseEntity with ApiResponse containing a list of RoleInheritanceResponseDTOs.
     */
    @GetMapping("/children/{parentRoleId}")
    public ResponseEntity<ApiResponse<List<RoleInheritanceResponseDTO>>> getChildrenOfRole(@PathVariable String parentRoleId) {
        List<RoleInheritance> children = roleInheritanceService.getChildrenOfRole(parentRoleId);
        List<RoleInheritanceResponseDTO> responseDTOs = children.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<RoleInheritanceResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves role inheritance relationships where a role is a child.
     * GET /api/role-inheritance/parents/{childRoleId}
     * @param childRoleId The ID of the child role.
     * @return ResponseEntity with ApiResponse containing a list of RoleInheritanceResponseDTOs.
     */
    @GetMapping("/parents/{childRoleId}")
    public ResponseEntity<ApiResponse<List<RoleInheritanceResponseDTO>>> getParentsOfRole(@PathVariable String childRoleId) {
        List<RoleInheritance> parents = roleInheritanceService.getParentsOfRole(childRoleId);
        List<RoleInheritanceResponseDTO> responseDTOs = parents.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<RoleInheritanceResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a role inheritance relationship.
     * DELETE /api/role-inheritance/{parentRoleId}/{childRoleId}
     * @param parentRoleId The ID of the parent role.
     * @param childRoleId The ID of the child role.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{parentRoleId}/{childRoleId}")
    public ResponseEntity<ApiResponse<String>> deleteRoleInheritance(
            @PathVariable String parentRoleId, @PathVariable String childRoleId) {
        roleInheritanceService.deleteRoleInheritance(parentRoleId, childRoleId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Role inheritance removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private RoleInheritanceResponseDTO convertToResponseDTO(RoleInheritance inheritance) {
        return new RoleInheritanceResponseDTO(
                inheritance.getParentRoleId(),
                inheritance.getChildRoleId(),
                inheritance.getApprovedBy(),
                inheritance.getApprovalDate()
        );
    }
}

