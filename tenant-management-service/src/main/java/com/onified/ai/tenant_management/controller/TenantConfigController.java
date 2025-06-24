package com.onified.ai.tenant_management.controller;

import com.onified.ai.tenant_management.entity.TenantConfig;
import com.onified.ai.tenant_management.service.TenantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants/{tenantId}/config")
public class TenantConfigController {
    @Autowired
    private TenantConfigService tenantConfigService;

    @GetMapping
    public ResponseEntity<TenantConfig> getConfig(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenantConfigService.getConfig(tenantId));
    }

    @PutMapping
    public ResponseEntity<TenantConfig> updateConfig(
            @PathVariable String tenantId,
            @RequestBody TenantConfig config) {
        return ResponseEntity.ok(tenantConfigService.updateConfig(tenantId, config));
    }
} 