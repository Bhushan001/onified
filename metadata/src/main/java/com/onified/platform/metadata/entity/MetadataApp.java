package com.onified.platform.metadata.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metadata_apps")
@Getter
@Setter
public class MetadataApp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "app_id")
    private UUID appId;

    @Column(name = "app_code", unique = true, nullable = false, length = 50)
    private String appCode;

    @Column(name = "app_name", nullable = false, length = 200)
    private String appName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Parent Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_code", referencedColumnName = "tenant_code")
    @JsonIgnore
    private MetadataTenant tenant;

    // Child Relationships with @JsonIgnore
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataTable> tables;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataTableStructure> tableStructures;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataFieldOverride> fieldOverrides;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataConstraint> constraints;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataMasking> maskingRules;

    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataRegex> regexPatterns;

    @Override
    public String toString() {
        return "MetadataApp{" +
                "appId=" + appId +
                ", appCode='" + appCode + '\'' +
                ", appName='" + appName + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
