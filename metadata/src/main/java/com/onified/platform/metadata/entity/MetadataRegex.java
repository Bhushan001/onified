package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "metadata_regex")
@Getter
@Setter
public class MetadataRegex extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "regex_id")
    private UUID regexId;

    @Column(name = "pattern", nullable = false, length = 1000)
    private String pattern;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "max_length")
    private Integer maxLength;

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
        return "MetadataRegex{" +
                "regexId=" + regexId +
                ", pattern='" + pattern + '\'' +
                ", description='" + description + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", maxLength=" + maxLength +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tableCode='" + (table != null ? table.getTableCode() : null) + '\'' +
                ", fieldName='" + (standardField != null ? standardField.getFieldName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
