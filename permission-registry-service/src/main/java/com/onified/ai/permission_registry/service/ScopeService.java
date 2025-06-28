package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Scope;
import com.onified.ai.permission_registry.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;

    /**
     * Creates a new Scope.
     * @param scope The Scope entity to create.
     * @return The created Scope entity or null if scope already exists.
     */
    public Scope createScope(Scope scope) {
        if (scopeRepository.existsById(scope.getScopeCode())) {
            return null; // Scope already exists
        }
        return scopeRepository.save(scope);
    }

    /**
     * Retrieves a Scope by its scopeCode.
     * @param scopeCode The code of the Scope to retrieve.
     * @return The found Scope entity or null if not found.
     */
    public Scope getScopeByCode(String scopeCode) {
        return scopeRepository.findById(scopeCode).orElse(null);
    }

    /**
     * Retrieves all Scopes.
     * @return A list of all Scope entities.
     */
    public List<Scope> getAllScopes() {
        return scopeRepository.findAll();
    }

    /**
     * Updates an existing Scope.
     * @param scopeCode The code of the Scope to update.
     * @param updatedScope The Scope entity with updated details.
     * @return The updated Scope entity or null if not found.
     */
    public Scope updateScope(String scopeCode, Scope updatedScope) {
        return scopeRepository.findById(scopeCode).map(existingScope -> {
            existingScope.setDisplayName(updatedScope.getDisplayName());
            existingScope.setDescription(updatedScope.getDescription());
            existingScope.setIsActive(updatedScope.getIsActive());
            return scopeRepository.save(existingScope);
        }).orElse(null);
    }

    /**
     * Deletes a Scope by its scopeCode.
     * @param scopeCode The code of the Scope to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deleteScope(String scopeCode) {
        if (!scopeRepository.existsById(scopeCode)) {
            return false;
        }
        // TODO: Add logic to check if any PBUs reference this scope before deleting
        scopeRepository.deleteById(scopeCode);
        return true;
    }
}
