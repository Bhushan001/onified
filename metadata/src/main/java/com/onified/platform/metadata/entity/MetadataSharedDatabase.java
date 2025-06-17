package com.onified.platform.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "metadata_shared_database")
@Getter
@Setter
public class MetadataSharedDatabase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shared_db_id")
    private UUID sharedDbId;

    @Column(name = "tenant_schema", nullable = false, length = 100)
    private String tenantSchema;

    @Column(name = "routing_strategy", nullable = false, length = 50)
    private String routingStrategy; // SCHEMA_BASED, DATABASE_BASED

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_code", referencedColumnName = "tenant_code")
    private MetadataTenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id")
    private MetadataDatabase database;

    @Override
    public String toString() {
        return "MetadataSharedDatabase{" +
                "sharedDbId=" + sharedDbId +
                ", tenantSchema='" + tenantSchema + '\'' +
                ", routingStrategy='" + routingStrategy + '\'' +
                ", isActive=" + isActive +
                ", tenantCode='" + (tenant != null ? tenant.getTenantCode() : null) + '\'' +
                ", databaseName='" + (database != null ? database.getDatabaseName() : null) + '\'' +
                ", " + super.toString() +
                '}';
    }
}
