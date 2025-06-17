package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "field_constraints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldConstraint extends Auditable {

    @Id
    private String constraintId;
    private String entityName;
    private String fieldName;
    private String accessType;

    @JdbcTypeCode(SqlTypes.JSON) // Changed from Types.JSON
    @Column(columnDefinition = "jsonb")
    private String conditionLogic;
    private Boolean isActive;

}
