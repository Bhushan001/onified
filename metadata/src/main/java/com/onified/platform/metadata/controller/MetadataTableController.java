package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataTable;
import com.onified.platform.metadata.repository.MetadataTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/tables")
@RequiredArgsConstructor
@Slf4j
public class MetadataTableController {

    private final MetadataTableRepository tableRepository;

    @GetMapping
    public ResponseEntity<List<MetadataTable>> getAllTables() {
        log.debug("Fetching all tables");
        List<MetadataTable> tables = tableRepository.findAll();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataTable> getTableById(@PathVariable UUID id) {
        log.debug("Fetching table by ID: {}", id);
        Optional<MetadataTable> table = tableRepository.findById(id);
        return table.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{tableCode}")
    public ResponseEntity<MetadataTable> getTableByCode(@PathVariable String tableCode) {
        log.debug("Fetching table by code: {}", tableCode);
        Optional<MetadataTable> table = tableRepository.findByTableCodeAndActive(tableCode);
        return table.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/app/{appCode}")
    public ResponseEntity<List<MetadataTable>> getTablesByAppCode(@PathVariable String appCode) {
        log.debug("Fetching tables by app code: {}", appCode);
        List<MetadataTable> tables = tableRepository.findByAppCodeAndActive(appCode);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataTable>> getTablesByTenantCode(@PathVariable String tenantCode) {
        log.debug("Fetching tables by tenant code: {}", tenantCode);
        List<MetadataTable> tables = tableRepository.findByTenantCodeAndActive(tenantCode);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/app/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataTable>> getTablesByAppCodeAndTenantCode(
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching tables by app code: {} and tenant code: {}", appCode, tenantCode);
        List<MetadataTable> tables = tableRepository.findByAppCodeAndTenantCodeAndActive(appCode, tenantCode);
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/app/{appCode}/tenant/{tenantCode}/table/{tableCode}")
    public ResponseEntity<MetadataTable> getTableByAppCodeAndTenantCodeAndTableCode(
            @PathVariable String appCode,
            @PathVariable String tenantCode,
            @PathVariable String tableCode) {
        log.debug("Fetching table by app code: {}, tenant code: {}, table code: {}", appCode, tenantCode, tableCode);
        Optional<MetadataTable> table = tableRepository.findByTableCodeAndAppCodeAndTenantCode(tableCode, appCode, tenantCode);
        return table.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetadataTable> createTable(@RequestBody MetadataTable table) {
        log.debug("Creating new table: {}", table.getTableCode());
        if (tableRepository.existsByTableCode(table.getTableCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        MetadataTable savedTable = tableRepository.save(table);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataTable> updateTable(@PathVariable UUID id, @RequestBody MetadataTable table) {
        log.debug("Updating table with ID: {}", id);
        if (!tableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        table.setTableId(id);
        MetadataTable updatedTable = tableRepository.save(table);
        return ResponseEntity.ok(updatedTable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable UUID id) {
        log.debug("Deleting table with ID: {}", id);
        if (!tableRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tableRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{tableCode}")
    public ResponseEntity<Boolean> existsByTableCode(@PathVariable String tableCode) {
        log.debug("Checking if table exists by code: {}", tableCode);
        boolean exists = tableRepository.existsByTableCode(tableCode);
        return ResponseEntity.ok(exists);
    }
}
