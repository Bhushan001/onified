package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.ContextualBehaviorRequestDTO;
import com.onified.ai.permission_registry.dto.ContextualBehaviorResponseDTO;
import com.onified.ai.permission_registry.entity.ContextualBehavior;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.ContextualBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/behaviors/contextual")
@RequiredArgsConstructor
public class ContextualBehaviorController {

    private final ContextualBehaviorService contextualBehaviorService;

    /**
     * Creates a new Contextual Behavior.
     * POST /api/behaviors/contextual
     * @param requestDTO The ContextualBehaviorRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created ContextualBehaviorResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ContextualBehaviorResponseDTO>> createContextualBehavior(@RequestBody ContextualBehaviorRequestDTO requestDTO) {
        ContextualBehavior behavior = new ContextualBehavior(
                requestDTO.getBehaviorId(),
                requestDTO.getBehaviorCode(),
                requestDTO.getDisplayName(),
                requestDTO.getConditionLogic(),
                requestDTO.getIsActive()
        );
        ContextualBehavior createdBehavior = contextualBehaviorService.createContextualBehavior(behavior);
        ApiResponse<ContextualBehaviorResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdBehavior));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a Contextual Behavior by its behaviorId.
     * GET /api/behaviors/contextual/{behaviorId}
     * @param behaviorId The ID of the contextual behavior to retrieve.
     * @return ResponseEntity with ApiResponse containing the ContextualBehaviorResponseDTO.
     */
    @GetMapping("/{behaviorId}")
    public ResponseEntity<ApiResponse<ContextualBehaviorResponseDTO>> getContextualBehaviorById(@PathVariable String behaviorId) {
        ContextualBehavior behavior = contextualBehaviorService.getContextualBehaviorById(behaviorId);
        ApiResponse<ContextualBehaviorResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(behavior));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all Contextual Behaviors.
     * GET /api/behaviors/contextual
     * @return ResponseEntity with ApiResponse containing a list of ContextualBehaviorResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContextualBehaviorResponseDTO>>> getAllContextualBehaviors() {
        List<ContextualBehavior> behaviors = contextualBehaviorService.getAllContextualBehaviors();
        List<ContextualBehaviorResponseDTO> responseDTOs = behaviors.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ContextualBehaviorResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing Contextual Behavior.
     * PUT /api/behaviors/contextual/{behaviorId}
     * @param behaviorId The ID of the contextual behavior to update.
     * @param requestDTO The ContextualBehaviorRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated ContextualBehaviorResponseDTO.
     */
    @PutMapping("/{behaviorId}")
    public ResponseEntity<ApiResponse<ContextualBehaviorResponseDTO>> updateContextualBehavior(@PathVariable String behaviorId, @RequestBody ContextualBehaviorRequestDTO requestDTO) {
        ContextualBehavior behavior = new ContextualBehavior(
                requestDTO.getBehaviorId(),
                requestDTO.getBehaviorCode(),
                requestDTO.getDisplayName(),
                requestDTO.getConditionLogic(),
                requestDTO.getIsActive()
        );
        ContextualBehavior updatedBehavior = contextualBehaviorService.updateContextualBehavior(behaviorId, behavior);
        ApiResponse<ContextualBehaviorResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedBehavior));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a Contextual Behavior by its behaviorId.
     * DELETE /api/behaviors/contextual/{behaviorId}
     * @param behaviorId The ID of the contextual behavior to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{behaviorId}")
    public ResponseEntity<ApiResponse<String>> deleteContextualBehavior(@PathVariable String behaviorId) {
        contextualBehaviorService.deleteContextualBehavior(behaviorId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Contextual Behavior deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ContextualBehaviorResponseDTO convertToResponseDTO(ContextualBehavior behavior) {
        return new ContextualBehaviorResponseDTO(
                behavior.getId(),
                behavior.getBehaviorId(),
                behavior.getBehaviorCode(),
                behavior.getDisplayName(),
                behavior.getConditionLogic(),
                behavior.getIsActive(),
                behavior.getCreatedAt(),
                behavior.getUpdatedAt()
        );
    }
}
