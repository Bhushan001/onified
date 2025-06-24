package com.onified.ai.platform_management.service;

import com.onified.ai.platform_management.constants.MessageConstants;
import com.onified.ai.platform_management.constants.ErrorConstants;
import com.onified.ai.platform_management.dto.TenantDTO;
import com.onified.ai.platform_management.entity.Tenant;
import com.onified.ai.platform_management.exception.TenantNotFoundException;
import com.onified.ai.platform_management.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    public List<TenantDTO> getAllTenants() {
        return tenantRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TenantDTO getTenant(String tenantId) {
        return tenantRepository.findById(tenantId).map(this::toDTO)
                .orElseThrow(() -> new TenantNotFoundException(ErrorConstants.TENANT_NOT_FOUND));
    }

    public TenantDTO createTenant(TenantDTO dto, String createdBy) {
        Tenant tenant = toEntity(dto);
        tenant.setTenantId(UUID.randomUUID().toString());
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        tenant.setCreatedBy(createdBy);
        tenant.setUpdatedBy(createdBy);
        tenant.setStatus("ACTIVE");
        return toDTO(tenantRepository.save(tenant));
    }

    public TenantDTO updateTenant(String tenantId, TenantDTO dto, String updatedBy) {
        return tenantRepository.findById(tenantId).map(existing -> {
            existing.setName(dto.getName());
            existing.setStatus(dto.getStatus());
            existing.setExtraConfig(dto.getExtraConfig());
            existing.setUpdatedAt(LocalDateTime.now());
            existing.setUpdatedBy(updatedBy);
            return toDTO(tenantRepository.save(existing));
        }).orElseThrow(() -> new TenantNotFoundException(ErrorConstants.TENANT_NOT_FOUND));
    }

    public void deleteTenant(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new TenantNotFoundException(ErrorConstants.TENANT_NOT_FOUND);
        }
        tenantRepository.deleteById(tenantId);
    }

    private TenantDTO toDTO(Tenant tenant) {
        TenantDTO dto = new TenantDTO();
        dto.setTenantId(tenant.getTenantId());
        dto.setName(tenant.getName());
        dto.setStatus(tenant.getStatus());
        dto.setExtraConfig(tenant.getExtraConfig());
        dto.setCreatedAt(tenant.getCreatedAt());
        dto.setUpdatedAt(tenant.getUpdatedAt());
        dto.setCreatedBy(tenant.getCreatedBy());
        dto.setUpdatedBy(tenant.getUpdatedBy());
        return dto;
    }

    private Tenant toEntity(TenantDTO dto) {
        Tenant tenant = new Tenant();
        tenant.setTenantId(dto.getTenantId());
        tenant.setName(dto.getName());
        tenant.setStatus(dto.getStatus());
        tenant.setExtraConfig(dto.getExtraConfig());
        tenant.setCreatedAt(dto.getCreatedAt());
        tenant.setUpdatedAt(dto.getUpdatedAt());
        tenant.setCreatedBy(dto.getCreatedBy());
        tenant.setUpdatedBy(dto.getUpdatedBy());
        return tenant;
    }
} 