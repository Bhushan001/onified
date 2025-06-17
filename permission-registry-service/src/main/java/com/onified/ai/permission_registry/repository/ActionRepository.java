package com.onified.ai.permission_registry.repository;


import com.onified.ai.permission_registry.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, String> {
}
