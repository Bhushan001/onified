package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.GeneralConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralConstraintRepository extends JpaRepository<GeneralConstraint, String> {
    // No additional methods needed for now as constraintId is the primary key.
}

