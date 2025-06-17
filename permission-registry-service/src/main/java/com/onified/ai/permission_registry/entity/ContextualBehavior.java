package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "contextual_behaviors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextualBehavior extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "behavior_id", unique = true)
    private String behaviorId;

    private String behaviorCode;

    private String displayName;

    @JdbcTypeCode(SqlTypes.JSON) // Changed from Types.JSON
    @Column(columnDefinition = "jsonb")
    private String conditionLogic;

    private Boolean isActive;

    public ContextualBehavior(String behaviorId, String behaviorCode, String displayName, String conditionLogic, Boolean isActive) {
        this.behaviorId = behaviorId;
        this.behaviorCode = behaviorCode;
        this.displayName = displayName;
        this.conditionLogic = conditionLogic;
        this.isActive = isActive;
    }
}
