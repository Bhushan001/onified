package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.RoleConstraintOverrideResponseDTO;
import com.onified.ai.permission_registry.entity.RoleContextualBehavior;
import com.onified.ai.permission_registry.entity.RoleFieldConstraint;
import com.onified.ai.permission_registry.entity.RoleGeneralConstraint;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.RoleConstraintOverrideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles/{roleId}/overrides")
@RequiredArgsConstructor
public class RoleConstraintOverrideController {

    private final RoleConstraintOverrideService roleConstraintOverrideService;

    // --- Role - General Constraint Overrides ---
    @PostMapping("/general-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<RoleConstraintOverrideResponseDTO>> addRoleGeneralConstraintOverride(
            @PathVariable String roleId, @PathVariable String constraintId) {
        RoleGeneralConstraint override = roleConstraintOverrideService.addRoleGeneralConstraintOverride(roleId, constraintId);
        ApiResponse<RoleConstraintOverrideResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(override.getRoleId(), override.getConstraintId(), null, "GENERAL_CONSTRAINT", "OVERRIDDEN"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/general-constraints")
    public ResponseEntity<ApiResponse<List<RoleConstraintOverrideResponseDTO>>> getRoleGeneralConstraintOverrides(@PathVariable String roleId) {
        List<RoleGeneralConstraint> overrides = roleConstraintOverrideService.getGeneralConstraintOverridesForRole(roleId);
        List<RoleConstraintOverrideResponseDTO> responseDTOs = overrides.stream()
                .map(override -> convertToResponseDTO(override.getRoleId(), override.getConstraintId(), null, "GENERAL_CONSTRAINT", "OVERRIDDEN"))
                .collect(Collectors.toList());
        ApiResponse<List<RoleConstraintOverrideResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/general-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<String>> removeRoleGeneralConstraintOverride(
            @PathVariable String roleId, @PathVariable String constraintId) {
        roleConstraintOverrideService.removeRoleGeneralConstraintOverride(roleId, constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Role-General Constraint override removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // --- Role - Field Constraint Overrides ---
    @PostMapping("/field-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<RoleConstraintOverrideResponseDTO>> addRoleFieldConstraintOverride(
            @PathVariable String roleId, @PathVariable String constraintId) {
        RoleFieldConstraint override = roleConstraintOverrideService.addRoleFieldConstraintOverride(roleId, constraintId);
        ApiResponse<RoleConstraintOverrideResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(override.getRoleId(), override.getConstraintId(), null, "FIELD_CONSTRAINT", "OVERRIDDEN"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/field-constraints")
    public ResponseEntity<ApiResponse<List<RoleConstraintOverrideResponseDTO>>> getRoleFieldConstraintOverrides(@PathVariable String roleId) {
        List<RoleFieldConstraint> overrides = roleConstraintOverrideService.getFieldConstraintOverridesForRole(roleId);
        List<RoleConstraintOverrideResponseDTO> responseDTOs = overrides.stream()
                .map(override -> convertToResponseDTO(override.getRoleId(), override.getConstraintId(), null, "FIELD_CONSTRAINT", "OVERRIDDEN"))
                .collect(Collectors.toList());
        ApiResponse<List<RoleConstraintOverrideResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/field-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<String>> removeRoleFieldConstraintOverride(
            @PathVariable String roleId, @PathVariable String constraintId) {
        roleConstraintOverrideService.removeRoleFieldConstraintOverride(roleId, constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Role-Field Constraint override removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // --- Role - Contextual Behavior Overrides ---
    @PostMapping("/contextual-behaviors/{behaviorId}")
    public ResponseEntity<ApiResponse<RoleConstraintOverrideResponseDTO>> addRoleContextualBehaviorOverride(
            @PathVariable String roleId, @PathVariable String behaviorId) {
        RoleContextualBehavior override = roleConstraintOverrideService.addRoleContextualBehaviorOverride(roleId, behaviorId);
        ApiResponse<RoleConstraintOverrideResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(override.getRoleId(), null, override.getBehaviorId(), "CONTEXTUAL_BEHAVIOR", "OVERRIDDEN"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/contextual-behaviors")
    public ResponseEntity<ApiResponse<List<RoleConstraintOverrideResponseDTO>>> getRoleContextualBehaviorOverrides(@PathVariable String roleId) {
        List<RoleContextualBehavior> overrides = roleConstraintOverrideService.getContextualBehaviorOverridesForRole(roleId);
        List<RoleConstraintOverrideResponseDTO> responseDTOs = overrides.stream()
                .map(override -> convertToResponseDTO(override.getRoleId(), null, override.getBehaviorId(), "CONTEXTUAL_BEHAVIOR", "OVERRIDDEN"))
                .collect(Collectors.toList());
        ApiResponse<List<RoleConstraintOverrideResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/contextual-behaviors/{behaviorId}")
    public ResponseEntity<ApiResponse<String>> removeRoleContextualBehaviorOverride(
            @PathVariable String roleId, @PathVariable String behaviorId) {
        roleConstraintOverrideService.removeRoleContextualBehaviorOverride(roleId, behaviorId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Role-Contextual Behavior override removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private RoleConstraintOverrideResponseDTO convertToResponseDTO(String roleId, String constraintId, String behaviorId, String overrideType, String status) {
        return new RoleConstraintOverrideResponseDTO(roleId, constraintId, behaviorId, overrideType, status);
    }
}
