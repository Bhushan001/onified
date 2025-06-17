package com.onified.ai.permission_registry.controller;


import com.onified.ai.permission_registry.dto.PbuConstraintAssociationResponseDTO;
import com.onified.ai.permission_registry.entity.PbuContextualBehavior;
import com.onified.ai.permission_registry.entity.PbuFieldConstraint;
import com.onified.ai.permission_registry.entity.PbuGeneralConstraint;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.PbuAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pbus/{pbuId}/associations")
@RequiredArgsConstructor
public class PbuAssociationController {

    private final PbuAssociationService pbuAssociationService;

    // --- General Constraint Associations ---
    @PostMapping("/general-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<PbuConstraintAssociationResponseDTO>> addPbuGeneralConstraint(
            @PathVariable String pbuId, @PathVariable String constraintId) {
        PbuGeneralConstraint association = pbuAssociationService.associatePbuWithGeneralConstraint(pbuId, constraintId);
        ApiResponse<PbuConstraintAssociationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(association.getPbuId(), association.getConstraintId(), null, "GENERAL_CONSTRAINT", "ASSOCIATED"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/general-constraints")
    public ResponseEntity<ApiResponse<List<PbuConstraintAssociationResponseDTO>>> getPbuGeneralConstraints(@PathVariable String pbuId) {
        List<PbuGeneralConstraint> associations = pbuAssociationService.getGeneralConstraintsForPbu(pbuId);
        List<PbuConstraintAssociationResponseDTO> responseDTOs = associations.stream()
                .map(assoc -> convertToResponseDTO(assoc.getPbuId(), assoc.getConstraintId(), null, "GENERAL_CONSTRAINT", "ASSOCIATED"))
                .collect(Collectors.toList());
        ApiResponse<List<PbuConstraintAssociationResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/general-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<String>> removePbuGeneralConstraint(
            @PathVariable String pbuId, @PathVariable String constraintId) {
        pbuAssociationService.removePbuGeneralConstraintAssociation(pbuId, constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "PBU-General Constraint association removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // --- Field Constraint Associations ---
    @PostMapping("/field-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<PbuConstraintAssociationResponseDTO>> addPbuFieldConstraint(
            @PathVariable String pbuId, @PathVariable String constraintId) {
        PbuFieldConstraint association = pbuAssociationService.associatePbuWithFieldConstraint(pbuId, constraintId);
        ApiResponse<PbuConstraintAssociationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(association.getPbuId(), association.getConstraintId(), null, "FIELD_CONSTRAINT", "ASSOCIATED"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/field-constraints")
    public ResponseEntity<ApiResponse<List<PbuConstraintAssociationResponseDTO>>> getPbuFieldConstraints(@PathVariable String pbuId) {
        List<PbuFieldConstraint> associations = pbuAssociationService.getFieldConstraintsForPbu(pbuId);
        List<PbuConstraintAssociationResponseDTO> responseDTOs = associations.stream()
                .map(assoc -> convertToResponseDTO(assoc.getPbuId(), assoc.getConstraintId(), null, "FIELD_CONSTRAINT", "ASSOCIATED"))
                .collect(Collectors.toList());
        ApiResponse<List<PbuConstraintAssociationResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/field-constraints/{constraintId}")
    public ResponseEntity<ApiResponse<String>> removePbuFieldConstraint(
            @PathVariable String pbuId, @PathVariable String constraintId) {
        pbuAssociationService.removePbuFieldConstraintAssociation(pbuId, constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "PBU-Field Constraint association removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    // --- Contextual Behavior Associations ---
    @PostMapping("/contextual-behaviors/{behaviorId}")
    public ResponseEntity<ApiResponse<PbuConstraintAssociationResponseDTO>> addPbuContextualBehavior(
            @PathVariable String pbuId, @PathVariable String behaviorId) {
        PbuContextualBehavior association = pbuAssociationService.associatePbuWithContextualBehavior(pbuId, behaviorId);
        ApiResponse<PbuConstraintAssociationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(association.getPbuId(), null, association.getBehaviorId(), "CONTEXTUAL_BEHAVIOR", "ASSOCIATED"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/contextual-behaviors")
    public ResponseEntity<ApiResponse<List<PbuConstraintAssociationResponseDTO>>> getPbuContextualBehaviors(@PathVariable String pbuId) {
        List<PbuContextualBehavior> associations = pbuAssociationService.getContextualBehaviorsForPbu(pbuId);
        List<PbuConstraintAssociationResponseDTO> responseDTOs = associations.stream()
                .map(assoc -> convertToResponseDTO(assoc.getPbuId(), null, assoc.getBehaviorId(), "CONTEXTUAL_BEHAVIOR", "ASSOCIATED"))
                .collect(Collectors.toList());
        ApiResponse<List<PbuConstraintAssociationResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/contextual-behaviors/{behaviorId}")
    public ResponseEntity<ApiResponse<String>> removePbuContextualBehavior(
            @PathVariable String pbuId, @PathVariable String behaviorId) {
        pbuAssociationService.removePbuContextualBehaviorAssociation(pbuId, behaviorId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "PBU-Contextual Behavior association removed successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private PbuConstraintAssociationResponseDTO convertToResponseDTO(String pbuId, String constraintId, String behaviorId, String associationType, String status) {
        return new PbuConstraintAssociationResponseDTO(pbuId, constraintId, behaviorId, associationType, status);
    }
}
