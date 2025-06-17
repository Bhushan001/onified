package com.onified.platform.metadata.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metadata_tables")
@Getter
@Setter
public class MetadataTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "table_id")
    private UUID tableId;

    @Column(name = "table_code", unique = true, nullable = false, length = 100)
    private String tableCode;

    @Column(name = "table_name", nullable = false, length = 200)
    private String tableName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Parent Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_code", referencedColumnName = "app_code")
    @JsonIgnore
    private MetadataApp app;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_code", referencedColumnName = "tenant_code")
    @JsonIgnore
    private MetadataTenant tenant;

    // Child Relationships with @JsonIgnore
    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataTableStructure> tableStructures;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataFieldOverride> fieldOverrides;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataConstraint> constraints;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataMasking> maskingRules;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataRegex> regexPatterns;

    @Override
    public String toString() {
        return "MetadataTable{" +
                "tableId=" + tableId +
                ", tableCode='" + tableCode + '\'' +
                ", tableName='" + tableName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
