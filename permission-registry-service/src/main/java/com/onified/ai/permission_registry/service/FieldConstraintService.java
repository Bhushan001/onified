package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.FieldConstraint;
import com.onified.ai.permission_registry.repository.FieldConstraintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldConstraintService {

    private final FieldConstraintRepository fieldConstraintRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public FieldConstraintService(FieldConstraintRepository fieldConstraintRepository, ObjectMapper objectMapper) {
        this.fieldConstraintRepository = fieldConstraintRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new FieldConstraint.
     * @param constraint The FieldConstraint entity to create.
     * @return The created FieldConstraint entity or null if constraint already exists or invalid JSON.
     */
    public FieldConstraint createFieldConstraint(FieldConstraint constraint) {
        if (fieldConstraintRepository.existsById(constraint.getConstraintId())) {
            return null; // Constraint already exists
        }
        if (!validateJsonFields(constraint)) {
            return null; // Invalid JSON
        }
        return fieldConstraintRepository.save(constraint);
    }

    /**
     * Retrieves a FieldConstraint by its constraintId.
     * @param constraintId The ID of the FieldConstraint to retrieve.
     * @return The found FieldConstraint entity or null if not found.
     */
    public FieldConstraint getFieldConstraintById(String constraintId) {
        return fieldConstraintRepository.findById(constraintId).orElse(null);
    }

    /**
     * Retrieves all FieldConstraints.
     * @return A list of all FieldConstraint entities.
     */
    public List<FieldConstraint> getAllFieldConstraints() {
        return fieldConstraintRepository.findAll();
    }

    /**
     * Updates an existing FieldConstraint.
     * @param constraintId The ID of the FieldConstraint to update.
     * @param updatedConstraint The FieldConstraint entity with updated details.
     * @return The updated FieldConstraint entity or null if not found or invalid JSON.
     */
    public FieldConstraint updateFieldConstraint(String constraintId, FieldConstraint updatedConstraint) {
        return fieldConstraintRepository.findById(constraintId).map(existingConstraint -> {
            if (!validateJsonFields(updatedConstraint)) {
                return null; // Invalid JSON
            }
            existingConstraint.setEntityName(updatedConstraint.getEntityName());
            existingConstraint.setFieldName(updatedConstraint.getFieldName());
            existingConstraint.setAccessType(updatedConstraint.getAccessType());
            existingConstraint.setConditionLogic(updatedConstraint.getConditionLogic());
            existingConstraint.setIsActive(updatedConstraint.getIsActive());
            return fieldConstraintRepository.save(existingConstraint);
        }).orElse(null);
    }

    /**
     * Deletes a FieldConstraint by its constraintId.
     * @param constraintId The ID of the FieldConstraint to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deleteFieldConstraint(String constraintId) {
        if (!fieldConstraintRepository.existsById(constraintId)) {
            return false;
        }
        // TODO: Add logic to check if any PBUs or Roles reference this constraint before deleting
        fieldConstraintRepository.deleteById(constraintId);
        return true;
    }

    private boolean validateJsonFields(FieldConstraint constraint) {
        if (constraint.getConditionLogic() != null && !constraint.getConditionLogic().isEmpty()) {
            try {
                objectMapper.readTree(constraint.getConditionLogic());
            } catch (JsonProcessingException e) {
                return false; // Invalid JSON
            }
        }
        return true;
    }
}

