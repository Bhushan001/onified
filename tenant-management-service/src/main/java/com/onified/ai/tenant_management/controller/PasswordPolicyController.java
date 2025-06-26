package com.onified.ai.tenant_management.controller;

import com.onified.ai.tenant_management.dto.CreatePasswordPolicyRequest;
import com.onified.ai.tenant_management.dto.PasswordPolicyDto;
import com.onified.ai.tenant_management.service.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/password-policies")
public class PasswordPolicyController {
    
    @Autowired
    private PasswordPolicyService passwordPolicyService;
    
    @GetMapping
    public ResponseEntity<List<PasswordPolicyDto>> getAllPasswordPolicies() {
        List<PasswordPolicyDto> policies = passwordPolicyService.getAllPasswordPolicies();
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PasswordPolicyDto> getPasswordPolicyById(@PathVariable Long id) {
        PasswordPolicyDto policy = passwordPolicyService.getPasswordPolicyById(id);
        if (policy != null) {
            return ResponseEntity.ok(policy);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/default")
    public ResponseEntity<PasswordPolicyDto> getDefaultPasswordPolicy() {
        PasswordPolicyDto policy = passwordPolicyService.getDefaultPasswordPolicy();
        if (policy != null) {
            return ResponseEntity.ok(policy);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<PasswordPolicyDto> createPasswordPolicy(@RequestBody CreatePasswordPolicyRequest request) {
        PasswordPolicyDto createdPolicy = passwordPolicyService.createPasswordPolicy(request);
        if (createdPolicy != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PasswordPolicyDto> updatePasswordPolicy(
            @PathVariable Long id, 
            @RequestBody CreatePasswordPolicyRequest request) {
        PasswordPolicyDto updatedPolicy = passwordPolicyService.updatePasswordPolicy(id, request);
        if (updatedPolicy != null) {
            return ResponseEntity.ok(updatedPolicy);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePasswordPolicy(@PathVariable Long id) {
        boolean deleted = passwordPolicyService.deletePasswordPolicy(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/default")
    public ResponseEntity<PasswordPolicyDto> setAsDefault(@PathVariable Long id) {
        PasswordPolicyDto policy = passwordPolicyService.setAsDefault(id);
        if (policy != null) {
            return ResponseEntity.ok(policy);
        }
        return ResponseEntity.notFound().build();
    }
} 