package com.onified.ai.platform_management.controller;

import com.onified.ai.platform_management.constants.MessageConstants;
import com.onified.ai.platform_management.dto.ApiResponse;
import com.onified.ai.platform_management.dto.TenantDTO;
import com.onified.ai.platform_management.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getAllTenants() {
        List<TenantDTO> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, tenants));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantDTO>> getTenant(@PathVariable String tenantId) {
        TenantDTO tenant = tenantService.getTenant(tenantId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, tenant));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TenantDTO>> createTenant(@RequestBody TenantDTO tenantDTO) {
        // In a real app, get createdBy from security context
        TenantDTO created = tenantService.createTenant(tenantDTO, "system");
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED.value(), MessageConstants.STATUS_SUCCESS, created), HttpStatus.CREATED);
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenant(@PathVariable String tenantId, @RequestBody TenantDTO tenantDTO) {
        // In a real app, get updatedBy from security context
        TenantDTO updated = tenantService.updateTenant(tenantId, tenantDTO, "system");
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, updated));
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable String tenantId) {
        tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), MessageConstants.STATUS_SUCCESS, null));
    }
} 