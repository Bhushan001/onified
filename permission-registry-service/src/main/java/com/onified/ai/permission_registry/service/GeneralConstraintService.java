package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.GeneralConstraint;
import com.onified.ai.permission_registry.repository.GeneralConstraintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralConstraintService {

    private final GeneralConstraintRepository generalConstraintRepository;
    private final ObjectMapper objectMapper; // For JSON validation

    @Autowired
    public GeneralConstraintService(GeneralConstraintRepository generalConstraintRepository, ObjectMapper objectMapper) {
        this.generalConstraintRepository = generalConstraintRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new GeneralConstraint.
     * @param constraint The GeneralConstraint entity to create.
     * @return The created GeneralConstraint entity or null if constraint already exists or invalid JSON.
     */
    public GeneralConstraint createGeneralConstraint(GeneralConstraint constraint) {
        if (generalConstraintRepository.existsById(constraint.getConstraintId())) {
            return null; // Constraint already exists
        }
        if (!validateJsonFields(constraint)) {
            return null; // Invalid JSON
        }
        return generalConstraintRepository.save(constraint);
    }

    /**
     * Retrieves a GeneralConstraint by its constraintId.
     * @param constraintId The ID of the GeneralConstraint to retrieve.
     * @return The found GeneralConstraint entity or null if not found.
     */
    public GeneralConstraint getGeneralConstraintById(String constraintId) {
        return generalConstraintRepository.findById(constraintId).orElse(null);
    }

    /**
     * Retrieves all GeneralConstraints.
     * @return A list of all GeneralConstraint entities.
     */
    public List<GeneralConstraint> getAllGeneralConstraints() {
        return generalConstraintRepository.findAll();
    }

    /**
     * Updates an existing GeneralConstraint.
     * @param constraintId The ID of the GeneralConstraint to update.
     * @param updatedConstraint The GeneralConstraint entity with updated details.
     * @return The updated GeneralConstraint entity or null if not found or invalid JSON.
     */
    public GeneralConstraint updateGeneralConstraint(String constraintId, GeneralConstraint updatedConstraint) {
        return generalConstraintRepository.findById(constraintId).map(existingConstraint -> {
            if (!validateJsonFields(updatedConstraint)) {
                return null; // Invalid JSON
            }
            existingConstraint.setConstraintName(updatedConstraint.getConstraintName());
            existingConstraint.setTableName(updatedConstraint.getTableName());
            existingConstraint.setColumnName(updatedConstraint.getColumnName());
            existingConstraint.setValueType(updatedConstraint.getValueType());
            existingConstraint.setTableValue(updatedConstraint.getTableValue());
            existingConstraint.setCustomValue(updatedConstraint.getCustomValue());
            existingConstraint.setRuleLogic(updatedConstraint.getRuleLogic());
            existingConstraint.setIsActive(updatedConstraint.getIsActive());
            return generalConstraintRepository.save(existingConstraint);
        }).orElse(null);
    }

    /**
     * Deletes a GeneralConstraint by its constraintId.
     * @param constraintId The ID of the GeneralConstraint to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deleteGeneralConstraint(String constraintId) {
        if (!generalConstraintRepository.existsById(constraintId)) {
            return false;
        }
        // TODO: Add logic to check if any PBUs or Roles reference this constraint before deleting
        generalConstraintRepository.deleteById(constraintId);
        return true;
    }

    private boolean validateJsonFields(GeneralConstraint constraint) {
        if (constraint.getRuleLogic() != null && !constraint.getRuleLogic().isEmpty()) {
            try {
                objectMapper.readTree(constraint.getRuleLogic());
            } catch (JsonProcessingException e) {
                return false; // Invalid JSON
            }
        }
        // You might want to add similar validation for tableValue and customValue if they are expected to be JSON.
        // For now, assuming ruleLogic is the primary JSON field requiring strict validation.
        return true;
    }
}
