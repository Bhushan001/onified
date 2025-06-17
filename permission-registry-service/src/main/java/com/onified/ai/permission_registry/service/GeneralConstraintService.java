package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.GeneralConstraint;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
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
     * @return The created GeneralConstraint entity.
     * @throws ConflictException if a GeneralConstraint with the same constraintId already exists.
     * @throws BadRequestException if ruleLogic is not valid JSON.
     */
    public GeneralConstraint createGeneralConstraint(GeneralConstraint constraint) {
        if (generalConstraintRepository.existsById(constraint.getConstraintId())) {
            throw new ConflictException(String.format(ErrorMessages.GENERAL_CONSTRAINT_ALREADY_EXISTS, constraint.getConstraintId()));
        }
        validateJsonFields(constraint);
        return generalConstraintRepository.save(constraint);
    }

    /**
     * Retrieves a GeneralConstraint by its constraintId.
     * @param constraintId The ID of the GeneralConstraint to retrieve.
     * @return The found GeneralConstraint entity.
     * @throws ResourceNotFoundException if no GeneralConstraint with the given ID is found.
     */
    public GeneralConstraint getGeneralConstraintById(String constraintId) {
        return generalConstraintRepository.findById(constraintId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.GENERAL_CONSTRAINT_NOT_FOUND, constraintId)));
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
     * @return The updated GeneralConstraint entity.
     * @throws ResourceNotFoundException if no GeneralConstraint with the given ID is found.
     * @throws BadRequestException if ruleLogic is not valid JSON.
     */
    public GeneralConstraint updateGeneralConstraint(String constraintId, GeneralConstraint updatedConstraint) {
        return generalConstraintRepository.findById(constraintId).map(existingConstraint -> {
            validateJsonFields(updatedConstraint); // Validate incoming JSON
            existingConstraint.setConstraintName(updatedConstraint.getConstraintName());
            existingConstraint.setTableName(updatedConstraint.getTableName());
            existingConstraint.setColumnName(updatedConstraint.getColumnName());
            existingConstraint.setValueType(updatedConstraint.getValueType());
            existingConstraint.setTableValue(updatedConstraint.getTableValue());
            existingConstraint.setCustomValue(updatedConstraint.getCustomValue());
            existingConstraint.setRuleLogic(updatedConstraint.getRuleLogic());
            existingConstraint.setIsActive(updatedConstraint.getIsActive());
            return generalConstraintRepository.save(existingConstraint);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.GENERAL_CONSTRAINT_NOT_FOUND, constraintId)));
    }

    /**
     * Deletes a GeneralConstraint by its constraintId.
     * @param constraintId The ID of the GeneralConstraint to delete.
     * @throws ResourceNotFoundException if no GeneralConstraint with the given ID is found.
     */
    public void deleteGeneralConstraint(String constraintId) {
        if (!generalConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.GENERAL_CONSTRAINT_NOT_FOUND, constraintId));
        }
        // TODO: Add logic to check if any PBUs or Roles reference this constraint before deleting
        generalConstraintRepository.deleteById(constraintId);
    }

    private void validateJsonFields(GeneralConstraint constraint) {
        if (constraint.getRuleLogic() != null && !constraint.getRuleLogic().isEmpty()) {
            try {
                objectMapper.readTree(constraint.getRuleLogic());
            } catch (JsonProcessingException e) {
                throw new BadRequestException(String.format(ErrorMessages.GENERAL_CONSTRAINT_INVALID_JSON_RULE, constraint.getConstraintId(), e.getMessage()));
            }
        }
        // You might want to add similar validation for tableValue and customValue if they are expected to be JSON.
        // For now, assuming ruleLogic is the primary JSON field requiring strict validation.
    }
}
