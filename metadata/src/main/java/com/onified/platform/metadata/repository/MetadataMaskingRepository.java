package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataMasking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataMaskingRepository extends JpaRepository<MetadataMasking, UUID> {

    @Query("SELECT m FROM MetadataMasking m " +
           "WHERE m.tenant.tenantCode = :tenantCode " +
           "AND m.app.appCode = :appCode " +
           "AND m.standardField.fieldName = :fieldName " +
           "AND m.isActive = true")
    List<MetadataMasking> findByTenantCodeAndAppCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT m FROM MetadataMasking m " +
           "WHERE m.tenant.tenantCode = :tenantCode " +
           "AND m.app.appCode = :appCode " +
           "AND m.table.tableCode = :tableCode " +
           "AND m.standardField.fieldName = :fieldName " +
           "AND m.isActive = true")
    List<MetadataMasking> findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("tableCode") String tableCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT m FROM MetadataMasking m " +
           "WHERE m.table.tableCode = :tableCode " +
           "AND m.app.appCode = :appCode " +
           "AND m.tenant.tenantCode = :tenantCode " +
           "AND m.isActive = true")
    List<MetadataMasking> findByTableCodeAndAppCodeAndTenantCodeAndActive(
        @Param("tableCode") String tableCode,
        @Param("appCode") String appCode,
        @Param("tenantCode") String tenantCode);

    @Query("SELECT m FROM MetadataMasking m " +
           "WHERE m.maskingType = :maskingType " +
           "AND m.tenant.tenantCode = :tenantCode " +
           "AND m.isActive = true")
    List<MetadataMasking> findByMaskingTypeAndTenantCodeAndActive(
        @Param("maskingType") String maskingType,
        @Param("tenantCode") String tenantCode);
}
