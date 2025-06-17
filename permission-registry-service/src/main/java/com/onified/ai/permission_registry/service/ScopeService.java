package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Scope;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
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
     * @return The created Scope entity.
     * @throws ConflictException if a Scope with the same scopeCode already exists.
     */
    public Scope createScope(Scope scope) {
        if (scopeRepository.existsById(scope.getScopeCode())) {
            throw new ConflictException(String.format(ErrorMessages.SCOPE_ALREADY_EXISTS, scope.getScopeCode()));
        }
        return scopeRepository.save(scope);
    }

    /**
     * Retrieves a Scope by its scopeCode.
     * @param scopeCode The code of the Scope to retrieve.
     * @return The found Scope entity.
     * @throws ResourceNotFoundException if no Scope with the given scopeCode is found.
     */
    public Scope getScopeByCode(String scopeCode) {
        return scopeRepository.findById(scopeCode)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.SCOPE_NOT_FOUND, scopeCode)));
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
     * @return The updated Scope entity.
     * @throws ResourceNotFoundException if no Scope with the given scopeCode is found.
     */
    public Scope updateScope(String scopeCode, Scope updatedScope) {
        return scopeRepository.findById(scopeCode).map(existingScope -> {
            existingScope.setDisplayName(updatedScope.getDisplayName());
            existingScope.setDescription(updatedScope.getDescription());
            existingScope.setIsActive(updatedScope.getIsActive());
            return scopeRepository.save(existingScope);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.SCOPE_NOT_FOUND, scopeCode)));
    }

    /**
     * Deletes a Scope by its scopeCode.
     * @param scopeCode The code of the Scope to delete.
     * @throws ResourceNotFoundException if no Scope with the given scopeCode is found.
     */
    public void deleteScope(String scopeCode) {
        if (!scopeRepository.existsById(scopeCode)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.SCOPE_NOT_FOUND, scopeCode));
        }
        // TODO: Add logic to check if any PBUs reference this scope before deleting
        scopeRepository.deleteById(scopeCode);
    }
}
