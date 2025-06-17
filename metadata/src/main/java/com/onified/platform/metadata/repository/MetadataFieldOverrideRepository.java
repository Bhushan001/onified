package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataFieldOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataFieldOverrideRepository extends JpaRepository<MetadataFieldOverride, UUID> {

    @Query("SELECT fo FROM MetadataFieldOverride fo " +
           "WHERE fo.tenant.tenantCode = :tenantCode " +
           "AND fo.app.appCode = :appCode " +
           "AND fo.standardField.fieldName = :fieldName " +
           "AND fo.isActive = true")
    List<MetadataFieldOverride> findByTenantCodeAndAppCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT fo FROM MetadataFieldOverride fo " +
           "WHERE fo.tenant.tenantCode = :tenantCode " +
           "AND fo.app.appCode = :appCode " +
           "AND fo.table.tableCode = :tableCode " +
           "AND fo.standardField.fieldName = :fieldName " +
           "AND fo.isActive = true")
    List<MetadataFieldOverride> findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("tableCode") String tableCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT fo FROM MetadataFieldOverride fo " +
           "WHERE fo.tenant.tenantCode = :tenantCode " +
           "AND fo.app.appCode = :appCode " +
           "AND fo.overrideType = :overrideType " +
           "AND fo.isActive = true")
    List<MetadataFieldOverride> findByTenantCodeAndAppCodeAndOverrideTypeAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("overrideType") String overrideType);

    @Query("SELECT fo FROM MetadataFieldOverride fo " +
           "WHERE fo.table.tableCode = :tableCode " +
           "AND fo.app.appCode = :appCode " +
           "AND fo.tenant.tenantCode = :tenantCode " +
           "AND fo.isActive = true")
    List<MetadataFieldOverride> findByTableCodeAndAppCodeAndTenantCodeAndActive(
        @Param("tableCode") String tableCode,
        @Param("appCode") String appCode,
        @Param("tenantCode") String tenantCode);
}
