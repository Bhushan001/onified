package com.onified.platform.metadata.repository;

import com.onified.platform.metadata.entity.MetadataStandardField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataStandardFieldRepository extends JpaRepository<MetadataStandardField, UUID> {

    @Query("SELECT sf FROM MetadataStandardField sf WHERE sf.fieldName = :fieldName AND sf.isActive = true")
    Optional<MetadataStandardField> findByFieldNameAndActive(@Param("fieldName") String fieldName);

    @Query("SELECT sf FROM MetadataStandardField sf WHERE sf.datatype = :datatype AND sf.isActive = true ORDER BY sf.fieldName")
    List<MetadataStandardField> findByDatatypeAndActive(@Param("datatype") String datatype);

    @Query("SELECT sf FROM MetadataStandardField sf WHERE sf.fieldType = :fieldType AND sf.isActive = true ORDER BY sf.fieldName")
    List<MetadataStandardField> findByFieldTypeAndActive(@Param("fieldType") String fieldType);

    @Query("SELECT sf FROM MetadataStandardField sf WHERE sf.isActive = true ORDER BY sf.fieldName")
    List<MetadataStandardField> findAllActiveFields();

    @Query("SELECT sf FROM MetadataStandardField sf WHERE sf.isRequired = true AND sf.isActive = true ORDER BY sf.fieldName")
    List<MetadataStandardField> findRequiredFields();

    boolean existsByFieldName(String fieldName);
}
