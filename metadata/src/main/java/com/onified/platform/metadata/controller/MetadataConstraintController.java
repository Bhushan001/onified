package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataConstraint;
import com.onified.platform.metadata.repository.MetadataConstraintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/constraints")
@RequiredArgsConstructor
@Slf4j
public class MetadataConstraintController {

    private final MetadataConstraintRepository constraintRepository;

    @GetMapping
    public ResponseEntity<List<MetadataConstraint>> getAllConstraints() {
        log.debug("Fetching all constraints");
        List<MetadataConstraint> constraints = constraintRepository.findAll();
        return ResponseEntity.ok(constraints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataConstraint> getConstraintById(@PathVariable UUID id) {
        log.debug("Fetching constraint by ID: {}", id);
        Optional<MetadataConstraint> constraint = constraintRepository.findById(id);
        return constraint.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataConstraint>> getConstraintsByTenantAppAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String fieldName) {
        log.debug("Fetching constraints by tenant: {}, app: {}, field: {}", tenantCode, appCode, fieldName);
        List<MetadataConstraint> constraints = constraintRepository
                .findByTenantCodeAndAppCodeAndFieldNameAndActive(tenantCode, appCode, fieldName);
        return ResponseEntity.ok(constraints);
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/table/{tableCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataConstraint>> getConstraintsByTenantAppTableAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String fieldName) {
        log.debug("Fetching constraints by tenant: {}, app: {}, table: {}, field: {}",
                tenantCode, appCode, tableCode, fieldName);
        List<MetadataConstraint> constraints = constraintRepository
                .findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(tenantCode, appCode, tableCode, fieldName);
        return ResponseEntity.ok(constraints);
    }

    @GetMapping("/table/{tableCode}/app/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataConstraint>> getConstraintsByTableAppAndTenant(
            @PathVariable String tableCode,
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching constraints by table: {}, app: {}, tenant: {}", tableCode, appCode, tenantCode);
        List<MetadataConstraint> constraints = constraintRepository
                .findByTableCodeAndAppCodeAndTenantCodeAndActive(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(constraints);
    }

    @GetMapping("/format/{formatType}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataConstraint>> getConstraintsByFormatTypeAndTenant(
            @PathVariable String formatType,
            @PathVariable String tenantCode) {
        log.debug("Fetching constraints by format type: {} and tenant: {}", formatType, tenantCode);
        List<MetadataConstraint> constraints = constraintRepository
                .findByFormatTypeAndTenantCodeAndActive(formatType, tenantCode);
        return ResponseEntity.ok(constraints);
    }

    @PostMapping
    public ResponseEntity<MetadataConstraint> createConstraint(@RequestBody MetadataConstraint constraint) {
        log.debug("Creating new constraint for field: {}",
                constraint.getStandardField() != null ? constraint.getStandardField().getFieldName() : "unknown");
        MetadataConstraint savedConstraint = constraintRepository.save(constraint);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedConstraint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataConstraint> updateConstraint(@PathVariable UUID id, @RequestBody MetadataConstraint constraint) {
        log.debug("Updating constraint with ID: {}", id);
        if (!constraintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        constraint.setConstraintId(id);
        MetadataConstraint updatedConstraint = constraintRepository.save(constraint);
        return ResponseEntity.ok(updatedConstraint);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConstraint(@PathVariable UUID id) {
        log.debug("Deleting constraint with ID: {}", id);
        if (!constraintRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        constraintRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
