package com.onified.ai.permission_registry.repository;

import com.onified.ai.permission_registry.entity.PbuContextualBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PbuContextualBehaviorRepository extends JpaRepository<PbuContextualBehavior, PbuContextualBehavior.PbuContextualBehaviorId> {
    List<PbuContextualBehavior> findByPbuId(String pbuId);
    List<PbuContextualBehavior> findByBehaviorId(String behaviorId);
}
