package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataAppRepository extends JpaRepository<MetadataApp, UUID> {

    @Query("SELECT a FROM MetadataApp a WHERE a.appCode = :appCode AND a.isActive = true")
    Optional<MetadataApp> findByAppCodeAndActive(@Param("appCode") String appCode);

    @Query("SELECT a FROM MetadataApp a WHERE a.tenant.tenantCode = :tenantCode AND a.isActive = true ORDER BY a.appName")
    List<MetadataApp> findByTenantCodeAndActive(@Param("tenantCode") String tenantCode);

    @Query("SELECT a FROM MetadataApp a WHERE a.isActive = true ORDER BY a.appName")
    List<MetadataApp> findAllActiveApps();

    @Query("SELECT a FROM MetadataApp a WHERE a.appCode = :appCode AND a.tenant.tenantCode = :tenantCode AND a.isActive = true")
    Optional<MetadataApp> findByAppCodeAndTenantCodeAndActive(@Param("appCode") String appCode, @Param("tenantCode") String tenantCode);

    boolean existsByAppCode(String appCode);
}

