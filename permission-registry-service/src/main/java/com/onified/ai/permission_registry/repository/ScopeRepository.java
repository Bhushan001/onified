package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, String> {
}