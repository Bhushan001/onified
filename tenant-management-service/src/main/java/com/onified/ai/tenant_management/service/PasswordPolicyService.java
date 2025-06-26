package com.onified.ai.tenant_management.service;

import com.onified.ai.tenant_management.client.PlatformPasswordPolicyClient;
import com.onified.ai.tenant_management.dto.CreatePasswordPolicyRequest;
import com.onified.ai.tenant_management.dto.PasswordPolicyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PasswordPolicyService {
    
    @Autowired
    private PlatformPasswordPolicyClient platformPasswordPolicyClient;
    
    public List<PasswordPolicyDto> getAllPasswordPolicies() {
        try {
            ResponseEntity<List<PasswordPolicyDto>> response = platformPasswordPolicyClient.getAllPasswordPolicies();
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            log.error("Failed to fetch password policies: {}", response.getStatusCode());
            return List.of();
        } catch (Exception e) {
            log.error("Error fetching password policies", e);
            return List.of();
        }
    }
    
    public PasswordPolicyDto getPasswordPolicyById(Long id) {
        try {
            ResponseEntity<PasswordPolicyDto> response = platformPasswordPolicyClient.getPasswordPolicyById(id);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            log.error("Failed to fetch password policy with id {}: {}", id, response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error fetching password policy with id: {}", id, e);
            return null;
        }
    }
    
    public PasswordPolicyDto getDefaultPasswordPolicy() {
        try {
            ResponseEntity<PasswordPolicyDto> response = platformPasswordPolicyClient.getDefaultPasswordPolicy();
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            log.error("Failed to fetch default password policy: {}", response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error fetching default password policy", e);
            return null;
        }
    }
    
    public PasswordPolicyDto createPasswordPolicy(CreatePasswordPolicyRequest request) {
        try {
            ResponseEntity<PasswordPolicyDto> response = platformPasswordPolicyClient.createPasswordPolicy(request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully created password policy: {}", request.getPolicyName());
                return response.getBody();
            }
            log.error("Failed to create password policy: {}", response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error creating password policy: {}", request.getPolicyName(), e);
            return null;
        }
    }
    
    public PasswordPolicyDto updatePasswordPolicy(Long id, CreatePasswordPolicyRequest request) {
        try {
            ResponseEntity<PasswordPolicyDto> response = platformPasswordPolicyClient.updatePasswordPolicy(id, request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully updated password policy with id: {}", id);
                return response.getBody();
            }
            log.error("Failed to update password policy with id {}: {}", id, response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error updating password policy with id: {}", id, e);
            return null;
        }
    }
    
    public boolean deletePasswordPolicy(Long id) {
        try {
            ResponseEntity<Void> response = platformPasswordPolicyClient.deletePasswordPolicy(id);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully deleted password policy with id: {}", id);
                return true;
            }
            log.error("Failed to delete password policy with id {}: {}", id, response.getStatusCode());
            return false;
        } catch (Exception e) {
            log.error("Error deleting password policy with id: {}", id, e);
            return false;
        }
    }
    
    public PasswordPolicyDto setAsDefault(Long id) {
        try {
            ResponseEntity<PasswordPolicyDto> response = platformPasswordPolicyClient.setAsDefault(id);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully set password policy with id {} as default", id);
                return response.getBody();
            }
            log.error("Failed to set password policy with id {} as default: {}", id, response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("Error setting password policy with id {} as default: {}", id, e);
            return null;
        }
    }
} 