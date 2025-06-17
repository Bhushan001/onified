package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataTenant;
import com.onified.platform.metadata.repository.MetadataTenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/tenants")
@RequiredArgsConstructor
@Slf4j
public class MetadataTenantController {

    private final MetadataTenantRepository tenantRepository;

    @GetMapping
    public ResponseEntity<List<MetadataTenant>> getAllTenants() {
        log.debug("Fetching all tenants");
        List<MetadataTenant> tenants = tenantRepository.findAll();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MetadataTenant>> getAllActiveTenants() {
        log.debug("Fetching all active tenants");
        List<MetadataTenant> tenants = tenantRepository.findAllActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataTenant> getTenantById(@PathVariable UUID id) {
        log.debug("Fetching tenant by ID: {}", id);
        Optional<MetadataTenant> tenant = tenantRepository.findById(id);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{tenantCode}")
    public ResponseEntity<MetadataTenant> getTenantByCode(@PathVariable String tenantCode) {
        log.debug("Fetching tenant by code: {}", tenantCode);
        Optional<MetadataTenant> tenant = tenantRepository.findByTenantCodeAndActive(tenantCode);
        return tenant.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MetadataTenant>> searchTenantsByName(@RequestParam String tenantName) {
        log.debug("Searching tenants by name: {}", tenantName);
        List<MetadataTenant> tenants = tenantRepository.findByTenantNameContainingAndActive(tenantName);
        return ResponseEntity.ok(tenants);
    }

    @PostMapping
    public ResponseEntity<MetadataTenant> createTenant(@RequestBody MetadataTenant tenant) {
        log.debug("Creating new tenant: {}", tenant.getTenantCode());
        if (tenantRepository.existsByTenantCode(tenant.getTenantCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        MetadataTenant savedTenant = tenantRepository.save(tenant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTenant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataTenant> updateTenant(@PathVariable UUID id, @RequestBody MetadataTenant tenant) {
        log.debug("Updating tenant with ID: {}", id);
        if (!tenantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tenant.setTenantId(id);
        MetadataTenant updatedTenant = tenantRepository.save(tenant);
        return ResponseEntity.ok(updatedTenant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        log.debug("Deleting tenant with ID: {}", id);
        if (!tenantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tenantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{tenantCode}")
    public ResponseEntity<Boolean> existsByTenantCode(@PathVariable String tenantCode) {
        log.debug("Checking if tenant exists by code: {}", tenantCode);
        boolean exists = tenantRepository.existsByTenantCode(tenantCode);
        return ResponseEntity.ok(exists);
    }
}
