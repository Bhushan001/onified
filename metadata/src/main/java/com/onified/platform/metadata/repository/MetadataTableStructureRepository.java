package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataTableStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataTableStructureRepository extends JpaRepository<MetadataTableStructure, UUID> {

    // Core metadata consumption queries
    @Query("SELECT ts FROM MetadataTableStructure ts " +
            "JOIN FETCH ts.standardField sf " +
            "LEFT JOIN FETCH sf.constraints c " +
            "LEFT JOIN FETCH sf.regexPatterns r " +
            "LEFT JOIN FETCH sf.maskingRules m " +
            "WHERE ts.table.tableCode = :tableCode " +
            "AND ts.table.app.appCode = :appCode " +
            "AND ts.table.tenant.tenantCode = :tenantCode " +
            "ORDER BY ts.displayOrder")
    List<MetadataTableStructure> findCompleteTableStructure(
            @Param("tableCode") String tableCode,
            @Param("appCode") String appCode,
            @Param("tenantCode") String tenantCode);

    @Query("SELECT ts FROM MetadataTableStructure ts " +
            "WHERE ts.table.tableCode = :tableCode " +
            "AND ts.app.appCode = :appCode " +
            "AND ts.tenant.tenantCode = :tenantCode " +
            "ORDER BY ts.displayOrder")
    List<MetadataTableStructure> findByAppCodeAndTableCodeAndTenantCodeOrderByDisplayOrder(
            @Param("appCode") String appCode,
            @Param("tableCode") String tableCode,
            @Param("tenantCode") String tenantCode);

    @Query("SELECT ts FROM MetadataTableStructure ts " +
            "WHERE ts.table.tableCode = :tableCode " +
            "AND ts.standardField.fieldName = :fieldName " +
            "AND ts.app.appCode = :appCode " +
            "AND ts.tenant.tenantCode = :tenantCode")
    Optional<MetadataTableStructure> findByTableCodeAndFieldNameAndAppCodeAndTenantCode(
            @Param("tableCode") String tableCode,
            @Param("fieldName") String fieldName,
            @Param("appCode") String appCode,
            @Param("tenantCode") String tenantCode);

    @Query("SELECT ts FROM MetadataTableStructure ts " +
            "WHERE ts.table.tableCode = :tableCode " +
            "AND ts.app.appCode = :appCode " +
            "AND ts.tenant.tenantCode = :tenantCode " +
            "AND ts.isVisible = true " +
            "ORDER BY ts.displayOrder")
    List<MetadataTableStructure> findVisibleFieldsByTableCodeAndAppCodeAndTenantCode(
            @Param("tableCode") String tableCode,
            @Param("appCode") String appCode,
            @Param("tenantCode") String tenantCode);

    @Query("SELECT ts FROM MetadataTableStructure ts " +
            "WHERE ts.table.tableCode = :tableCode " +
            "AND ts.app.appCode = :appCode " +
            "AND ts.tenant.tenantCode = :tenantCode " +
            "AND ts.isEditable = true " +
            "ORDER BY ts.displayOrder")
    List<MetadataTableStructure> findEditableFieldsByTableCodeAndAppCodeAndTenantCode(
            @Param("tableCode") String tableCode,
            @Param("appCode") String appCode,
            @Param("tenantCode") String tenantCode);
}
