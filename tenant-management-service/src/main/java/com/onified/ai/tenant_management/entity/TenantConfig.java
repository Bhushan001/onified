package com.onified.ai.tenant_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tenant_config")
public class TenantConfig {
    @Id
    private String tenantId;

    @Column(columnDefinition = "text")
    private String branding; // JSON or simple string for now

    @ElementCollection
    @CollectionTable(name = "tenant_app_subscriptions", joinColumns = @JoinColumn(name = "tenant_id"))
    @Column(name = "app_id")
    private List<String> appSubscriptions;
} 