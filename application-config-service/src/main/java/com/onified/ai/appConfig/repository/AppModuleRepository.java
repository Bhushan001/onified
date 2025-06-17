package com.onified.ai.appConfig.repository;

import com.onified.ai.appConfig.entity.AppModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AppModuleRepository extends JpaRepository<AppModule, Integer> { // Renamed interface and generic type
    Optional<AppModule> findByAppCodeAndModuleCode(String appCode, String moduleCode); // Updated method signature
    List<AppModule> findByAppCode(String appCode); // Updated method signature
}
