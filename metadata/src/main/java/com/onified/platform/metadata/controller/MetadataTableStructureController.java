package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataTableStructure;
import com.onified.platform.metadata.repository.MetadataTableStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/table-structure")
@RequiredArgsConstructor
@Slf4j
public class MetadataTableStructureController {

    private final MetadataTableStructureRepository tableStructureRepository;

    @GetMapping
    public ResponseEntity<List<MetadataTableStructure>> getAllTableStructures() {
        log.debug("Fetching all table structures");
        List<MetadataTableStructure> structures = tableStructureRepository.findAll();
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataTableStructure> getTableStructureById(@PathVariable UUID id) {
        log.debug("Fetching table structure by ID: {}", id);
        Optional<MetadataTableStructure> structure = tableStructureRepository.findById(id);
        return structure.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/app/{appCode}/table/{tableCode}/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataTableStructure>> getTableStructuresByAppCodeAndTableCodeAndTenantCode(
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching table structures by app code: {}, table code: {}, tenant code: {}", appCode, tableCode, tenantCode);
        List<MetadataTableStructure> structures = tableStructureRepository
                .findByAppCodeAndTableCodeAndTenantCodeOrderByDisplayOrder(appCode, tableCode, tenantCode);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/app/{appCode}/table/{tableCode}/tenant/{tenantCode}/complete")
    public ResponseEntity<List<MetadataTableStructure>> getCompleteTableStructure(
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching complete table structure by app code: {}, table code: {}, tenant code: {}", appCode, tableCode, tenantCode);
        List<MetadataTableStructure> structures = tableStructureRepository
                .findCompleteTableStructure(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/app/{appCode}/table/{tableCode}/tenant/{tenantCode}/field/{fieldName}")
    public ResponseEntity<MetadataTableStructure> getTableStructureByTableCodeAndFieldName(
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String tenantCode,
            @PathVariable String fieldName) {
        log.debug("Fetching table structure by app: {}, table: {}, tenant: {}, field: {}", appCode, tableCode, tenantCode, fieldName);
        Optional<MetadataTableStructure> structure = tableStructureRepository
                .findByTableCodeAndFieldNameAndAppCodeAndTenantCode(tableCode, fieldName, appCode, tenantCode);
        return structure.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/app/{appCode}/table/{tableCode}/tenant/{tenantCode}/visible")
    public ResponseEntity<List<MetadataTableStructure>> getVisibleFieldsByTableCode(
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching visible fields by app: {}, table: {}, tenant: {}", appCode, tableCode, tenantCode);
        List<MetadataTableStructure> structures = tableStructureRepository
                .findVisibleFieldsByTableCodeAndAppCodeAndTenantCode(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(structures);
    }

    @GetMapping("/app/{appCode}/table/{tableCode}/tenant/{tenantCode}/editable")
    public ResponseEntity<List<MetadataTableStructure>> getEditableFieldsByTableCode(
            @PathVariable String appCode,
            @PathVariable String tableCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching editable fields by app: {}, table: {}, tenant: {}", appCode, tableCode, tenantCode);
        List<MetadataTableStructure> structures = tableStructureRepository
                .findEditableFieldsByTableCodeAndAppCodeAndTenantCode(tableCode, appCode, tenantCode);
        return ResponseEntity.ok(structures);
    }

    @PostMapping
    public ResponseEntity<MetadataTableStructure> createTableStructure(@RequestBody MetadataTableStructure structure) {
        log.debug("Creating new table structure for table: {}",
                structure.getTable() != null ? structure.getTable().getTableCode() : "unknown");
        MetadataTableStructure savedStructure = tableStructureRepository.save(structure);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStructure);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataTableStructure> updateTableStructure(@PathVariable UUID id, @RequestBody MetadataTableStructure structure) {
        log.debug("Updating table structure with ID: {}", id);
        if (!tableStructureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        structure.setStructureId(id);
        MetadataTableStructure updatedStructure = tableStructureRepository.save(structure);
        return ResponseEntity.ok(updatedStructure);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTableStructure(@PathVariable UUID id) {
        log.debug("Deleting table structure with ID: {}", id);
        if (!tableStructureRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tableStructureRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
