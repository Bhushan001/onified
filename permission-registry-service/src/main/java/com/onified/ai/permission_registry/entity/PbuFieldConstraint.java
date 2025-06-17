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
@Table(name = "pbu_field_constraints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PbuFieldConstraint.PbuFieldConstraintId.class)
public class PbuFieldConstraint {

    @Id
    @Column(name = "pbu_id")
    private String pbuId;

    @Id
    @Column(name = "constraint_id")
    private String constraintId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PbuFieldConstraintId implements Serializable {
        private String pbuId;
        private String constraintId;
    }
}
