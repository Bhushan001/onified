package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.RoleContextualBehavior;
import com.onified.ai.permission_registry.entity.RoleFieldConstraint;
import com.onified.ai.permission_registry.entity.RoleGeneralConstraint;
import com.onified.ai.permission_registry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        if (!validateRoleAndGeneralConstraintExistence(roleId, constraintId)) {
            return null; // Validation failed
        }

        RoleGeneralConstraint.RoleGeneralConstraintId id = new RoleGeneralConstraint.RoleGeneralConstraintId(roleId, constraintId);
        if (roleGeneralConstraintRepository.existsById(id)) {
            return null; // Override already exists
        }
        RoleGeneralConstraint override = new RoleGeneralConstraint(roleId, constraintId);
        return roleGeneralConstraintRepository.save(override);
    }

    public List<RoleGeneralConstraint> getGeneralConstraintOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            return Collections.emptyList();
        }
        return roleGeneralConstraintRepository.findByRoleId(roleId);
    }

    public boolean removeRoleGeneralConstraintOverride(String roleId, String constraintId) {
        RoleGeneralConstraint.RoleGeneralConstraintId id = new RoleGeneralConstraint.RoleGeneralConstraintId(roleId, constraintId);
        if (!roleGeneralConstraintRepository.existsById(id)) {
            return false;
        }
        roleGeneralConstraintRepository.deleteById(id);
        return true;
    }

    // --- Role - Field Constraint Overrides ---
    public RoleFieldConstraint addRoleFieldConstraintOverride(String roleId, String constraintId) {
        if (!validateRoleAndFieldConstraintExistence(roleId, constraintId)) {
            return null; // Validation failed
        }

        RoleFieldConstraint.RoleFieldConstraintId id = new RoleFieldConstraint.RoleFieldConstraintId(roleId, constraintId);
        if (roleFieldConstraintRepository.existsById(id)) {
            return null; // Override already exists
        }
        RoleFieldConstraint override = new RoleFieldConstraint(roleId, constraintId);
        return roleFieldConstraintRepository.save(override);
    }

    public List<RoleFieldConstraint> getFieldConstraintOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            return Collections.emptyList();
        }
        return roleFieldConstraintRepository.findByRoleId(roleId);
    }

    public boolean removeRoleFieldConstraintOverride(String roleId, String constraintId) {
        RoleFieldConstraint.RoleFieldConstraintId id = new RoleFieldConstraint.RoleFieldConstraintId(roleId, constraintId);
        if (!roleFieldConstraintRepository.existsById(id)) {
            return false;
        }
        roleFieldConstraintRepository.deleteById(id);
        return true;
    }

    // --- Role - Contextual Behavior Overrides ---
    public RoleContextualBehavior addRoleContextualBehaviorOverride(String roleId, String behaviorId) {
        if (!validateRoleAndContextualBehaviorExistence(roleId, behaviorId)) {
            return null; // Validation failed
        }

        RoleContextualBehavior.RoleContextualBehaviorId id = new RoleContextualBehavior.RoleContextualBehaviorId(roleId, behaviorId);
        if (roleContextualBehaviorRepository.existsById(id)) {
            return null; // Override already exists
        }
        RoleContextualBehavior override = new RoleContextualBehavior(roleId, behaviorId);
        return roleContextualBehaviorRepository.save(override);
    }

    public List<RoleContextualBehavior> getContextualBehaviorOverridesForRole(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            return Collections.emptyList();
        }
        return roleContextualBehaviorRepository.findByRoleId(roleId);
    }

    public boolean removeRoleContextualBehaviorOverride(String roleId, String behaviorId) {
        RoleContextualBehavior.RoleContextualBehaviorId id = new RoleContextualBehavior.RoleContextualBehaviorId(roleId, behaviorId);
        if (!roleContextualBehaviorRepository.existsById(id)) {
            return false;
        }
        roleContextualBehaviorRepository.deleteById(id);
        return true;
    }

    // --- Private Validation Helpers ---

    private boolean validateRoleAndGeneralConstraintExistence(String roleId, String constraintId) {
        return roleRepository.existsById(roleId) && generalConstraintRepository.existsById(constraintId);
    }

    private boolean validateRoleAndFieldConstraintExistence(String roleId, String constraintId) {
        return roleRepository.existsById(roleId) && fieldConstraintRepository.existsById(constraintId);
    }

    private boolean validateRoleAndContextualBehaviorExistence(String roleId, String behaviorId) {
        return roleRepository.existsById(roleId) && contextualBehaviorRepository.findByBehaviorId(behaviorId).isPresent();
    }
}
