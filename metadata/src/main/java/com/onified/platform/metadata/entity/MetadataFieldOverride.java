package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "metadata_field_overrides")
@Getter
@Setter
public class MetadataFieldOverride extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "override_id")
    private UUID overrideId;

    @Column(name = "override_type", nullable = false, length = 50)
    private String overrideType; // DISPLAY, VALIDATION, CONSTRAINT, etc.

    @Column(name = "override_value", length = 1000)
    private String overrideValue;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "help_text", length = 500)
    private String helpText;

    @Column(name = "placeholder", length = 200)
    private String placeholder;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_unique")
    private Boolean isUnique;

    @Column(name = "min_value", precision = 19, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 19, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "max_length")
    private Integer maxLength;

    @Column(name = "pattern", length = 1000)
    private String pattern;

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
        return "MetadataFieldOverride{" +
                "overrideId=" + overrideId +
                ", overrideType='" + overrideType + '\'' +
                ", overrideValue='" + overrideValue + '\'' +
                ", displayName='" + displayName + '\'' +
                ", helpText='" + helpText + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", isRequired=" + isRequired +
                ", isUnique=" + isUnique +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", maxLength=" + maxLength +
                ", pattern='" + pattern + '\'' +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tableCode='" + (table != null ? table.getTableCode() : null) + '\'' +
                ", fieldName='" + (standardField != null ? standardField.getFieldName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
