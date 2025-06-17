package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.PermissionBundleUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionBundleUnitRepository extends JpaRepository<PermissionBundleUnit, String> {
    // You might add custom queries later, e.g., find by actionCode, scopeCode
    List<PermissionBundleUnit> findByActionCode(String actionCode);
    List<PermissionBundleUnit> findByScopeCode(String scopeCode);
}