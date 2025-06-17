package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.RoleFieldConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleFieldConstraintRepository extends JpaRepository<RoleFieldConstraint, RoleFieldConstraint.RoleFieldConstraintId> {
    List<RoleFieldConstraint> findByRoleId(String roleId);
    List<RoleFieldConstraint> findByConstraintId(String constraintId);
}
