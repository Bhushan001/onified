package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.RoleInheritance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleInheritanceRepository extends JpaRepository<RoleInheritance, RoleInheritance.RoleInheritanceId> {
    List<RoleInheritance> findByParentRoleId(String parentRoleId);
    List<RoleInheritance> findByChildRoleId(String childRoleId);
}


