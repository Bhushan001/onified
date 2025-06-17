package com.onified.ai.appConfig.repository;

import com.onified.ai.appConfig.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
}

