package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataTenantRepository extends JpaRepository<MetadataTenant, UUID> {

    @Query("SELECT t FROM MetadataTenant t WHERE t.tenantCode = :tenantCode AND t.isActive = true")
    Optional<MetadataTenant> findByTenantCodeAndActive(@Param("tenantCode") String tenantCode);

    @Query("SELECT t FROM MetadataTenant t WHERE t.isActive = true ORDER BY t.tenantName")
    List<MetadataTenant> findAllActiveTenants();

    @Query("SELECT t FROM MetadataTenant t WHERE t.tenantName LIKE %:tenantName% AND t.isActive = true")
    List<MetadataTenant> findByTenantNameContainingAndActive(@Param("tenantName") String tenantName);

    boolean existsByTenantCode(String tenantCode);
}
