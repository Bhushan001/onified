package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.Action;
import com.onified.ai.permission_registry.entity.PermissionBundleUnit;
import com.onified.ai.permission_registry.entity.Scope;
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
     * @return The created PBU entity or null if validation fails.
     */
    public PermissionBundleUnit createPbu(PermissionBundleUnit pbu) {
        if (!validatePbuIdNamingConvention(pbu.getPbuId())) {
            return null; // Invalid naming convention
        }

        if (pbuRepository.existsById(pbu.getPbuId())) {
            return null; // PBU already exists
        }

        if (!validateActionAndScope(pbu.getActionCode(), pbu.getScopeCode())) {
            return null; // Invalid action or scope
        }

        return pbuRepository.save(pbu);
    }

    /**
     * Retrieves a PBU by its pbuId.
     * @param pbuId The ID of the PBU to retrieve.
     * @return The found PBU entity or null if not found.
     */
    public PermissionBundleUnit getPbuById(String pbuId) {
        return pbuRepository.findById(pbuId).orElse(null);
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
     * @return The updated PBU entity or null if not found or validation fails.
     */
    public PermissionBundleUnit updatePbu(String pbuId, PermissionBundleUnit updatedPbu) {
        return pbuRepository.findById(pbuId).map(existingPbu -> {
            // Naming convention cannot change for an existing PBU's ID
            if (!validateActionAndScope(updatedPbu.getActionCode(), updatedPbu.getScopeCode())) {
                return null; // Invalid action or scope
            }

            existingPbu.setDisplayName(updatedPbu.getDisplayName());
            existingPbu.setApiEndpoint(updatedPbu.getApiEndpoint());
            existingPbu.setActionCode(updatedPbu.getActionCode());
            existingPbu.setScopeCode(updatedPbu.getScopeCode());
            existingPbu.setIsActive(updatedPbu.getIsActive());
            existingPbu.setVersion(updatedPbu.getVersion());
            return pbuRepository.save(existingPbu);
        }).orElse(null);
    }

    /**
     * Deletes a PBU by its pbuId.
     * @param pbuId The ID of the PBU to delete.
     * @return true if deleted successfully, false if not found.
     */
    public boolean deletePbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            return false;
        }
        // TODO: Add logic to check and delete PBU-constraint/behavior associations
        // TODO: Add logic to check if any roles or user permission maps reference this PBU before deleting
        pbuRepository.deleteById(pbuId);
        return true;
    }

    private boolean validateActionAndScope(String actionCode, String scopeCode) {
        Action action = actionRepository.findById(actionCode).orElse(null);
        Scope scope = scopeRepository.findById(scopeCode).orElse(null);

        if (action == null || scope == null) {
            return false;
        }

        return action.getIsActive() && scope.getIsActive();
    }

    private boolean validatePbuIdNamingConvention(String pbuId) {
        return PBU_NAMING_PATTERN.matcher(pbuId).matches();
        // Further parsing logic can be added here to extract resource, action, scope and validate against existing ones if needed.
    }
}

