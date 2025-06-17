package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.ActionRequestDTO;
import com.onified.ai.permission_registry.dto.ActionResponseDTO;
import com.onified.ai.permission_registry.entity.Action;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.service.ActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    /**
     * Creates a new action.
     * POST /api/actions
     * @param requestDTO The ActionRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created ActionResponseDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ActionResponseDTO>> createAction(@RequestBody ActionRequestDTO requestDTO) {
        Action action = new Action(requestDTO.getActionCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Action createdAction = actionService.createAction(action);
        ApiResponse<ActionResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdAction));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves an action by its actionCode.
     * GET /api/actions/{actionCode}
     * @param actionCode The code of the action to retrieve.
     * @return ResponseEntity with ApiResponse containing the ActionResponseDTO.
     */
    @GetMapping("/{actionCode}")
    public ResponseEntity<ApiResponse<ActionResponseDTO>> getActionByCode(@PathVariable String actionCode) {
        Action action = actionService.getActionByCode(actionCode);
        ApiResponse<ActionResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(action));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all actions.
     * GET /api/actions
     * @return ResponseEntity with ApiResponse containing a list of ActionResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActionResponseDTO>>> getAllActions() {
        List<Action> actions = actionService.getAllActions();
        List<ActionResponseDTO> responseDTOs = actions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ActionResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing action.
     * PUT /api/actions/{actionCode}
     * @param actionCode The code of the action to update.
     * @param requestDTO The ActionRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated ActionResponseDTO.
     */
    @PutMapping("/{actionCode}")
    public ResponseEntity<ApiResponse<ActionResponseDTO>> updateAction(@PathVariable String actionCode, @RequestBody ActionRequestDTO requestDTO) {
        Action action = new Action(requestDTO.getActionCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Action updatedAction = actionService.updateAction(actionCode, action);
        ApiResponse<ActionResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedAction));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes an action by its actionCode.
     * DELETE /api/actions/{actionCode}
     * @param actionCode The code of the action to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{actionCode}")
    public ResponseEntity<ApiResponse<String>> deleteAction(@PathVariable String actionCode) {
        actionService.deleteAction(actionCode);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Action deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ActionResponseDTO convertToResponseDTO(Action action) {
        return new ActionResponseDTO(
                action.getActionCode(),
                action.getDisplayName(),
                action.getDescription(),
                action.getIsActive(),
                action.getCreatedAt(),
                action.getUpdatedAt()
        );
    }
}
