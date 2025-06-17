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
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends Auditable {

    @Id
    private String roleId;
    private String displayName;
    @Column(name = "app_code")
    private String appCode;
    @Column(name = "module_code")
    private String moduleCode;
    @Column(name = "role_function")
    private String roleFunction;
    private Boolean isActive;
    private Integer inheritanceDepth;
    private Boolean tenantCustomizable;
}
