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
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

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
        try {
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
            ApiResponse<PermissionBundleUnitResponseDTO> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdPbu));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
    }

    /**
     * Retrieves a PBU by its pbuId.
     * GET /api/pbus/{pbuId}
     * @param pbuId The ID of the PBU to retrieve.
     * @return ResponseEntity with ApiResponse containing the PermissionBundleUnitResponseDTO.
     */
    @GetMapping("/{pbuId}")
    public ResponseEntity<ApiResponse<PermissionBundleUnitResponseDTO>> getPbuById(@PathVariable String pbuId) {
        PermissionBundleUnit pbu = pbuService.getPbuById(pbuId);
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
        try {
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
            ApiResponse<PermissionBundleUnitResponseDTO> response = new ApiResponse<>(
                    HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedPbu));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
    }

    /**
     * Deletes a PBU by its pbuId.
     * DELETE /api/pbus/{pbuId}
     * @param pbuId The ID of the PBU to delete.
     * @return ResponseEntity with ApiResponse indicating success.
     */
    @DeleteMapping("/{pbuId}")
    public ResponseEntity<ApiResponse<String>> deletePbu(@PathVariable String pbuId) {
        pbuService.deletePbu(pbuId);
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
