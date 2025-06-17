package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataSharedDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataSharedDatabaseRepository extends JpaRepository<MetadataSharedDatabase, UUID> {

    @Query("SELECT sd FROM MetadataSharedDatabase sd WHERE sd.tenant.tenantCode = :tenantCode AND sd.isActive = true")
    List<MetadataSharedDatabase> findByTenantCodeAndActive(@Param("tenantCode") String tenantCode);

    @Query("SELECT sd FROM MetadataSharedDatabase sd WHERE sd.database.databaseId = :databaseId AND sd.isActive = true")
    List<MetadataSharedDatabase> findByDatabaseIdAndActive(@Param("databaseId") UUID databaseId);

    @Query("SELECT sd FROM MetadataSharedDatabase sd WHERE sd.tenant.tenantCode = :tenantCode AND sd.database.databaseName = :databaseName AND sd.isActive = true")
    Optional<MetadataSharedDatabase> findByTenantCodeAndDatabaseNameAndActive(@Param("tenantCode") String tenantCode, @Param("databaseName") String databaseName);

    @Query("SELECT sd FROM MetadataSharedDatabase sd WHERE sd.routingStrategy = :routingStrategy AND sd.isActive = true")
    List<MetadataSharedDatabase> findByRoutingStrategyAndActive(@Param("routingStrategy") String routingStrategy);

    @Query("SELECT sd FROM MetadataSharedDatabase sd WHERE sd.tenantSchema = :tenantSchema AND sd.isActive = true")
    Optional<MetadataSharedDatabase> findByTenantSchemaAndActive(@Param("tenantSchema") String tenantSchema);
}
