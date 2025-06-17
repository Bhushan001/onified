package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.FieldConstraint;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
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
     * @return The created FieldConstraint entity.
     * @throws ConflictException if a FieldConstraint with the same constraintId already exists.
     * @throws BadRequestException if conditionLogic is not valid JSON.
     */
    public FieldConstraint createFieldConstraint(FieldConstraint constraint) {
        if (fieldConstraintRepository.existsById(constraint.getConstraintId())) {
            throw new ConflictException(String.format(ErrorMessages.FIELD_CONSTRAINT_ALREADY_EXISTS, constraint.getConstraintId()));
        }
        validateJsonFields(constraint);
        return fieldConstraintRepository.save(constraint);
    }

    /**
     * Retrieves a FieldConstraint by its constraintId.
     * @param constraintId The ID of the FieldConstraint to retrieve.
     * @return The found FieldConstraint entity.
     * @throws ResourceNotFoundException if no FieldConstraint with the given ID is found.
     */
    public FieldConstraint getFieldConstraintById(String constraintId) {
        return fieldConstraintRepository.findById(constraintId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.FIELD_CONSTRAINT_NOT_FOUND, constraintId)));
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
     * @return The updated FieldConstraint entity.
     * @throws ResourceNotFoundException if no FieldConstraint with the given ID is found.
     * @throws BadRequestException if conditionLogic is not valid JSON.
     */
    public FieldConstraint updateFieldConstraint(String constraintId, FieldConstraint updatedConstraint) {
        return fieldConstraintRepository.findById(constraintId).map(existingConstraint -> {
            validateJsonFields(updatedConstraint);
            existingConstraint.setEntityName(updatedConstraint.getEntityName());
            existingConstraint.setFieldName(updatedConstraint.getFieldName());
            existingConstraint.setAccessType(updatedConstraint.getAccessType());
            existingConstraint.setConditionLogic(updatedConstraint.getConditionLogic());
            existingConstraint.setIsActive(updatedConstraint.getIsActive());
            return fieldConstraintRepository.save(existingConstraint);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.FIELD_CONSTRAINT_NOT_FOUND, constraintId)));
    }

    /**
     * Deletes a FieldConstraint by its constraintId.
     * @param constraintId The ID of the FieldConstraint to delete.
     * @throws ResourceNotFoundException if no FieldConstraint with the given ID is found.
     */
    public void deleteFieldConstraint(String constraintId) {
        if (!fieldConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.FIELD_CONSTRAINT_NOT_FOUND, constraintId));
        }
        // TODO: Add logic to check if any PBUs or Roles reference this constraint before deleting
        fieldConstraintRepository.deleteById(constraintId);
    }

    private void validateJsonFields(FieldConstraint constraint) {
        if (constraint.getConditionLogic() != null && !constraint.getConditionLogic().isEmpty()) {
            try {
                objectMapper.readTree(constraint.getConditionLogic());
            } catch (JsonProcessingException e) {
                throw new BadRequestException(String.format(ErrorMessages.FIELD_CONSTRAINT_INVALID_JSON_CONDITION, constraint.getConstraintId(), e.getMessage()));
            }
        }
    }
}

