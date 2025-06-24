package com.onified.ai.platform_management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "tenants")
public class Tenant extends Auditable {
    @Id
    private String tenantId;
    private String name;
    private String status;
    @Column(columnDefinition = "text")
    private String extraConfig; // JSON for extensibility
} 