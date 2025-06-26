package com.onified.ai.platform_management.repository;

import com.onified.ai.platform_management.entity.PasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, Long> {
    
    Optional<PasswordPolicy> findByPolicyName(String policyName);
    
    List<PasswordPolicy> findByIsActiveTrue();
    
    @Query("SELECT p FROM PasswordPolicy p WHERE p.isDefault = true AND p.isActive = true")
    Optional<PasswordPolicy> findDefaultActivePolicy();
    
    @Query("SELECT p FROM PasswordPolicy p WHERE p.isActive = true ORDER BY p.isDefault DESC, p.policyName ASC")
    List<PasswordPolicy> findAllActivePoliciesOrdered();
    
    boolean existsByPolicyName(String policyName);
    
    boolean existsByPolicyNameAndIdNot(String policyName, Long id);
} 