package com.onified.ai.platform_management.controller;

import com.onified.ai.platform_management.entity.PasswordPolicy;
import com.onified.ai.platform_management.service.PasswordPolicyService;
import com.onified.ai.platform_management.dto.ApiResponse;
import com.onified.ai.platform_management.dto.CustomErrorResponse;
import com.onified.ai.platform_management.constants.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-policies")
public class PasswordPolicyController {
    
    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @GetMapping("/platform")
    public ResponseEntity<ApiResponse<PasswordPolicy>> getPlatformPasswordPolicy() {
        try {
            Optional<PasswordPolicy> policy = passwordPolicyService.getDefaultPasswordPolicy();
            if (policy.isPresent()) {
                ApiResponse<PasswordPolicy> response = new ApiResponse<>(
                        HttpStatus.OK.value(),
                        MessageConstants.STATUS_SUCCESS,
                        policy.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<CustomErrorResponse> errorResponse = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        MessageConstants.STATUS_ERROR,
                        new CustomErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                MessageConstants.STATUS_ERROR,
                                "Default platform password policy not found.",
                                null
                        )
                );
                return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ApiResponse<CustomErrorResponse> errorResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    MessageConstants.STATUS_ERROR,
                    new CustomErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            MessageConstants.STATUS_ERROR,
                            "An unexpected error occurred: " + ex.getMessage(),
                            null
                    )
            );
            return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PasswordPolicy>> getAllPasswordPolicies() {
        List<PasswordPolicy> policies = passwordPolicyService.getAllPasswordPolicies();
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PasswordPolicy> getPasswordPolicyById(@PathVariable Long id) {
        Optional<PasswordPolicy> policy = passwordPolicyService.getPasswordPolicyById(id);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{policyName}")
    public ResponseEntity<PasswordPolicy> getPasswordPolicyByName(@PathVariable String policyName) {
        Optional<PasswordPolicy> policy = passwordPolicyService.getPasswordPolicyByName(policyName);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/default")
    public ResponseEntity<PasswordPolicy> getDefaultPasswordPolicy() {
        Optional<PasswordPolicy> policy = passwordPolicyService.getDefaultPasswordPolicy();
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<PasswordPolicy> createPasswordPolicy(@RequestBody PasswordPolicy passwordPolicy) {
        try {
            PasswordPolicy createdPolicy = passwordPolicyService.createPasswordPolicy(passwordPolicy);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PasswordPolicy> updatePasswordPolicy(
            @PathVariable Long id, 
            @RequestBody PasswordPolicy passwordPolicy) {
        try {
            PasswordPolicy updatedPolicy = passwordPolicyService.updatePasswordPolicy(id, passwordPolicy);
            return ResponseEntity.ok(updatedPolicy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePasswordPolicy(@PathVariable Long id) {
        try {
            passwordPolicyService.deletePasswordPolicy(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/default")
    public ResponseEntity<PasswordPolicy> setAsDefault(@PathVariable Long id) {
        try {
            PasswordPolicy defaultPolicy = passwordPolicyService.setAsDefault(id);
            return ResponseEntity.ok(defaultPolicy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/default/ensure")
    public ResponseEntity<PasswordPolicy> ensureDefaultPolicyExists() {
        PasswordPolicy defaultPolicy = passwordPolicyService.getOrCreateDefaultPolicy();
        return ResponseEntity.ok(defaultPolicy);
    }
}