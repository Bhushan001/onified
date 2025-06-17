package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataSharedDatabase;
import com.onified.platform.metadata.repository.MetadataSharedDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/shared-databases")
@RequiredArgsConstructor
@Slf4j
public class MetadataSharedDatabaseController {

    private final MetadataSharedDatabaseRepository sharedDatabaseRepository;

    @GetMapping
    public ResponseEntity<List<MetadataSharedDatabase>> getAllSharedDatabases() {
        log.debug("Fetching all shared databases");
        List<MetadataSharedDatabase> sharedDatabases = sharedDatabaseRepository.findAll();
        return ResponseEntity.ok(sharedDatabases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataSharedDatabase> getSharedDatabaseById(@PathVariable UUID id) {
        log.debug("Fetching shared database by ID: {}", id);
        Optional<MetadataSharedDatabase> sharedDatabase = sharedDatabaseRepository.findById(id);
        return sharedDatabase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataSharedDatabase>> getSharedDatabasesByTenantCode(@PathVariable String tenantCode) {
        log.debug("Fetching shared databases by tenant code: {}", tenantCode);
        List<MetadataSharedDatabase> sharedDatabases = sharedDatabaseRepository.findByTenantCodeAndActive(tenantCode);
        return ResponseEntity.ok(sharedDatabases);
    }

    @GetMapping("/database/{databaseId}")
    public ResponseEntity<List<MetadataSharedDatabase>> getSharedDatabasesByDatabaseId(@PathVariable UUID databaseId) {
        log.debug("Fetching shared databases by database ID: {}", databaseId);
        List<MetadataSharedDatabase> sharedDatabases = sharedDatabaseRepository.findByDatabaseIdAndActive(databaseId);
        return ResponseEntity.ok(sharedDatabases);
    }

    @GetMapping("/tenant/{tenantCode}/database/{databaseName}")
    public ResponseEntity<MetadataSharedDatabase> getSharedDatabaseByTenantCodeAndDatabaseName(
            @PathVariable String tenantCode,
            @PathVariable String databaseName) {
        log.debug("Fetching shared database by tenant code: {} and database name: {}", tenantCode, databaseName);
        Optional<MetadataSharedDatabase> sharedDatabase = sharedDatabaseRepository.findByTenantCodeAndDatabaseNameAndActive(tenantCode, databaseName);
        return sharedDatabase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/routing/{routingStrategy}")
    public ResponseEntity<List<MetadataSharedDatabase>> getSharedDatabasesByRoutingStrategy(@PathVariable String routingStrategy) {
        log.debug("Fetching shared databases by routing strategy: {}", routingStrategy);
        List<MetadataSharedDatabase> sharedDatabases = sharedDatabaseRepository.findByRoutingStrategyAndActive(routingStrategy);
        return ResponseEntity.ok(sharedDatabases);
    }

    @GetMapping("/schema/{tenantSchema}")
    public ResponseEntity<MetadataSharedDatabase> getSharedDatabaseByTenantSchema(@PathVariable String tenantSchema) {
        log.debug("Fetching shared database by tenant schema: {}", tenantSchema);
        Optional<MetadataSharedDatabase> sharedDatabase = sharedDatabaseRepository.findByTenantSchemaAndActive(tenantSchema);
        return sharedDatabase.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetadataSharedDatabase> createSharedDatabase(@RequestBody MetadataSharedDatabase sharedDatabase) {
        log.debug("Creating new shared database for tenant: {} and database: {}",
                sharedDatabase.getTenant() != null ? sharedDatabase.getTenant().getTenantCode() : "unknown",
                sharedDatabase.getDatabase() != null ? sharedDatabase.getDatabase().getDatabaseName() : "unknown");
        MetadataSharedDatabase savedSharedDatabase = sharedDatabaseRepository.save(sharedDatabase);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSharedDatabase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataSharedDatabase> updateSharedDatabase(@PathVariable UUID id, @RequestBody MetadataSharedDatabase sharedDatabase) {
        log.debug("Updating shared database with ID: {}", id);

        if (!sharedDatabaseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        sharedDatabase.setSharedDbId(id);
        MetadataSharedDatabase updatedSharedDatabase = sharedDatabaseRepository.save(sharedDatabase);
        return ResponseEntity.ok(updatedSharedDatabase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSharedDatabase(@PathVariable UUID id) {
        log.debug("Deleting shared database with ID: {}", id);

        if (!sharedDatabaseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        sharedDatabaseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
