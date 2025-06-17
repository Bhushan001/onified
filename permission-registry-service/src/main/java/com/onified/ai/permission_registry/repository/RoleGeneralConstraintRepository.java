package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.RoleGeneralConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleGeneralConstraintRepository extends JpaRepository<RoleGeneralConstraint, RoleGeneralConstraint.RoleGeneralConstraintId> {
    List<RoleGeneralConstraint> findByRoleId(String roleId);
    List<RoleGeneralConstraint> findByConstraintId(String constraintId);
}