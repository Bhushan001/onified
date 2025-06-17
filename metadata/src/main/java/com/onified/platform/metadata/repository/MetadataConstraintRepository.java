package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataConstraintRepository extends JpaRepository<MetadataConstraint, UUID> {

    @Query("SELECT c FROM MetadataConstraint c " +
           "WHERE c.tenant.tenantCode = :tenantCode " +
           "AND c.app.appCode = :appCode " +
           "AND c.standardField.fieldName = :fieldName " +
           "AND c.isActive = true")
    List<MetadataConstraint> findByTenantCodeAndAppCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT c FROM MetadataConstraint c " +
           "WHERE c.tenant.tenantCode = :tenantCode " +
           "AND c.app.appCode = :appCode " +
           "AND c.table.tableCode = :tableCode " +
           "AND c.standardField.fieldName = :fieldName " +
           "AND c.isActive = true")
    List<MetadataConstraint> findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(
        @Param("tenantCode") String tenantCode,
        @Param("appCode") String appCode,
        @Param("tableCode") String tableCode,
        @Param("fieldName") String fieldName);

    @Query("SELECT c FROM MetadataConstraint c " +
           "WHERE c.table.tableCode = :tableCode " +
           "AND c.app.appCode = :appCode " +
           "AND c.tenant.tenantCode = :tenantCode " +
           "AND c.isActive = true")
    List<MetadataConstraint> findByTableCodeAndAppCodeAndTenantCodeAndActive(
        @Param("tableCode") String tableCode,
        @Param("appCode") String appCode,
        @Param("tenantCode") String tenantCode);

    @Query("SELECT c FROM MetadataConstraint c " +
           "WHERE c.formatType = :formatType " +
           "AND c.tenant.tenantCode = :tenantCode " +
           "AND c.isActive = true")
    List<MetadataConstraint> findByFormatTypeAndTenantCodeAndActive(
        @Param("formatType") String formatType,
        @Param("tenantCode") String tenantCode);
}
