package com.onified.ai.platform_management.service;

import com.onified.ai.platform_management.entity.PasswordPolicy;
import com.onified.ai.platform_management.repository.PasswordPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasswordPolicyService {
    
    @Autowired
    private PasswordPolicyRepository passwordPolicyRepository;
    
    public List<PasswordPolicy> getAllPasswordPolicies() {
        return passwordPolicyRepository.findAllActivePoliciesOrdered();
    }
    
    public Optional<PasswordPolicy> getPasswordPolicyById(Long id) {
        return passwordPolicyRepository.findById(id);
    }
    
    public Optional<PasswordPolicy> getPasswordPolicyByName(String policyName) {
        return passwordPolicyRepository.findByPolicyName(policyName);
    }
    
    public Optional<PasswordPolicy> getDefaultPasswordPolicy() {
        return passwordPolicyRepository.findDefaultActivePolicy();
    }
    
    public PasswordPolicy createPasswordPolicy(PasswordPolicy passwordPolicy) {
        // Ensure only one default policy exists
        if (passwordPolicy.isDefault()) {
            clearDefaultPolicy();
        }
        
        // Validate policy name uniqueness
        if (passwordPolicyRepository.existsByPolicyName(passwordPolicy.getPolicyName())) {
            throw new IllegalArgumentException("Policy name already exists: " + passwordPolicy.getPolicyName());
        }
        
        return passwordPolicyRepository.save(passwordPolicy);
    }
    
    public PasswordPolicy updatePasswordPolicy(Long id, PasswordPolicy updatedPolicy) {
        Optional<PasswordPolicy> existingPolicy = passwordPolicyRepository.findById(id);
        if (existingPolicy.isEmpty()) {
            throw new IllegalArgumentException("Password policy not found with id: " + id);
        }
        
        PasswordPolicy policy = existingPolicy.get();
        
        // Check if policy name is being changed and if it conflicts with existing names
        if (!policy.getPolicyName().equals(updatedPolicy.getPolicyName()) &&
            passwordPolicyRepository.existsByPolicyNameAndIdNot(updatedPolicy.getPolicyName(), id)) {
            throw new IllegalArgumentException("Policy name already exists: " + updatedPolicy.getPolicyName());
        }
        
        // Ensure only one default policy exists
        if (updatedPolicy.isDefault() && !policy.isDefault()) {
            clearDefaultPolicy();
        }
        
        // Update fields
        policy.setPolicyName(updatedPolicy.getPolicyName());
        policy.setDescription(updatedPolicy.getDescription());
        policy.setMinLength(updatedPolicy.getMinLength());
        policy.setMaxPasswordAge(updatedPolicy.getMaxPasswordAge());
        policy.setMinPasswordAge(updatedPolicy.getMinPasswordAge());
        policy.setPasswordHistory(updatedPolicy.getPasswordHistory());
        policy.setRequireUppercase(updatedPolicy.isRequireUppercase());
        policy.setRequireLowercase(updatedPolicy.isRequireLowercase());
        policy.setRequireNumber(updatedPolicy.isRequireNumber());
        policy.setRequireSpecial(updatedPolicy.isRequireSpecial());
        policy.setInitialPasswordFormat(updatedPolicy.getInitialPasswordFormat());
        policy.setBannedPatterns(updatedPolicy.getBannedPatterns());
        policy.setActive(updatedPolicy.isActive());
        policy.setDefault(updatedPolicy.isDefault());
        
        return passwordPolicyRepository.save(policy);
    }
    
    public void deletePasswordPolicy(Long id) {
        Optional<PasswordPolicy> policy = passwordPolicyRepository.findById(id);
        if (policy.isEmpty()) {
            throw new IllegalArgumentException("Password policy not found with id: " + id);
        }
        
        if (policy.get().isDefault()) {
            throw new IllegalArgumentException("Cannot delete the default password policy");
        }
        
        passwordPolicyRepository.deleteById(id);
    }
    
    public PasswordPolicy setAsDefault(Long id) {
        Optional<PasswordPolicy> policy = passwordPolicyRepository.findById(id);
        if (policy.isEmpty()) {
            throw new IllegalArgumentException("Password policy not found with id: " + id);
        }
        
        // Clear existing default
        clearDefaultPolicy();
        
        // Set new default
        PasswordPolicy defaultPolicy = policy.get();
        defaultPolicy.setDefault(true);
        return passwordPolicyRepository.save(defaultPolicy);
    }
    
    private void clearDefaultPolicy() {
        List<PasswordPolicy> defaultPolicies = passwordPolicyRepository.findByIsActiveTrue();
        for (PasswordPolicy policy : defaultPolicies) {
            if (policy.isDefault()) {
                policy.setDefault(false);
                passwordPolicyRepository.save(policy);
            }
        }
    }
    
    public PasswordPolicy getOrCreateDefaultPolicy() {
        Optional<PasswordPolicy> defaultPolicy = getDefaultPasswordPolicy();
        if (defaultPolicy.isPresent()) {
            return defaultPolicy.get();
        }
        
        // Create a default policy if none exists
        PasswordPolicy newDefaultPolicy = new PasswordPolicy();
        newDefaultPolicy.setPolicyName("Default Password Policy");
        newDefaultPolicy.setDescription("Default password policy for the platform");
        newDefaultPolicy.setDefault(true);
        newDefaultPolicy.setActive(true);
        
        return passwordPolicyRepository.save(newDefaultPolicy);
    }
} 