package com.onified.ai.permission_registry.service;

import com.onified.ai.permission_registry.constants.ErrorMessages;
import com.onified.ai.permission_registry.entity.PbuContextualBehavior;
import com.onified.ai.permission_registry.entity.PbuFieldConstraint;
import com.onified.ai.permission_registry.entity.PbuGeneralConstraint;
import com.onified.ai.permission_registry.exception.ConflictException;
import com.onified.ai.permission_registry.exception.ResourceNotFoundException;
import com.onified.ai.permission_registry.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        validatePbuAndGeneralConstraintExistence(pbuId, constraintId);

        PbuGeneralConstraint.PbuGeneralConstraintId id = new PbuGeneralConstraint.PbuGeneralConstraintId(pbuId, constraintId);
        if (pbuGeneralConstraintRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.PBU_CONSTRAINT_ASSOCIATION_ALREADY_EXISTS, pbuId, constraintId));
        }
        PbuGeneralConstraint association = new PbuGeneralConstraint(pbuId, constraintId);
        return pbuGeneralConstraintRepository.save(association);
    }

    public List<PbuGeneralConstraint> getGeneralConstraintsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        return pbuGeneralConstraintRepository.findByPbuId(pbuId);
    }

    public void removePbuGeneralConstraintAssociation(String pbuId, String constraintId) {
        PbuGeneralConstraint.PbuGeneralConstraintId id = new PbuGeneralConstraint.PbuGeneralConstraintId(pbuId, constraintId);
        if (!pbuGeneralConstraintRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_CONSTRAINT_ASSOCIATION_NOT_FOUND, pbuId, constraintId));
        }
        pbuGeneralConstraintRepository.deleteById(id);
    }

    // --- PBU - Field Constraint Associations ---
    public PbuFieldConstraint associatePbuWithFieldConstraint(String pbuId, String constraintId) {
        validatePbuAndFieldConstraintExistence(pbuId, constraintId);

        PbuFieldConstraint.PbuFieldConstraintId id = new PbuFieldConstraint.PbuFieldConstraintId(pbuId, constraintId);
        if (pbuFieldConstraintRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.PBU_CONSTRAINT_ASSOCIATION_ALREADY_EXISTS, pbuId, constraintId));
        }
        PbuFieldConstraint association = new PbuFieldConstraint(pbuId, constraintId);
        return pbuFieldConstraintRepository.save(association);
    }

    public List<PbuFieldConstraint> getFieldConstraintsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        return pbuFieldConstraintRepository.findByPbuId(pbuId);
    }

    public void removePbuFieldConstraintAssociation(String pbuId, String constraintId) {
        PbuFieldConstraint.PbuFieldConstraintId id = new PbuFieldConstraint.PbuFieldConstraintId(pbuId, constraintId);
        if (!pbuFieldConstraintRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_CONSTRAINT_ASSOCIATION_NOT_FOUND, pbuId, constraintId));
        }
        pbuFieldConstraintRepository.deleteById(id);
    }

    // --- PBU - Contextual Behavior Associations ---
    public PbuContextualBehavior associatePbuWithContextualBehavior(String pbuId, String behaviorId) {
        validatePbuAndContextualBehaviorExistence(pbuId, behaviorId);

        PbuContextualBehavior.PbuContextualBehaviorId id = new PbuContextualBehavior.PbuContextualBehaviorId(pbuId, behaviorId);
        if (pbuContextualBehaviorRepository.existsById(id)) {
            throw new ConflictException(String.format(ErrorMessages.PBU_BEHAVIOR_ASSOCIATION_ALREADY_EXISTS, pbuId, behaviorId));
        }
        PbuContextualBehavior association = new PbuContextualBehavior(pbuId, behaviorId);
        return pbuContextualBehaviorRepository.save(association);
    }

    public List<PbuContextualBehavior> getContextualBehaviorsForPbu(String pbuId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        return pbuContextualBehaviorRepository.findByPbuId(pbuId);
    }

    public void removePbuContextualBehaviorAssociation(String pbuId, String behaviorId) {
        PbuContextualBehavior.PbuContextualBehaviorId id = new PbuContextualBehavior.PbuContextualBehaviorId(pbuId, behaviorId);
        if (!pbuContextualBehaviorRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_BEHAVIOR_ASSOCIATION_NOT_FOUND, pbuId, behaviorId));
        }
        pbuContextualBehaviorRepository.deleteById(id);
    }

    // --- Private Validation Helpers ---

    private void validatePbuAndGeneralConstraintExistence(String pbuId, String constraintId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        if (!generalConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.GENERAL_CONSTRAINT_NOT_FOUND, constraintId));
        }
    }

    private void validatePbuAndFieldConstraintExistence(String pbuId, String constraintId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        if (!fieldConstraintRepository.existsById(constraintId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.FIELD_CONSTRAINT_NOT_FOUND, constraintId));
        }
    }

    private void validatePbuAndContextualBehaviorExistence(String pbuId, String behaviorId) {
        if (!pbuRepository.existsById(pbuId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.PBU_NOT_FOUND, pbuId));
        }
        if (contextualBehaviorRepository.findByBehaviorId(behaviorId).isEmpty()) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.CONTEXTUAL_BEHAVIOR_NOT_FOUND, behaviorId));
        }
    }
}
