package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.PbuFieldConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PbuFieldConstraintRepository extends JpaRepository<PbuFieldConstraint, PbuFieldConstraint.PbuFieldConstraintId> {
    List<PbuFieldConstraint> findByPbuId(String pbuId);
    List<PbuFieldConstraint> findByConstraintId(String constraintId);
}
