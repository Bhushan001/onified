package com.onified.ai.permission_registry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.ContextualBehavior;
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
     * @return The created ContextualBehavior entity or null if behavior already exists or invalid JSON.
     */
    public ContextualBehavior createContextualBehavior(ContextualBehavior behavior) {
        if (contextualBehaviorRepository.findByBehaviorId(behavior.getBehaviorId()).isPresent()) {
            return null; // Behavior already exists
        }
        if (!validateJsonFields(behavior)) {
            return null; // Invalid JSON
        }
        return contextualBehaviorRepository.save(behavior);
    }

    /**
     * Retrieves a ContextualBehavior by its behaviorId.
     * @param behaviorId The ID of the ContextualBehavior to retrieve.
     * @return The found ContextualBehavior entity or null if not found.
     */
    public ContextualBehavior getContextualBehaviorById(String behaviorId) {
        return contextualBehaviorRepository.findByBehaviorId(behaviorId).orElse(null);
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
     * @return The updated ContextualBehavior entity or null if not found or invalid JSON.
     */
    public ContextualBehavior updateContextualBehavior(String behaviorId, ContextualBehavior updatedBehavior) {
        return contextualBehaviorRepository.findByBehaviorId(behaviorId).map(existingBehavior -> {
            if (!validateJsonFields(updatedBehavior)) {
                return null; // Invalid JSON
            }
            existingBehavior.setBehaviorCode(updatedBehavior.getBehaviorCode());
            existingBehavior.setDisplayName(updatedBehavior.getDisplayName());
            existingBehavior.setConditionLogic(updatedBehavior.getConditionLogic());
            existingBehavior.setIsActive(updatedBehavior.getIsActive());
            return contextualBehaviorRepository.save(existingBehavior);
        }).orElse(null);
    }

    /**
     * Deletes a ContextualBehavior by its behaviorId.
     * @param behaviorId The ID of the ContextualBehavior to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deleteContextualBehavior(String behaviorId) {
        ContextualBehavior behavior = contextualBehaviorRepository.findByBehaviorId(behaviorId).orElse(null);
        if (behavior == null) {
            return false;
        }
        // TODO: Add logic to check if any PBUs or Roles reference this behavior before deleting
        contextualBehaviorRepository.deleteById(behavior.getId()); // Delete by actual PK (id), not behaviorId
        return true;
    }

    private boolean validateJsonFields(ContextualBehavior behavior) {
        if (behavior.getConditionLogic() != null && !behavior.getConditionLogic().isEmpty()) {
            try {
                objectMapper.readTree(behavior.getConditionLogic());
            } catch (JsonProcessingException e) {
                return false; // Invalid JSON
            }
        }
        return true;
    }
}

