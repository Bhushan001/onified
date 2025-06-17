package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataMasking;
import com.onified.platform.metadata.repository.MetadataMaskingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/masking")
@RequiredArgsConstructor
@Slf4j
public class MetadataMaskingController {

    private final MetadataMaskingRepository maskingRepository;

    @GetMapping
    public ResponseEntity<List<MetadataMasking>> getAllMaskingRules() {
        log.debug("Fetching all masking rules");
        List<MetadataMasking> rules = maskingRepository.findAll();
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataMasking> getMaskingRuleById(@PathVariable UUID id) {
        log.debug("Fetching masking rule by ID: {}", id);
        Optional<MetadataMasking> rule = maskingRepository.findById(id);
        return rule.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataMasking>> getMaskingRulesByTenantAppAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String fieldName) {
        log.debug("Fetching masking rules by tenant: {}, app: {}, field: {}", tenantCode, appCode, fieldName);
        List<MetadataMasking> rules = maskingRepository
                .findByTenantCodeAndAppCodeAndFieldNameAndActive(tenantCode, appCode, fieldName);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/table/{tableCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataMasking>> getMaskingRulesByTenantAppTableAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String fieldName) {
        log.debug("Fetching masking rules by tenant: {}, app: {}, table: {}, field: {}",
                tenantCode, appCode, tableCode, fieldName);
        List<MetadataMasking> rules = maskingRepository
                .findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(tenantCode, appCode, tableCode, fieldName);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/table/{tableCode}/app/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataMasking>> getMaskingRulesByTableAppAndTenant(
            @PathVariable String tableCode,
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching masking rules by table: {}, app: {}, tenant: {}", tableCode, appCode, tenantCode);
        List<MetadataMasking> rules = maskingRepository
                .findByTableCodeAndAppCodeAndTenantCodeAndActive(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(rules);
    }

    @GetMapping("/type/{maskingType}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataMasking>> getMaskingRulesByTypeAndTenant(
            @PathVariable String maskingType,
            @PathVariable String tenantCode) {
        log.debug("Fetching masking rules by type: {} and tenant: {}", maskingType, tenantCode);
        List<MetadataMasking> rules = maskingRepository
                .findByMaskingTypeAndTenantCodeAndActive(maskingType, tenantCode);
        return ResponseEntity.ok(rules);
    }

    @PostMapping
    public ResponseEntity<MetadataMasking> createMaskingRule(@RequestBody MetadataMasking rule) {
        log.debug("Creating new masking rule for field: {}",
                rule.getStandardField() != null ? rule.getStandardField().getFieldName() : "unknown");
        MetadataMasking savedRule = maskingRepository.save(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataMasking> updateMaskingRule(@PathVariable UUID id, @RequestBody MetadataMasking rule) {
        log.debug("Updating masking rule with ID: {}", id);
        if (!maskingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rule.setMaskingId(id);
        MetadataMasking updatedRule = maskingRepository.save(rule);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaskingRule(@PathVariable UUID id) {
        log.debug("Deleting masking rule with ID: {}", id);
        if (!maskingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        maskingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
