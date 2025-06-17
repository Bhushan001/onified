package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.FieldConstraintRequestDTO;
import com.onified.ai.permission_registry.dto.FieldConstraintResponseDTO;
import com.onified.ai.permission_registry.entity.FieldConstraint;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.FieldConstraintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/constraints/field")
@RequiredArgsConstructor
public class FieldConstraintController {

    private final FieldConstraintService fieldConstraintService;


    /**
     * Creates a new Field Constraint.
     * POST /api/constraints/field
     * @param requestDTO The FieldConstraintRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created FieldConstraintResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FieldConstraintResponseDTO>> createFieldConstraint(@RequestBody FieldConstraintRequestDTO requestDTO) {
        FieldConstraint constraint = new FieldConstraint(
                requestDTO.getConstraintId(),
                requestDTO.getEntityName(),
                requestDTO.getFieldName(),
                requestDTO.getAccessType(),
                requestDTO.getConditionLogic(),
                requestDTO.getIsActive()
        );
        FieldConstraint createdConstraint = fieldConstraintService.createFieldConstraint(constraint);
        ApiResponse<FieldConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdConstraint));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a Field Constraint by its ID.
     * GET /api/constraints/field/{constraintId}
     * @param constraintId The ID of the field constraint to retrieve.
     * @return ResponseEntity with ApiResponse containing the FieldConstraintResponseDTO.
     */
    @GetMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<FieldConstraintResponseDTO>> getFieldConstraintById(@PathVariable String constraintId) {
        FieldConstraint constraint = fieldConstraintService.getFieldConstraintById(constraintId);
        ApiResponse<FieldConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(constraint));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all Field Constraints.
     * GET /api/constraints/field
     * @return ResponseEntity with ApiResponse containing a list of FieldConstraintResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FieldConstraintResponseDTO>>> getAllFieldConstraints() {
        List<FieldConstraint> constraints = fieldConstraintService.getAllFieldConstraints();
        List<FieldConstraintResponseDTO> responseDTOs = constraints.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<FieldConstraintResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing Field Constraint.
     * PUT /api/constraints/field/{constraintId}
     * @param constraintId The ID of the field constraint to update.
     * @param requestDTO The FieldConstraintRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated FieldConstraintResponseDTO.
     */
    @PutMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<FieldConstraintResponseDTO>> updateFieldConstraint(@PathVariable String constraintId, @RequestBody FieldConstraintRequestDTO requestDTO) {
        FieldConstraint constraint = new FieldConstraint(
                requestDTO.getConstraintId(),
                requestDTO.getEntityName(),
                requestDTO.getFieldName(),
                requestDTO.getAccessType(),
                requestDTO.getConditionLogic(),
                requestDTO.getIsActive()
        );
        FieldConstraint updatedConstraint = fieldConstraintService.updateFieldConstraint(constraintId, constraint);
        ApiResponse<FieldConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedConstraint));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a Field Constraint by its ID.
     * DELETE /api/constraints/field/{constraintId}
     * @param constraintId The ID of the field constraint to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<String>> deleteFieldConstraint(@PathVariable String constraintId) {
        fieldConstraintService.deleteFieldConstraint(constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Field Constraint deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private FieldConstraintResponseDTO convertToResponseDTO(FieldConstraint constraint) {
        return new FieldConstraintResponseDTO(
                constraint.getConstraintId(),
                constraint.getEntityName(),
                constraint.getFieldName(),
                constraint.getAccessType(),
                constraint.getConditionLogic(),
                constraint.getIsActive(),
                constraint.getCreatedAt(),
                constraint.getUpdatedAt()
        );
    }
}
