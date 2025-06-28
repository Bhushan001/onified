package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.PbuContextualBehavior;
import com.onified.ai.permission_registry.entity.PbuFieldConstraint;
import com.onified.ai.permission_registry.entity.PbuGeneralConstraint;
import com.onified.ai.permission_registry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PbuAssociationService {

    private final PermissionBundleUnitRepository pbuRepository;
    private final GeneralConstraintRepository generalConstraintRepository;
    private final FieldConstraintRepository fieldConstraintRepository;
    private final ContextualBehaviorRepository contextualBehaviorRepository;

    private final PbuGeneralConstraintRepository pbuGeneralConstraintRepository;
    private final PbuFieldConstraintRepository pbuFieldConstraintRepository;
    private final PbuContextualBehaviorRepository pbuContextualBehaviorRepository;

    @Autowired
    public PbuAssociationService(
            PermissionBundleUnitRepository pbuRepository,
            GeneralConstraintRepository generalConstraintRepository,
            FieldConstraintRepository fieldConstraintRepository,
            ContextualBehaviorRepository contextualBehaviorRepository,
            PbuGeneralConstraintRepository pbuGeneralConstraintRepository,
            PbuFieldConstraintRepository pbuFieldConstraintRepository,
            PbuContextualBehaviorRepository pbuContextualBehaviorRepository) {
        this.pbuRepository = pbuRepository;
        this.generalConstraintRepository = generalConstraintRepository;
        this.fieldConstraintRepository = fieldConstraintRepository;
        this.contextualBehaviorRepository = contextualBehaviorRepository;
        this.pbuGeneralConstraintRepository = pbuGeneralConstraintRepository;
        this.pbuFieldConstraintRepository = pbuFieldConstraintRepository;
        this.pbuContextualBehaviorRepository = pbuContextualBehaviorRepository;
    }

    // --- PBU - General Constraint Associations ---
    public PbuGeneralConstraint associatePbuWithGeneralConstraint(String pbuId, String constraintId) {
        if (!validatePbuAndGeneralConstraintExistence(pbuId, constraintId)) {
            return null; // Validation failed
        }

        PbuGeneralConstraint.PbuGeneralConstraintId id = new PbuGeneralConstraint.PbuGeneralConstraintId(pbuId, constraintId);
        if (pbuGeneralConstraintRepository.existsById(id)) {
            return null; // Association already exists
        }
        PbuGeneralConstraint association = new PbuGeneralConstraint(pbuId, constraintId);
        return pbuGeneralConstraintRepository.save(association);
    }

    public List<PbuGeneralConstraint> getGeneralConstraintsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            return Collections.emptyList();
        }
        return pbuGeneralConstraintRepository.findByPbuId(pbuId);
    }

    public boolean removePbuGeneralConstraintAssociation(String pbuId, String constraintId) {
        PbuGeneralConstraint.PbuGeneralConstraintId id = new PbuGeneralConstraint.PbuGeneralConstraintId(pbuId, constraintId);
        if (!pbuGeneralConstraintRepository.existsById(id)) {
            return false;
        }
        pbuGeneralConstraintRepository.deleteById(id);
        return true;
    }

    // --- PBU - Field Constraint Associations ---
    public PbuFieldConstraint associatePbuWithFieldConstraint(String pbuId, String constraintId) {
        if (!validatePbuAndFieldConstraintExistence(pbuId, constraintId)) {
            return null; // Validation failed
        }

        PbuFieldConstraint.PbuFieldConstraintId id = new PbuFieldConstraint.PbuFieldConstraintId(pbuId, constraintId);
        if (pbuFieldConstraintRepository.existsById(id)) {
            return null; // Association already exists
        }
        PbuFieldConstraint association = new PbuFieldConstraint(pbuId, constraintId);
        return pbuFieldConstraintRepository.save(association);
    }

    public List<PbuFieldConstraint> getFieldConstraintsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            return Collections.emptyList();
        }
        return pbuFieldConstraintRepository.findByPbuId(pbuId);
    }

    public boolean removePbuFieldConstraintAssociation(String pbuId, String constraintId) {
        PbuFieldConstraint.PbuFieldConstraintId id = new PbuFieldConstraint.PbuFieldConstraintId(pbuId, constraintId);
        if (!pbuFieldConstraintRepository.existsById(id)) {
            return false;
        }
        pbuFieldConstraintRepository.deleteById(id);
        return true;
    }

    // --- PBU - Contextual Behavior Associations ---
    public PbuContextualBehavior associatePbuWithContextualBehavior(String pbuId, String behaviorId) {
        if (!validatePbuAndContextualBehaviorExistence(pbuId, behaviorId)) {
            return null; // Validation failed
        }

        PbuContextualBehavior.PbuContextualBehaviorId id = new PbuContextualBehavior.PbuContextualBehaviorId(pbuId, behaviorId);
        if (pbuContextualBehaviorRepository.existsById(id)) {
            return null; // Association already exists
        }
        PbuContextualBehavior association = new PbuContextualBehavior(pbuId, behaviorId);
        return pbuContextualBehaviorRepository.save(association);
    }

    public List<PbuContextualBehavior> getContextualBehaviorsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            return Collections.emptyList();
        }
        return pbuContextualBehaviorRepository.findByPbuId(pbuId);
    }

    public boolean removePbuContextualBehaviorAssociation(String pbuId, String behaviorId) {
        PbuContextualBehavior.PbuContextualBehaviorId id = new PbuContextualBehavior.PbuContextualBehaviorId(pbuId, behaviorId);
        if (!pbuContextualBehaviorRepository.existsById(id)) {
            return false;
        }
        pbuContextualBehaviorRepository.deleteById(id);
        return true;
    }

    // --- Private Validation Helpers ---

    private boolean validatePbuAndGeneralConstraintExistence(String pbuId, String constraintId) {
        return pbuRepository.existsById(pbuId) && generalConstraintRepository.existsById(constraintId);
    }

    private boolean validatePbuAndFieldConstraintExistence(String pbuId, String constraintId) {
        return pbuRepository.existsById(pbuId) && fieldConstraintRepository.existsById(constraintId);
    }

    private boolean validatePbuAndContextualBehaviorExistence(String pbuId, String behaviorId) {
        return pbuRepository.existsById(pbuId) && contextualBehaviorRepository.findByBehaviorId(behaviorId).isPresent();
    }
}
