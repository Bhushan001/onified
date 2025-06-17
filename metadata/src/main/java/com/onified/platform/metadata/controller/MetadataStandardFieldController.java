package com.onified.platform.metadata.controller;

import com.onified.platform.metadata.entity.MetadataStandardField;
import com.onified.platform.metadata.repository.MetadataStandardFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metadata/fields")
@RequiredArgsConstructor
@Slf4j
public class MetadataStandardFieldController {

    private final MetadataStandardFieldRepository fieldRepository;

    @GetMapping
    public ResponseEntity<List<MetadataStandardField>> getAllFields() {
        log.debug("Fetching all standard fields");
        List<MetadataStandardField> fields = fieldRepository.findAll();
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MetadataStandardField>> getAllActiveFields() {
        log.debug("Fetching all active standard fields");
        List<MetadataStandardField> fields = fieldRepository.findAllActiveFields();
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetadataStandardField> getFieldById(@PathVariable UUID id) {
        log.debug("Fetching field by ID: {}", id);
        Optional<MetadataStandardField> field = fieldRepository.findById(id);
        return field.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{fieldName}")
    public ResponseEntity<MetadataStandardField> getFieldByName(@PathVariable String fieldName) {
        log.debug("Fetching field by name: {}", fieldName);
        Optional<MetadataStandardField> field = fieldRepository.findByFieldNameAndActive(fieldName);
        return field.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/datatype/{datatype}")
    public ResponseEntity<List<MetadataStandardField>> getFieldsByDatatype(@PathVariable String datatype) {
        log.debug("Fetching fields by datatype: {}", datatype);
        List<MetadataStandardField> fields = fieldRepository.findByDatatypeAndActive(datatype);
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/fieldtype/{fieldType}")
    public ResponseEntity<List<MetadataStandardField>> getFieldsByFieldType(@PathVariable String fieldType) {
        log.debug("Fetching fields by field type: {}", fieldType);
        List<MetadataStandardField> fields = fieldRepository.findByFieldTypeAndActive(fieldType);
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/required")
    public ResponseEntity<List<MetadataStandardField>> getRequiredFields() {
        log.debug("Fetching required fields");
        List<MetadataStandardField> fields = fieldRepository.findRequiredFields();
        return ResponseEntity.ok(fields);
    }

    @PostMapping
    public ResponseEntity<MetadataStandardField> createField(@RequestBody MetadataStandardField field) {
        log.debug("Creating new field: {}", field.getFieldName());
        if (fieldRepository.existsByFieldName(field.getFieldName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        MetadataStandardField savedField = fieldRepository.save(field);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedField);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetadataStandardField> updateField(@PathVariable UUID id, @RequestBody MetadataStandardField field) {
        log.debug("Updating field with ID: {}", id);
        if (!fieldRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        field.setFieldId(id);
        MetadataStandardField updatedField = fieldRepository.save(field);
        return ResponseEntity.ok(updatedField);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable UUID id) {
        log.debug("Deleting field with ID: {}", id);
        if (!fieldRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fieldRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/name/{fieldName}")
    public ResponseEntity<Boolean> existsByFieldName(@PathVariable String fieldName) {
        log.debug("Checking if field exists by name: {}", fieldName);
        boolean exists = fieldRepository.existsByFieldName(fieldName);
        return ResponseEntity.ok(exists);
    }
}
