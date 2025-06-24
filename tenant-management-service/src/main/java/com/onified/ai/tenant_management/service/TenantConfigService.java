package com.onified.ai.tenant_management.service;

import com.onified.ai.tenant_management.entity.TenantConfig;
import com.onified.ai.tenant_management.repository.TenantConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantConfigService {
    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    public TenantConfig getConfig(String tenantId) {
        return tenantConfigRepository.findById(tenantId)
            .orElseGet(() -> createDefaultConfig(tenantId));
    }

    public TenantConfig updateConfig(String tenantId, TenantConfig updatedConfig) {
        updatedConfig.setTenantId(tenantId);
        return tenantConfigRepository.save(updatedConfig);
    }

    private TenantConfig createDefaultConfig(String tenantId) {
        TenantConfig config = new TenantConfig();
        config.setTenantId(tenantId);
        config.setBranding("{}");
        config.setPasswordPolicy(new com.onified.ai.tenant_management.entity.PasswordPolicy());
        config.setAppSubscriptions(java.util.Collections.emptyList());
        return tenantConfigRepository.save(config);
    }
} 