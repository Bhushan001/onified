package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.RoleContextualBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleContextualBehaviorRepository extends JpaRepository<RoleContextualBehavior, RoleContextualBehavior.RoleContextualBehaviorId> {
    List<RoleContextualBehavior> findByRoleId(String roleId);
    List<RoleContextualBehavior> findByBehaviorId(String behaviorId);
}
