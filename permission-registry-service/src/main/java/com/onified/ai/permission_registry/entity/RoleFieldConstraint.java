package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "role_field_constraints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RoleFieldConstraint.RoleFieldConstraintId.class)
public class RoleFieldConstraint {

    @Id
    @Column(name = "role_id")
    private String roleId;

    @Id
    @Column(name = "constraint_id")
    private String constraintId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleFieldConstraintId implements Serializable {
        private String roleId;
        private String constraintId;
    }
}
