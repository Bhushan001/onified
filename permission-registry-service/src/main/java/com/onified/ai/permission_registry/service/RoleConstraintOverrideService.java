package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.RoleContextualBehavior;
import com.onified.ai.permission_registry.entity.RoleFieldConstraint;
import com.onified.ai.permission_registry.entity.RoleGeneralConstraint;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleConstraintOverrideService {

    private final RoleRepository roleRepository;
    private final GeneralConstraintRepository generalConstraintRepository;
    private final FieldConstraintRepository fieldConstraintRepository;
    private final ContextualBehaviorRepository contextualBehaviorRepository;

    private final RoleGeneralConstraintRepository roleGeneralConstraintRepository;
    private final RoleFieldConstraintRepository roleFieldConstraintRepository;
    private final RoleContextualBehaviorRepository roleContextualBehaviorRepository;

    @Autowired
    public RoleConstraintOverrideService(
            RoleRepository roleRepository,
            GeneralConstraintRepository generalConstraintRepository,
            FieldConstraintRepository fieldConstraintRepository,
            ContextualBehaviorRepository contextualBehaviorRepository,
            RoleGeneralConstraintRepository roleGeneralConstraintRepository,
            RoleFieldConstraintRepository roleFieldConstraintRepository,
            RoleContextualBehaviorRepository roleContextualBehaviorRepository) {
        this.roleRepository = roleRepository;
        this.generalConstraintRepository = generalConstraintRepository;
        this.fieldConstraintRepository = fieldConstraintRepository;
        this.contextualBehaviorRepository = contextualBehaviorRepository;
        this.roleGeneralConstraintRepository = roleGeneralConstraintRepository;
        this.roleFieldConstraintRepository = roleFieldConstraintRepository;
        this.roleContextualBehaviorRepository = roleContextualBehaviorRepository;
    }

    // --- Role - General Constraint Overrides ---
    public RoleGeneralConstraint addRoleGeneralConstraintOverride(String roleId, String constraintId) {
        validateRoleAndGeneralConstraintExistence(roleId, constraintId);

        RoleGeneralConstraint.RoleGeneralConstraintId id = new RoleGeneralConstraint.RoleGeneralConstraintId(roleId, constraintId);
        if (roleGeneralConstraintRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_CONSTRAINT_OVERRIDE_ALREADY_EXISTS, roleId, constraintId));
        }
        RoleGeneralConstraint override = new RoleGeneralConstraint(roleId, constraintId);
        return roleGeneralConstraintRepository.save(override);
    }

    public List<RoleGeneralConstraint> getGeneralConstraintOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        return roleGeneralConstraintRepository.findByRoleId(roleId);
    }

    public void removeRoleGeneralConstraintOverride(String roleId, String constraintId) {
        RoleGeneralConstraint.RoleGeneralConstraintId id = new RoleGeneralConstraint.RoleGeneralConstraintId(roleId, constraintId);
        if (!roleGeneralConstraintRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_CONSTRAINT_OVERRIDE_NOT_FOUND, roleId, constraintId));
        }
        roleGeneralConstraintRepository.deleteById(id);
    }

    // --- Role - Field Constraint Overrides ---
    public RoleFieldConstraint addRoleFieldConstraintOverride(String roleId, String constraintId) {
        validateRoleAndFieldConstraintExistence(roleId, constraintId);

        RoleFieldConstraint.RoleFieldConstraintId id = new RoleFieldConstraint.RoleFieldConstraintId(roleId, constraintId);
        if (roleFieldConstraintRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_CONSTRAINT_OVERRIDE_ALREADY_EXISTS, roleId, constraintId));
        }
        RoleFieldConstraint override = new RoleFieldConstraint(roleId, constraintId);
        return roleFieldConstraintRepository.save(override);
    }

    public List<RoleFieldConstraint> getFieldConstraintOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        return roleFieldConstraintRepository.findByRoleId(roleId);
    }

    public void removeRoleFieldConstraintOverride(String roleId, String constraintId) {
        RoleFieldConstraint.RoleFieldConstraintId id = new RoleFieldConstraint.RoleFieldConstraintId(roleId, constraintId);
        if (!roleFieldConstraintRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_CONSTRAINT_OVERRIDE_NOT_FOUND, roleId, constraintId));
        }
        roleFieldConstraintRepository.deleteById(id);
    }

    // --- Role - Contextual Behavior Overrides ---
    public RoleContextualBehavior addRoleContextualBehaviorOverride(String roleId, String behaviorId) {
        validateRoleAndContextualBehaviorExistence(roleId, behaviorId);

        RoleContextualBehavior.RoleContextualBehaviorId id = new RoleContextualBehavior.RoleContextualBehaviorId(roleId, behaviorId);
        if (roleContextualBehaviorRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.ROLE_BEHAVIOR_OVERRIDE_ALREADY_EXISTS, roleId, behaviorId));
        }
        RoleContextualBehavior override = new RoleContextualBehavior(roleId, behaviorId);
        return roleContextualBehaviorRepository.save(override);
    }

    public List<RoleContextualBehavior> getContextualBehaviorOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        return roleContextualBehaviorRepository.findByRoleId(roleId);
    }

    public void removeRoleContextualBehaviorOverride(String roleId, String behaviorId) {
        RoleContextualBehavior.RoleContextualBehaviorId id = new RoleContextualBehavior.RoleContextualBehaviorId(roleId, behaviorId);
        if (!roleContextualBehaviorRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_BEHAVIOR_OVERRIDE_NOT_FOUND, roleId, behaviorId));
        }
        roleContextualBehaviorRepository.deleteById(id);
    }

    // --- Private Validation Helpers ---

    private void validateRoleAndGeneralConstraintExistence(String roleId, String constraintId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        if (!generalConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.GENERAL_CONSTRAINT_NOT_FOUND, constraintId));
        }
    }

    private void validateRoleAndFieldConstraintExistence(String roleId, String constraintId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        if (!fieldConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.FIELD_CONSTRAINT_NOT_FOUND, constraintId));
        }
    }

    private void validateRoleAndContextualBehaviorExistence(String roleId, String behaviorId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ROLE_NOT_FOUND, roleId));
        }
        if (contextualBehaviorRepository.findByBehaviorId(behaviorId).isEmpty()) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_NOT_FOUND, behaviorId));
        }
    }
}
