package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataDatabaseRepository extends JpaRepository<MetadataDatabase, UUID> {

    Optional<MetadataDatabase> findByDatabaseName(String databaseName);

    @Query("SELECT d FROM MetadataDatabase d WHERE d.isActive = true")
    List<MetadataDatabase> findAllActiveDatabases();

    @Query("SELECT d FROM MetadataDatabase d WHERE d.databaseType = :databaseType AND d.isActive = true")
    List<MetadataDatabase> findByDatabaseTypeAndActive(@Param("databaseType") String databaseType);

    @Query("SELECT d FROM MetadataDatabase d WHERE d.host = :host AND d.port = :port AND d.isActive = true")
    List<MetadataDatabase> findByHostAndPortAndActive(@Param("host") String host, @Param("port") Integer port);

    boolean existsByDatabaseName(String databaseName);

    @Query("SELECT COUNT(d) > 0 FROM MetadataDatabase d WHERE d.host = :host AND d.port = :port AND d.databaseName = :databaseName")
    boolean existsByHostAndPortAndDatabaseName(@Param("host") String host, @Param("port") Integer port, @Param("databaseName") String databaseName);
}

