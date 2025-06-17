package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.ContextualBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContextualBehaviorRepository extends JpaRepository<ContextualBehavior, Integer> {
    // Find by behaviorId as it's a unique field in the entity
    Optional<ContextualBehavior> findByBehaviorId(String behaviorId);
}