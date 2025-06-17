package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.GeneralConstraintRequestDTO;
import com.onified.ai.permission_registry.dto.GeneralConstraintResponseDTO;
import com.onified.ai.permission_registry.entity.GeneralConstraint;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.GeneralConstraintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/constraints/general")
@RequiredArgsConstructor
public class GeneralConstraintController {

    private final GeneralConstraintService generalConstraintService;

    /**
     * Creates a new General Constraint.
     * POST /api/constraints/general
     * @param requestDTO The GeneralConstraintRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created GeneralConstraintResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GeneralConstraintResponseDTO>> createGeneralConstraint(@RequestBody GeneralConstraintRequestDTO requestDTO) {
        GeneralConstraint constraint = new GeneralConstraint(
                requestDTO.getConstraintId(),
                requestDTO.getConstraintName(),
                requestDTO.getTableName(),
                requestDTO.getColumnName(),
                requestDTO.getValueType(),
                requestDTO.getTableValue(),
                requestDTO.getCustomValue(),
                requestDTO.getRuleLogic(),
                requestDTO.getIsActive()
        );
        GeneralConstraint createdConstraint = generalConstraintService.createGeneralConstraint(constraint);
        ApiResponse<GeneralConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdConstraint));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a General Constraint by its ID.
     * GET /api/constraints/general/{constraintId}
     * @param constraintId The ID of the general constraint to retrieve.
     * @return ResponseEntity with ApiResponse containing the GeneralConstraintResponseDTO.
     */
    @GetMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<GeneralConstraintResponseDTO>> getGeneralConstraintById(@PathVariable String constraintId) {
        GeneralConstraint constraint = generalConstraintService.getGeneralConstraintById(constraintId);
        ApiResponse<GeneralConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(constraint));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all General Constraints.
     * GET /api/constraints/general
     * @return ResponseEntity with ApiResponse containing a list of GeneralConstraintResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<GeneralConstraintResponseDTO>>> getAllGeneralConstraints() {
        List<GeneralConstraint> constraints = generalConstraintService.getAllGeneralConstraints();
        List<GeneralConstraintResponseDTO> responseDTOs = constraints.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<GeneralConstraintResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing General Constraint.
     * PUT /api/constraints/general/{constraintId}
     * @param constraintId The ID of the general constraint to update.
     * @param requestDTO The GeneralConstraintRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated GeneralConstraintResponseDTO.
     */
    @PutMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<GeneralConstraintResponseDTO>> updateGeneralConstraint(@PathVariable String constraintId, @RequestBody GeneralConstraintRequestDTO requestDTO) {
        GeneralConstraint constraint = new GeneralConstraint(
                requestDTO.getConstraintId(), // Use ID from request body as well, but path variable is primary
                requestDTO.getConstraintName(),
                requestDTO.getTableName(),
                requestDTO.getColumnName(),
                requestDTO.getValueType(),
                requestDTO.getTableValue(),
                requestDTO.getCustomValue(),
                requestDTO.getRuleLogic(),
                requestDTO.getIsActive()
        );
        GeneralConstraint updatedConstraint = generalConstraintService.updateGeneralConstraint(constraintId, constraint);
        ApiResponse<GeneralConstraintResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedConstraint));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a General Constraint by its ID.
     * DELETE /api/constraints/general/{constraintId}
     * @param constraintId The ID of the general constraint to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{constraintId}")
    public ResponseEntity<ApiResponse<String>> deleteGeneralConstraint(@PathVariable String constraintId) {
        generalConstraintService.deleteGeneralConstraint(constraintId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "General Constraint deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private GeneralConstraintResponseDTO convertToResponseDTO(GeneralConstraint constraint) {
        return new GeneralConstraintResponseDTO(
                constraint.getConstraintId(),
                constraint.getConstraintName(),
                constraint.getTableName(),
                constraint.getColumnName(),
                constraint.getValueType(),
                constraint.getTableValue(),
                constraint.getCustomValue(),
                constraint.getRuleLogic(),
                constraint.getIsActive(),
                constraint.getCreatedAt(),
                constraint.getUpdatedAt()
        );
    }
}

