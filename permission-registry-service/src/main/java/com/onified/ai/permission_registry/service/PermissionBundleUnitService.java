package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Action;
import com.onified.ai.permission_registry.entity.PermissionBundleUnit;
import com.onified.ai.permission_registry.entity.Scope;
import com.onified.ai.permission_registry.exception.BadRequestException;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.repository.ActionRepository;
import com.onified.ai.permission_registry.repository.PermissionBundleUnitRepository;
import com.onified.ai.permission_registry.repository.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class PermissionBundleUnitService {

    private final PermissionBundleUnitRepository pbuRepository;
    private final ActionRepository actionRepository;
    private final ScopeRepository scopeRepository;

    // Regex for PBU naming convention: PBU_{RESOURCE}{ACTION}{SCOPE}
    private static final Pattern PBU_NAMING_PATTERN = Pattern.compile("^PBU_[A-Z0-9_]+[A-Z_]+[A-Z_]+$");

    @Autowired
    public PermissionBundleUnitService(PermissionBundleUnitRepository pbuRepository,
                                       ActionRepository actionRepository,
                                       ScopeRepository scopeRepository) {
        this.pbuRepository = pbuRepository;
        this.actionRepository = actionRepository;
        this.scopeRepository = scopeRepository;
    }

    /**
     * Creates a new Permission Bundle Unit (PBU).
     * Validates naming convention, existence of associated action and scope, and uniqueness.
     * @param pbu The PBU entity to create.
     * @return The created PBU entity.
     * @throws ConflictException if a PBU with the same pbuId already exists.
     * @throws BadRequestException if naming convention is violated, or action/scope are invalid.
     */
    public PermissionBundleUnit createPbu(PermissionBundleUnit pbu) {
        validatePbuIdNamingConvention(pbu.getPbuId());

        if (pbuRepository.existsById(pbu.getPbuId())) {
            throw new ConflictException(String.format(ErrorMessages.PBU_ALREADY_EXISTS, pbu.getPbuId()));
        }

        validateActionAndScope(pbu.getActionCode(), pbu.getScopeCode());

        return pbuRepository.save(pbu);
    }

    /**
     * Retrieves a PBU by its pbuId.
     * @param pbuId The ID of the PBU to retrieve.
     * @return The found PBU entity.
     * @throws ResourceNotFoundException if no PBU with the given ID is found.
     */
    public PermissionBundleUnit getPbuById(String pbuId) {
        return pbuRepository.findById(pbuId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId)));
    }

    /**
     * Retrieves all PBUs.
     * @return A list of all PBU entities.
     */
    public List<PermissionBundleUnit> getAllPbus() {
        return pbuRepository.findAll();
    }

    /**
     * Updates an existing PBU.
     * @param pbuId The ID of the PBU to update.
     * @param updatedPbu The PBU entity with updated details.
     * @return The updated PBU entity.
     * @throws ResourceNotFoundException if no PBU with the given ID is found.
     * @throws BadRequestException if naming convention is violated, or action/scope are invalid.
     */
    public PermissionBundleUnit updatePbu(String pbuId, PermissionBundleUnit updatedPbu) {
        return pbuRepository.findById(pbuId).map(existingPbu -> {
            // Naming convention cannot change for an existing PBU's ID
            validateActionAndScope(updatedPbu.getActionCode(), updatedPbu.getScopeCode());

            existingPbu.setDisplayName(updatedPbu.getDisplayName());
            existingPbu.setApiEndpoint(updatedPbu.getApiEndpoint());
            existingPbu.setActionCode(updatedPbu.getActionCode());
            existingPbu.setScopeCode(updatedPbu.getScopeCode());
            existingPbu.setIsActive(updatedPbu.getIsActive());
            existingPbu.setVersion(updatedPbu.getVersion());
            return pbuRepository.save(existingPbu);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId)));
    }

    /**
     * Deletes a PBU by its pbuId.
     * @param pbuId The ID of the PBU to delete.
     * @throws ResourceNotFoundException if no PBU with the given ID is found.
     */
    public void deletePbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        // TODO: Add logic to check and delete PBU-constraint/behavior associations
        // TODO: Add logic to check if any roles or user permission maps reference this PBU before deleting
        pbuRepository.deleteById(pbuId);
    }

    private void validateActionAndScope(String actionCode, String scopeCode) {
        Action action = actionRepository.findById(actionCode)
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.PBU_ACTION_SCOPE_INVALID, actionCode, scopeCode)));
        Scope scope = scopeRepository.findById(scopeCode)
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.PBU_ACTION_SCOPE_INVALID, actionCode, scopeCode)));

        if (!action.getIsActive() || !scope.getIsActive()) {
            throw new BadRequestException(String.format(ErrorMessages.PBU_ACTION_SCOPE_INVALID, actionCode, scopeCode));
        }
    }

    private void validatePbuIdNamingConvention(String pbuId) {
        if (!PBU_NAMING_PATTERN.matcher(pbuId).matches()) {
            throw new BadRequestException(String.format(ErrorMessages.PBU_NAMING_CONVENTION_VIOLATION, pbuId));
        }
        // Further parsing logic can be added here to extract resource, action, scope and validate against existing ones if needed.
    }
}

