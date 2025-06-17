package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataTableRepository extends JpaRepository<MetadataTable, UUID> {

    @Query("SELECT t FROM MetadataTable t WHERE t.tableCode = :tableCode AND t.isActive = true")
    Optional<MetadataTable> findByTableCodeAndActive(@Param("tableCode") String tableCode);

    @Query("SELECT t FROM MetadataTable t WHERE t.app.appCode = :appCode AND t.isActive = true")
    List<MetadataTable> findByAppCodeAndActive(@Param("appCode") String appCode);

    @Query("SELECT t FROM MetadataTable t WHERE t.tenant.tenantCode = :tenantCode AND t.isActive = true ORDER BY t.tableName")
    List<MetadataTable> findByTenantCodeAndActive(@Param("tenantCode") String tenantCode);

    @Query("SELECT t FROM MetadataTable t WHERE t.app.appCode = :appCode AND t.tenant.tenantCode = :tenantCode AND t.isActive = true ORDER BY t.tableName")
    List<MetadataTable> findByAppCodeAndTenantCodeAndActive(@Param("appCode") String appCode, @Param("tenantCode") String tenantCode);

    @Query("SELECT t FROM MetadataTable t WHERE t.tableCode = :tableCode AND t.app.appCode = :appCode AND t.tenant.tenantCode = :tenantCode")
    Optional<MetadataTable> findByTableCodeAndAppCodeAndTenantCode(@Param("tableCode") String tableCode, @Param("appCode") String appCode, @Param("tenantCode") String tenantCode);

    boolean existsByTableCode(String tableCode);
}
