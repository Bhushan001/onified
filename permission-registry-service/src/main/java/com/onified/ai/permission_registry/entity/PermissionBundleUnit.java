package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permission_bundle_units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionBundleUnit extends Auditable {

    @Id
    private String pbuId;
    private String displayName;
    private String apiEndpoint;
    @Column(name = "action_code")
    private String actionCode;
    @Column(name = "scope_code")
    private String scopeCode;
    private Boolean isActive;
    private String version;
}

