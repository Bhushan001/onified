package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataFieldOverride;
import com.onified.platform.metadata.repository.MetadataFieldOverrideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/field-overrides")
@RequiredArgsConstructor
@Slf4j
public class MetadataFieldOverrideController {

    private final MetadataFieldOverrideRepository fieldOverrideRepository;

    @GetMapping
    public ResponseEntity<List<MetadataFieldOverride>> getAllFieldOverrides() {
        log.debug("Fetching all field overrides");
        List<MetadataFieldOverride> overrides = fieldOverrideRepository.findAll();
        return ResponseEntity.ok(overrides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataFieldOverride> getFieldOverrideById(@PathVariable UUID id) {
        log.debug("Fetching field override by ID: {}", id);
        Optional<MetadataFieldOverride> override = fieldOverrideRepository.findById(id);
        return override.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataFieldOverride>> getFieldOverridesByTenantAppAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String fieldName) {
        log.debug("Fetching field overrides by tenant: {}, app: {}, field: {}", tenantCode, appCode, fieldName);
        List<MetadataFieldOverride> overrides = fieldOverrideRepository
                .findByTenantCodeAndAppCodeAndFieldNameAndActive(tenantCode, appCode, fieldName);
        return ResponseEntity.ok(overrides);
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/table/{tableCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataFieldOverride>> getFieldOverridesByTenantAppTableAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String fieldName) {
        log.debug("Fetching field overrides by tenant: {}, app: {}, table: {}, field: {}",
                tenantCode, appCode, tableCode, fieldName);
        List<MetadataFieldOverride> overrides = fieldOverrideRepository
                .findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(tenantCode, appCode, tableCode, fieldName);
        return ResponseEntity.ok(overrides);
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/type/{overrideType}")
    public ResponseEntity<List<MetadataFieldOverride>> getFieldOverridesByTenantAppAndType(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String overrideType) {
        log.debug("Fetching field overrides by tenant: {}, app: {}, type: {}", tenantCode, appCode, overrideType);
        List<MetadataFieldOverride> overrides = fieldOverrideRepository
                .findByTenantCodeAndAppCodeAndOverrideTypeAndActive(tenantCode, appCode, overrideType);
        return ResponseEntity.ok(overrides);
    }

    @GetMapping("/table/{tableCode}/app/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataFieldOverride>> getFieldOverridesByTableAppAndTenant(
            @PathVariable String tableCode,
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching field overrides by table: {}, app: {}, tenant: {}", tableCode, appCode, tenantCode);
        List<MetadataFieldOverride> overrides = fieldOverrideRepository
                .findByTableCodeAndAppCodeAndTenantCodeAndActive(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(overrides);
    }

    @PostMapping
    public ResponseEntity<MetadataFieldOverride> createFieldOverride(@RequestBody MetadataFieldOverride override) {
        log.debug("Creating new field override for tenant: {} and field: {}",
                override.getTenant() != null ? override.getTenant().getTenantCode() : "unknown",
                override.getStandardField() != null ? override.getStandardField().getFieldName() : "unknown");
        MetadataFieldOverride savedOverride = fieldOverrideRepository.save(override);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOverride);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataFieldOverride> updateFieldOverride(@PathVariable UUID id, @RequestBody MetadataFieldOverride override) {
        log.debug("Updating field override with ID: {}", id);
        if (!fieldOverrideRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        override.setOverrideId(id);
        MetadataFieldOverride updatedOverride = fieldOverrideRepository.save(override);
        return ResponseEntity.ok(updatedOverride);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFieldOverride(@PathVariable UUID id) {
        log.debug("Deleting field override with ID: {}", id);
        if (!fieldOverrideRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fieldOverrideRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
