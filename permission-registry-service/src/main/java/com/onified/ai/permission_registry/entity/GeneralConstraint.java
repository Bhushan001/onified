package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "general_constraints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralConstraint extends Auditable {

    @Id
    private String constraintId;
    private String constraintName;
    private String tableName;

    @Column(name = "column_name")
    private String columnName;
    private String valueType;

    @JdbcTypeCode(SqlTypes.JSON) // Changed from Types.JSON
    @Column(columnDefinition = "jsonb")
    private String tableValue;

    @JdbcTypeCode(SqlTypes.JSON) // Changed from Types.JSON
    @Column(columnDefinition = "jsonb")
    private String customValue;

    @JdbcTypeCode(SqlTypes.JSON) // Changed from Types.JSON
    @Column(columnDefinition = "jsonb")
    private String ruleLogic;
    private Boolean isActive;

}
