package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataRegex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataRegexRepository extends JpaRepository<MetadataRegex, UUID> {

 @Query("SELECT r FROM MetadataRegex r " +
        "WHERE r.tenant.tenantCode = :tenantCode " +
        "AND r.app.appCode = :appCode " +
        "AND r.standardField.fieldName = :fieldName " +
        "AND r.isActive = true")
 List<MetadataRegex> findByTenantCodeAndAppCodeAndFieldNameAndActive(
     @Param("tenantCode") String tenantCode,
     @Param("appCode") String appCode,
     @Param("fieldName") String fieldName);

 @Query("SELECT r FROM MetadataRegex r " +
        "WHERE r.tenant.tenantCode = :tenantCode " +
        "AND r.app.appCode = :appCode " +
        "AND r.table.tableCode = :tableCode " +
        "AND r.standardField.fieldName = :fieldName " +
        "AND r.isActive = true")
 List<MetadataRegex> findByTenantCodeAndAppCodeAndTableCodeAndFieldNameAndActive(
     @Param("tenantCode") String tenantCode,
     @Param("appCode") String appCode,
     @Param("tableCode") String tableCode,
     @Param("fieldName") String fieldName);

 @Query("SELECT r FROM MetadataRegex r " +
        "WHERE r.table.tableCode = :tableCode " +
        "AND r.app.appCode = :appCode " +
        "AND r.tenant.tenantCode = :tenantCode " +
        "AND r.isActive = true")
 List<MetadataRegex> findByTableCodeAndAppCodeAndTenantCodeAndActive(
     @Param("tableCode") String tableCode,
     @Param("appCode") String appCode,
     @Param("tenantCode") String tenantCode);

 @Query("SELECT r FROM MetadataRegex r " +
        "WHERE r.pattern = :pattern " +
        "AND r.tenant.tenantCode = :tenantCode " +
        "AND r.isActive = true")
 Optional<MetadataRegex> findByPatternAndTenantCodeAndActive(
     @Param("pattern") String pattern,
     @Param("tenantCode") String tenantCode);
}
