package com.onified.ai.permission_registry.controller;

import com.onified.ai.permission_registry.dto.PermissionBundleUnitRequestDTO;
import com.onified.ai.permission_registry.dto.PermissionBundleUnitResponseDTO;
import com.onified.ai.permission_registry.entity.PermissionBundleUnit;
import com.onified.ai.permission_registry.model.ApiResponse;
import com.onified.ai.permission_registry.model.CustomErrorResponse;
import com.onified.ai.permission_registry.service.PermissionBundleUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pbus")
@RequiredArgsConstructor
public class PermissionBundleUnitController {

    private final PermissionBundleUnitService pbuService;

    /**
     * Creates a new Permission Bundle Unit (PBU).
     * POST /api/pbus
     * @param requestDTO The PermissionBundleUnitRequestDTO from the request body.
     * @return ResponseEntity with ApiResponse containing the created PermissionBundleUnitResponseDTO.
     */
    @PostMapping
    public ResponseEntity<?> createPbu(@RequestBody PermissionBundleUnitRequestDTO requestDTO) {
        PermissionBundleUnit pbu = new PermissionBundleUnit(
                requestDTO.getPbuId(),
                requestDTO.getDisplayName(),
                requestDTO.getApiEndpoint(),
                requestDTO.getActionCode(),
                requestDTO.getScopeCode(),
                requestDTO.getIsActive(),
                requestDTO.getVersion()
        );
        PermissionBundleUnit createdPbu = pbuService.createPbu(pbu);
        
        if (createdPbu == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("CONFLICT", "PBU with this ID already exists or validation failed");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), "CONFLICT", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        
        ApiResponse<PermissionBundleUnitResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdPbu));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a PBU by its pbuId.
     * GET /api/pbus/{pbuId}
     * @param pbuId The ID of the PBU to retrieve.
     * @return ResponseEntity with ApiResponse containing the PermissionBundleUnitResponseDTO.
     */
    @GetMapping("/{pbuId}")
    public ResponseEntity<?> getPbuById(@PathVariable String pbuId) {
        PermissionBundleUnit pbu = pbuService.getPbuById(pbuId);
        
        if (pbu == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "PBU not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<PermissionBundleUnitResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(pbu));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves all PBUs.
     * GET /api/pbus
     * @return ResponseEntity with ApiResponse containing a list of PermissionBundleUnitResponseDTOs.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionBundleUnitResponseDTO>>> getAllPbus() {
        List<PermissionBundleUnit> pbus = pbuService.getAllPbus();
        List<PermissionBundleUnitResponseDTO> responseDTOs = pbus.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<PermissionBundleUnitResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Updates an existing PBU.
     * PUT /api/pbus/{pbuId}
     * @param pbuId The ID of the PBU to update.
     * @param requestDTO The PermissionBundleUnitRequestDTO with updated details.
     * @return ResponseEntity with ApiResponse containing the updated PermissionBundleUnitResponseDTO.
     */
    @PutMapping("/{pbuId}")
    public ResponseEntity<?> updatePbu(@PathVariable String pbuId, @RequestBody PermissionBundleUnitRequestDTO requestDTO) {
        PermissionBundleUnit pbu = new PermissionBundleUnit(
                requestDTO.getPbuId(), // Should match path variable
                requestDTO.getDisplayName(),
                requestDTO.getApiEndpoint(),
                requestDTO.getActionCode(),
                requestDTO.getScopeCode(),
                requestDTO.getIsActive(),
                requestDTO.getVersion()
        );
        PermissionBundleUnit updatedPbu = pbuService.updatePbu(pbuId, pbu);
        
        if (updatedPbu == null) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "PBU not found or validation failed");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<PermissionBundleUnitResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedPbu));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deletes a PBU by its pbuId.
     * DELETE /api/pbus/{pbuId}
     * @param pbuId The ID of the PBU to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{pbuId}")
    public ResponseEntity<?> deletePbu(@PathVariable String pbuId) {
        boolean deleted = pbuService.deletePbu(pbuId);
        
        if (!deleted) {
            CustomErrorResponse errorResponse = new CustomErrorResponse("NOT_FOUND", "PBU not found");
            ApiResponse<CustomErrorResponse> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", errorResponse);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Permission Bundle Unit deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private PermissionBundleUnitResponseDTO convertToResponseDTO(PermissionBundleUnit pbu) {
        return new PermissionBundleUnitResponseDTO(
                pbu.getPbuId(),
                pbu.getDisplayName(),
                pbu.getApiEndpoint(),
                pbu.getActionCode(),
                pbu.getScopeCode(),
                pbu.getIsActive(),
                pbu.getVersion(),
                pbu.getCreatedAt(),
                pbu.getUpdatedAt()
        );
    }
}
