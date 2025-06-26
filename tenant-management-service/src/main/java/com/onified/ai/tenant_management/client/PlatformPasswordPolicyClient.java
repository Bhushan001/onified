package com.onified.ai.tenant_management.client;

import com.onified.ai.tenant_management.dto.CreatePasswordPolicyRequest;
import com.onified.ai.tenant_management.dto.PasswordPolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "platform-management-service")
public interface PlatformPasswordPolicyClient {
    
    @GetMapping("/api/password-policies")
    ResponseEntity<List<PasswordPolicyDto>> getAllPasswordPolicies();
    
    @GetMapping("/api/password-policies/{id}")
    ResponseEntity<PasswordPolicyDto> getPasswordPolicyById(@PathVariable("id") Long id);
    
    @GetMapping("/api/password-policies/default")
    ResponseEntity<PasswordPolicyDto> getDefaultPasswordPolicy();
    
    @PostMapping("/api/password-policies")
    ResponseEntity<PasswordPolicyDto> createPasswordPolicy(@RequestBody CreatePasswordPolicyRequest request);
    
    @PutMapping("/api/password-policies/{id}")
    ResponseEntity<PasswordPolicyDto> updatePasswordPolicy(
            @PathVariable("id") Long id, 
            @RequestBody CreatePasswordPolicyRequest request);
    
    @DeleteMapping("/api/password-policies/{id}")
    ResponseEntity<Void> deletePasswordPolicy(@PathVariable("id") Long id);
    
    @PutMapping("/api/password-policies/{id}/default")
    ResponseEntity<PasswordPolicyDto> setAsDefault(@PathVariable("id") Long id);
} 