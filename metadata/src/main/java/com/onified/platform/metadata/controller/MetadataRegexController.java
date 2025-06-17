package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataRegex;
import com.onified.platform.metadata.repository.MetadataRegexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/regex")
@RequiredArgsConstructor
@Slf4j
public class MetadataRegexController {

    private final MetadataRegexRepository regexRepository;

    @GetMapping
    public ResponseEntity<List<MetadataRegex>> getAllRegexPatterns() {
        log.debug("Fetching all regex patterns");
        List<MetadataRegex> patterns = regexRepository.findAll();
        return ResponseEntity.ok(patterns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataRegex> getRegexPatternById(@PathVariable UUID id) {
        log.debug("Fetching regex pattern by ID: {}", id);
        Optional<MetadataRegex> pattern = regexRepository.findById(id);
        return pattern.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataRegex>> getRegexPatternsByTenantAppAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String fieldName) {
        log.debug("Fetching regex patterns by tenant: {}, app: {}, field: {}", tenantCode, appCode, fieldName);
        List<MetadataRegex> patterns = regexRepository
                .findByTenantCodeAndAppCodeAndFieldNameAndActive(tenantCode, appCode, fieldName);
        return ResponseEntity.ok(patterns);
    }

    @GetMapping("/tenant/{tenantCode}/app/{appCode}/table/{tableCode}/field/{fieldName}")
    public ResponseEntity<List<MetadataRegex>> getRegexPatternsByTenantAppTableAndField(
            @PathVariable String tenantCode,
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String fieldName) {
        log.debug("Fetching regex patterns by tenant: {}, app: {}, table: {}, field: {}",
                tenantCode, appCode, tableCode, fieldName);
        List<MetadataRegex> patterns = regexRepository
                .findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(tenantCode, appCode, tableCode, fieldName);
        return ResponseEntity.ok(patterns);
    }

    @GetMapping("/table/{tableCode}/app/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataRegex>> getRegexPatternsByTableAppAndTenant(
            @PathVariable String tableCode,
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching regex patterns by table: {}, app: {}, tenant: {}", tableCode, appCode, tenantCode);
        List<MetadataRegex> patterns = regexRepository
                .findByTableCodeAndAppCodeAndTenantCodeAndActive(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(patterns);
    }

    @GetMapping("/pattern/{pattern}/tenant/{tenantCode}")
    public ResponseEntity<MetadataRegex> getRegexPatternByPatternAndTenant(
            @PathVariable String pattern,
            @PathVariable String tenantCode) {
        log.debug("Fetching regex pattern by pattern: {} and tenant: {}", pattern, tenantCode);
        Optional<MetadataRegex> regexPattern = regexRepository
                .findByPatternAndTenantCodeAndActive(pattern, tenantCode);
        return regexPattern.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetadataRegex> createRegexPattern(@RequestBody MetadataRegex pattern) {
        log.debug("Creating new regex pattern for field: {}",
                pattern.getStandardField() != null ? pattern.getStandardField().getFieldName() : "unknown");
        MetadataRegex savedPattern = regexRepository.save(pattern);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPattern);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataRegex> updateRegexPattern(@PathVariable UUID id, @RequestBody MetadataRegex pattern) {
        log.debug("Updating regex pattern with ID: {}", id);
        if (!regexRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pattern.setRegexId(id);
        MetadataRegex updatedPattern = regexRepository.save(pattern);
        return ResponseEntity.ok(updatedPattern);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegexPattern(@PathVariable UUID id) {
        log.debug("Deleting regex pattern with ID: {}", id);
        if (!regexRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        regexRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
