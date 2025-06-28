package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Action;
import com.onified.ai.permission_registry.repository.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    /**
     * Creates a new Action.
     * @param action The Action entity to create.
     * @return The created Action entity or null if action already exists.
     */
    public Action createAction(Action action) {
        if (actionRepository.existsById(action.getActionCode())) {
            return null; // Action already exists
        }
        return actionRepository.save(action);
    }

    /**
     * Retrieves an Action by its actionCode.
     * @param actionCode The code of the Action to retrieve.
     * @return The found Action entity or null if not found.
     */
    public Action getActionByCode(String actionCode) {
        return actionRepository.findById(actionCode).orElse(null);
    }

    /**
     * Retrieves all Actions.
     * @return A list of all Action entities.
     */
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    /**
     * Updates an existing Action.
     * @param actionCode The code of the Action to update.
     * @param updatedAction The Action entity with updated details.
     * @return The updated Action entity or null if not found.
     */
    public Action updateAction(String actionCode, Action updatedAction) {
        return actionRepository.findById(actionCode).map(existingAction -> {
            existingAction.setDisplayName(updatedAction.getDisplayName());
            existingAction.setDescription(updatedAction.getDescription());
            existingAction.setIsActive(updatedAction.getIsActive());
            return actionRepository.save(existingAction);
        }).orElse(null);
    }

    /**
     * Deletes an Action by its actionCode.
     * @param actionCode The code of the Action to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deleteAction(String actionCode) {
        if (!actionRepository.existsById(actionCode)) {
            return false;
        }
        // TODO: Add logic to check if any PBUs reference this action before deleting
        actionRepository.deleteById(actionCode);
        return true;
    }
}
