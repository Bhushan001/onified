package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "metadata_table_structure")
@Getter
@Setter
public class MetadataTableStructure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "structure_id")
    private UUID structureId;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;

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
        return "MetadataTableStructure{" +
                "structureId=" + structureId +
                ", displayOrder=" + displayOrder +
                ", isVisible=" + isVisible +
                ", isEditable=" + isEditable +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", appCode='" + (app != null ? app.getAppCode() : null) + '\'' +
                ", tableCode='" + (table != null ? table.getTableCode() : null) + '\'' +
                ", fieldName='" + (standardField != null ? standardField.getFieldName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
