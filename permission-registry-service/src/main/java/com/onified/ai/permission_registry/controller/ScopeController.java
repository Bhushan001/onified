package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.ScopeRequestDTO;
import com.onified.ai.permission_registry.dto.ScopeResponseDTO;
import com.onified.ai.permission_registry.entity.Scope;
import com.onified.ai.permission_registry.model.ApiResponse;
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
    public ResponseEntity<ApiResponse<ScopeResponseDTO>> createScope(@RequestBody ScopeRequestDTO requestDTO) {
        Scope scope = new Scope(requestDTO.getScopeCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Scope createdScope = scopeService.createScope(scope);
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
    public ResponseEntity<ApiResponse<ScopeResponseDTO>> getScopeByCode(@PathVariable String scopeCode) {
        Scope scope = scopeService.getScopeByCode(scopeCode);
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
    public ResponseEntity<ApiResponse<ScopeResponseDTO>> updateScope(@PathVariable String scopeCode, @RequestBody ScopeRequestDTO requestDTO) {
        Scope scope = new Scope(requestDTO.getScopeCode(), requestDTO.getDisplayName(), requestDTO.getDescription(), requestDTO.getIsActive());
        Scope updatedScope = scopeService.updateScope(scopeCode, scope);
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
    public ResponseEntity<ApiResponse<String>> deleteScope(@PathVariable String scopeCode) {
        scopeService.deleteScope(scopeCode);
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

