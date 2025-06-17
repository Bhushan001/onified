package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataApp;
import com.onified.platform.metadata.repository.MetadataAppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/apps")
@RequiredArgsConstructor
@Slf4j
public class MetadataAppController {

    private final MetadataAppRepository appRepository;

    @GetMapping
    public ResponseEntity<List<MetadataApp>> getAllApps() {
        log.debug("Fetching all apps");
        List<MetadataApp> apps = appRepository.findAll();
        return ResponseEntity.ok(apps);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MetadataApp>> getAllActiveApps() {
        log.debug("Fetching all active apps");
        List<MetadataApp> apps = appRepository.findAllActiveApps();
        return ResponseEntity.ok(apps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataApp> getAppById(@PathVariable UUID id) {
        log.debug("Fetching app by ID: {}", id);
        Optional<MetadataApp> app = appRepository.findById(id);
        return app.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{appCode}")
    public ResponseEntity<MetadataApp> getAppByCode(@PathVariable String appCode) {
        log.debug("Fetching app by code: {}", appCode);
        Optional<MetadataApp> app = appRepository.findByAppCodeAndActive(appCode);
        return app.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantCode}")
    public ResponseEntity<List<MetadataApp>> getAppsByTenantCode(@PathVariable String tenantCode) {
        log.debug("Fetching apps by tenant code: {}", tenantCode);
        List<MetadataApp> apps = appRepository.findByTenantCodeAndActive(tenantCode);
        return ResponseEntity.ok(apps);
    }

    @GetMapping("/code/{appCode}/tenant/{tenantCode}")
    public ResponseEntity<MetadataApp> getAppByCodeAndTenantCode(
            @PathVariable String appCode,
            @PathVariable String tenantCode) {
        log.debug("Fetching app by code: {} and tenant code: {}", appCode, tenantCode);
        Optional<MetadataApp> app = appRepository.findByAppCodeAndTenantCodeAndActive(appCode, tenantCode);
        return app.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetadataApp> createApp(@RequestBody MetadataApp app) {
        log.debug("Creating new app: {}", app.getAppCode());
        if (appRepository.existsByAppCode(app.getAppCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        MetadataApp savedApp = appRepository.save(app);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedApp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataApp> updateApp(@PathVariable UUID id, @RequestBody MetadataApp app) {
        log.debug("Updating app with ID: {}", id);
        if (!appRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        app.setAppId(id);
        MetadataApp updatedApp = appRepository.save(app);
        return ResponseEntity.ok(updatedApp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApp(@PathVariable UUID id) {
        log.debug("Deleting app with ID: {}", id);
        if (!appRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{appCode}")
    public ResponseEntity<Boolean> existsByAppCode(@PathVariable String appCode) {
        log.debug("Checking if app exists by code: {}", appCode);
        boolean exists = appRepository.existsByAppCode(appCode);
        return ResponseEntity.ok(exists);
    }
}
