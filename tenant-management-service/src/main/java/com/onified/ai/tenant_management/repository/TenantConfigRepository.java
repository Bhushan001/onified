package com.onified.ai.tenant_management.repository;

import com.onified.ai.tenant_management.entity.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, String> {
} 