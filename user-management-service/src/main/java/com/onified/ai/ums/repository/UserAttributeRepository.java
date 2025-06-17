package com.onified.ai.ums.repository;

import com.onified.ai.ums.entity.UserAttribute;
import com.onified.ai.ums.entity.UserAttributeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, UserAttributeId> {
    List<UserAttribute> findByIdUserId(UUID userId);
    void deleteByIdUserId(UUID userId);
}
