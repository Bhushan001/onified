package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // Custom queries could be added here if needed, e.g., find by appCode, moduleCode
    List<Role> findByAppCodeAndModuleCode(String appCode, String moduleCode);
    List<Role> findByAppCode(String appCode);
    List<Role> findByRoleFunction(String roleFunction);
}
