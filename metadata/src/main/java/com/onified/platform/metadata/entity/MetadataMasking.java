package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "metadata_masking")
@Getter
@Setter
public class MetadataMasking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "masking_id")
    private UUID maskingId;

    @Column(name = "masking_type", nullable = false, length = 50)
    private String maskingType;

    @Column(name = "mask_character", length = 5)
    private String maskCharacter = "*";

    @Column(name = "visible_characters")
    private Integer visibleCharacters;

    @Column(name = "mask_position", length = 20)
    private String maskPosition; // START, END, MIDDLE

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
        return "MetadataMasking{" +
                "maskingId=" + maskingId +
                ", maskingType='" + maskingType + '\'' +
                ", maskCharacter='" + maskCharacter + '\'' +
                ", visibleCharacters=" + visibleCharacters +
                ", maskPosition='" + maskPosition + '\'' +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tableCode='" + (table != null ? table.getTableCode() : null) + '\'' +
                ", fieldName='" + (standardField != null ? standardField.getFieldName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
