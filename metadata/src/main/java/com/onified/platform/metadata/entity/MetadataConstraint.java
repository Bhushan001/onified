package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "metadata_constraints")
@Getter
@Setter
public class MetadataConstraint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "constraint_id")
    private UUID constraintId;

    @Column(name = "min_value", precision = 19, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 19, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "precision_value", length = 50)
    private String precisionValue;

    @Column(name = "format_type", length = 100)
    private String formatType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Perfect Object Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_code", referencedColumnName = "tenant_code")
    private MetadataTenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_code", referencedColumnName = "app_code")
    private MetadataApp app;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_code", referencedColumnName = "table_code")
    private MetadataTable table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private MetadataStandardField standardField;

    @Override
    public String toString() {
        return "MetadataConstraint{" +
                "constraintId=" + constraintId +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", precisionValue='" + precisionValue + '\'' +
                ", formatType='" + formatType + '\'' +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tableCode='" + (table != null ? table.getTableCode() : null) + '\'' +
                ", fieldName='" + (standardField != null ? standardField.getFieldName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}

