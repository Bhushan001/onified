package com.onified.ai.platform_management.controller;

import com.onified.ai.platform_management.constants.MessageConstants;
import com.onified.ai.platform_management.dto.ApiResponse;
import com.onified.ai.platform_management.dto.TenantDTO;
import com.onified.ai.platform_management.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "APIs for managing multi-tenant configurations and tenant lifecycle operations")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping
    @Operation(
        summary = "Get All Tenants",
        description = "Retrieves all tenants in the system with their configurations",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "List of tenants retrieved successfully",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            )
        }
    )
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getAllTenants() {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, tenants));
    }

    @GetMapping("/{tenantId}")
    @Operation(
        summary = "Get Tenant by ID",
        description = "Retrieves a specific tenant by its unique identifier",
        parameters = {
            @Parameter(name = "tenantId", description = "Unique identifier of the tenant", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Tenant found and retrieved successfully",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Tenant not found"
            )
        }
    )
    public ResponseEntity<ApiResponse<TenantDTO>> getTenant(@PathVariable String tenantId) {
        TenantDTO tenant = tenantService.getTenant(tenantId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, tenant));
    }

    @PostMapping
    @Operation(
        summary = "Create New Tenant",
        description = "Creates a new tenant with the specified configuration",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Tenant configuration details",
            required = true,
            content = @Content(schema = @Schema(implementation = TenantDTO.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Tenant created successfully",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid tenant configuration"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Tenant with the same ID already exists"
            )
        }
    )
    public ResponseEntity<ApiResponse<TenantDTO>> createTenant(@RequestBody TenantDTO tenantDTO) {
        // In a real app, get createdBy from security context
        TenantDTO created = tenantService.createTenant(tenantDTO, "system");
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED.value(), MessageConstants.STATUS_SUCCESS, created), HttpStatus.CREATED);
    }

    @PutMapping("/{tenantId}")
    @Operation(
        summary = "Update Tenant",
        description = "Updates an existing tenant with new configuration",
        parameters = {
            @Parameter(name = "tenantId", description = "Unique identifier of the tenant to update", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated tenant configuration",
            required = true,
            content = @Content(schema = @Schema(implementation = TenantDTO.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Tenant updated successfully",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid tenant configuration"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Tenant not found"
            )
        }
    )
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenant(@PathVariable String tenantId, @RequestBody TenantDTO tenantDTO) {
        // In a real app, get updatedBy from security context
        TenantDTO updated = tenantService.updateTenant(tenantId, tenantDTO, "system");
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, updated));
    }

    @DeleteMapping("/{tenantId}")
    @Operation(
        summary = "Delete Tenant",
        description = "Deletes a tenant and all its associated data. This operation is irreversible.",
        parameters = {
            @Parameter(name = "tenantId", description = "Unique identifier of the tenant to delete", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Tenant deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Tenant not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Cannot delete tenant - has active resources or dependencies"
            )
        }
    )
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable String tenantId) {
        tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, null));
    }
} 