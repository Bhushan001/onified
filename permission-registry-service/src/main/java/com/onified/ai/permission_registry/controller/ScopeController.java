package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.ScopeRequestDTO;
import com.onified.ai.permission_registry.dto.ScopeResponseDTO;
import com.onified.ai.permission_registry.entity.Scope;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.model.CustomErrorResponse;
import com.onified.ai.permission_registry.service.ScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scopes")
@RequiredArgsConstructor
public class ScopeController {

    private final ScopeService scopeService;

    /**
     * Creates a new scope.
     * POST /api/scopes
     * @param requestDTO The ScopeRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created ScopeResponseDTO.
     */
    @PostMapping
    public ResponseEntity<?> createScope(@RequestBody ScopeRequestDTO requestDTO) {
        Scope scope = new Scope(requestDTO.getScopeCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Scope createdScope = scopeService.createScope(scope);
        
        if (createdScope == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("CONFLICT", "Scope with this code already exists");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), "CONFLICT", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        
        ApiResponse<ScopeResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdScope));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a scope by its scopeCode.
     * GET /api/scopes/{scopeCode}
     * @param scopeCode The code of the scope to retrieve.
     * @return ResponseEntity with ApiResponse containing the ScopeResponseDTO.
     */
    @GetMapping("/{scopeCode}")
    public ResponseEntity<?> getScopeByCode(@PathVariable String scopeCode) {
        Scope scope = scopeService.getScopeByCode(scopeCode);
        
        if (scope == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Scope not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<ScopeResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(scope));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all scopes.
     * GET /api/scopes
     * @return ResponseEntity with ApiResponse containing a list of ScopeResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScopeResponseDTO>>> getAllScopes() {
        List<Scope> scopes = scopeService.getAllScopes();
        List<ScopeResponseDTO> responseDTOs = scopes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ScopeResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing scope.
     * PUT /api/scopes/{scopeCode}
     * @param scopeCode The code of the scope to update.
     * @param requestDTO The ScopeRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated ScopeResponseDTO.
     */
    @PutMapping("/{scopeCode}")
    public ResponseEntity<?> updateScope(@PathVariable String scopeCode, @RequestBody ScopeRequestDTO requestDTO) {
        Scope scope = new Scope(requestDTO.getScopeCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Scope updatedScope = scopeService.updateScope(scopeCode, scope);
        
        if (updatedScope == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Scope not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<ScopeResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedScope));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a scope by its scopeCode.
     * DELETE /api/scopes/{scopeCode}
     * @param scopeCode The code of the scope to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{scopeCode}")
    public ResponseEntity<?> deleteScope(@PathVariable String scopeCode) {
        boolean deleted = scopeService.deleteScope(scopeCode);
        
        if (!deleted) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "Scope not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Scope deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ScopeResponseDTO convertToResponseDTO(Scope scope) {
        return new ScopeResponseDTO(
                scope.getScopeCode(),
                scope.getDisplayName(),
                scope.getDescription(),
                scope.getIsActive(),
                scope.getCreatedAt(),
                scope.getUpdatedAt()
        );
    }
}

