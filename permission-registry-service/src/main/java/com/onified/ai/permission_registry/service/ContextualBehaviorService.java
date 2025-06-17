package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.ContextualBehavior;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.repository.ContextualBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextualBehaviorService {

    private final ContextualBehaviorRepository contextualBehaviorRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ContextualBehaviorService(ContextualBehaviorRepository contextualBehaviorRepository, ObjectMapper objectMapper) {
        this.contextualBehaviorRepository = contextualBehaviorRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new ContextualBehavior.
     * @param behavior The ContextualBehavior entity to create.
     * @return The created ContextualBehavior entity.
     * @throws ConflictException if a ContextualBehavior with the same behaviorId already exists.
     * @throws BadRequestException if conditionLogic is not valid JSON.
     */
    public ContextualBehavior createContextualBehavior(ContextualBehavior behavior) {
        if (contextualBehaviorRepository.findByBehaviorId(behavior.getBehaviorId()).isPresent()) {
            throw new ConflictException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_ALREADY_EXISTS, behavior.getBehaviorId()));
        }
        validateJsonFields(behavior);
        return contextualBehaviorRepository.save(behavior);
    }

    /**
     * Retrieves a ContextualBehavior by its behaviorId.
     * @param behaviorId The ID of the ContextualBehavior to retrieve.
     * @return The found ContextualBehavior entity.
     * @throws ResourceNotFoundException if no ContextualBehavior with the given ID is found.
     */
    public ContextualBehavior getContextualBehaviorById(String behaviorId) {
        return contextualBehaviorRepository.findByBehaviorId(behaviorId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_NOT_FOUND, behaviorId)));
    }

    /**
     * Retrieves all ContextualBehaviors.
     * @return A list of all ContextualBehavior entities.
     */
    public List<ContextualBehavior> getAllContextualBehaviors() {
        return contextualBehaviorRepository.findAll();
    }

    /**
     * Updates an existing ContextualBehavior.
     * @param behaviorId The ID of the ContextualBehavior to update.
     * @param updatedBehavior The ContextualBehavior entity with updated details.
     * @return The updated ContextualBehavior entity.
     * @throws ResourceNotFoundException if no ContextualBehavior with the given ID is found.
     * @throws BadRequestException if conditionLogic is not valid JSON.
     */
    public ContextualBehavior updateContextualBehavior(String behaviorId, ContextualBehavior updatedBehavior) {
        return contextualBehaviorRepository.findByBehaviorId(behaviorId).map(existingBehavior -> {
            validateJsonFields(updatedBehavior);
            existingBehavior.setBehaviorCode(updatedBehavior.getBehaviorCode());
            existingBehavior.setDisplayName(updatedBehavior.getDisplayName());
            existingBehavior.setConditionLogic(updatedBehavior.getConditionLogic());
            existingBehavior.setIsActive(updatedBehavior.getIsActive());
            return contextualBehaviorRepository.save(existingBehavior);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_NOT_FOUND, behaviorId)));
    }

    /**
     * Deletes a ContextualBehavior by its behaviorId.
     * @param behaviorId The ID of the ContextualBehavior to delete.
     * @throws ResourceNotFoundException if no ContextualBehavior with the given ID is found.
     */
    public void deleteContextualBehavior(String behaviorId) {
        ContextualBehavior behavior = contextualBehaviorRepository.findByBehaviorId(behaviorId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_NOT_FOUND, behaviorId)));
        // TODO: Add logic to check if any PBUs or Roles reference this behavior before deleting
        contextualBehaviorRepository.deleteById(behavior.getId()); // Delete by actual PK (id), not behaviorId
    }

    private void validateJsonFields(ContextualBehavior behavior) {
        if (behavior.getConditionLogic() != null && !behavior.getConditionLogic().isEmpty()) {
            try {
                objectMapper.readTree(behavior.getConditionLogic());
            } catch (JsonProcessingException e) {
                throw new BadRequestException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_INVALID_JSON_CONDITION, behavior.getBehaviorId(), e.getMessage()));
            }
        }
    }
}

