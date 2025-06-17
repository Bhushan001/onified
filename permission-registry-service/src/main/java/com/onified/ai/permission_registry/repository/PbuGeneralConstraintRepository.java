package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.PbuGeneralConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PbuGeneralConstraintRepository extends JpaRepository<PbuGeneralConstraint, PbuGeneralConstraint.PbuGeneralConstraintId> {
    // Custom method to find all general constraints for a given PBU
    List<PbuGeneralConstraint> findByPbuId(String pbuId);
    // You might also need to find by constraintId if you want to know which PBUs use a specific constraint
    List<PbuGeneralConstraint> findByConstraintId(String constraintId);
}

