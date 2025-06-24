package com.onified.ai.platform_management.repository;

import com.onified.ai.platform_management.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
} 