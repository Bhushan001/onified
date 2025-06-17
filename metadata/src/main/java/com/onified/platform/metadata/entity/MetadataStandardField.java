package com.onified.platform.metadata.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metadata_standard_fields")
@Getter
@Setter
public class MetadataStandardField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "field_id")
    private UUID fieldId;

    @Column(name = "field_name", unique = true, nullable = false, length = 100)
    private String fieldName;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(name = "datatype", nullable = false, length = 50)
    private String datatype;

    @Column(name = "field_type", nullable = false, length = 50)
    private String fieldType;

    @Column(name = "help_text", length = 500)
    private String helpText;

    @Column(name = "placeholder", length = 200)
    private String placeholder;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @Column(name = "is_unique", nullable = false)
    private Boolean isUnique = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships with @JsonIgnore to prevent cycles
    @OneToMany(mappedBy = "standardField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataFieldOverride> fieldOverrides;

    @OneToMany(mappedBy = "standardField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataConstraint> constraints;

    @OneToMany(mappedBy = "standardField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataMasking> maskingRules;

    @OneToMany(mappedBy = "standardField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataRegex> regexPatterns;

    @OneToMany(mappedBy = "standardField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataTableStructure> tableStructures;

    @Override
    public String toString() {
        return "MetadataStandardField{" +
                "fieldId=" + fieldId +
                ", fieldName='" + fieldName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", datatype='" + datatype + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", helpText='" + helpText + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", isRequired=" + isRequired +
                ", isUnique=" + isUnique +
                ", isActive=" + isActive +
                ", " + super.toString() +
                '}';
    }
}
