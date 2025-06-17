package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataDatabase;
import com.onified.platform.metadata.repository.MetadataDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/databases")
@RequiredArgsConstructor
@Slf4j
public class MetadataDatabaseController {

    private final MetadataDatabaseRepository databaseRepository;

    @GetMapping
    public ResponseEntity<List<MetadataDatabase>> getAllDatabases() {
        log.debug("Fetching all databases");
        List<MetadataDatabase> databases = databaseRepository.findAll();
        return ResponseEntity.ok(databases);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MetadataDatabase>> getAllActiveDatabases() {
        log.debug("Fetching all active databases");
        List<MetadataDatabase> databases = databaseRepository.findAllActiveDatabases();
        return ResponseEntity.ok(databases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataDatabase> getDatabaseById(@PathVariable UUID id) {
        log.debug("Fetching database by ID: {}", id);
        Optional<MetadataDatabase> database = databaseRepository.findById(id);
        return database.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{databaseName}")
    public ResponseEntity<MetadataDatabase> getDatabaseByName(@PathVariable String databaseName) {
        log.debug("Fetching database by name: {}", databaseName);
        Optional<MetadataDatabase> database = databaseRepository.findByDatabaseName(databaseName);
        return database.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{databaseType}")
    public ResponseEntity<List<MetadataDatabase>> getDatabasesByType(@PathVariable String databaseType) {
        log.debug("Fetching databases by type: {}", databaseType);
        List<MetadataDatabase> databases = databaseRepository.findByDatabaseTypeAndActive(databaseType);
        return ResponseEntity.ok(databases);
    }

    @GetMapping("/host/{host}/port/{port}")
    public ResponseEntity<List<MetadataDatabase>> getDatabasesByHostAndPort(@PathVariable String host, @PathVariable Integer port) {
        log.debug("Fetching databases by host: {} and port: {}", host, port);
        List<MetadataDatabase> databases = databaseRepository.findByHostAndPortAndActive(host, port);
        return ResponseEntity.ok(databases);
    }

    @PostMapping
    public ResponseEntity<MetadataDatabase> createDatabase(@RequestBody MetadataDatabase database) {
        log.debug("Creating new database: {}", database.getDatabaseName());

        if (databaseRepository.existsByDatabaseName(database.getDatabaseName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        MetadataDatabase savedDatabase = databaseRepository.save(database);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDatabase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataDatabase> updateDatabase(@PathVariable UUID id, @RequestBody MetadataDatabase database) {
        log.debug("Updating database with ID: {}", id);

        if (!databaseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        database.setDatabaseId(id);
        MetadataDatabase updatedDatabase = databaseRepository.save(database);
        return ResponseEntity.ok(updatedDatabase);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDatabase(@PathVariable UUID id) {
        log.debug("Deleting database with ID: {}", id);

        if (!databaseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        databaseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/name/{databaseName}")
    public ResponseEntity<Boolean> existsByDatabaseName(@PathVariable String databaseName) {
        log.debug("Checking if database exists by name: {}", databaseName);
        boolean exists = databaseRepository.existsByDatabaseName(databaseName);
        return ResponseEntity.ok(exists);
    }
}
