package com.onified.ai.platform_management.controller;

import com.onified.ai.platform_management.entity.PasswordPolicy;
import com.onified.ai.platform_management.service.PasswordPolicyService;
import com.onified.ai.platform_management.dto.ApiResponse;
import com.onified.ai.platform_management.dto.CustomErrorResponse;
import com.onified.ai.platform_management.constants.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-policies")
@Tag(name = "Password Policy Management", description = "APIs for managing password policies and security configurations")
public class PasswordPolicyController {
    
    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @GetMapping("/platform")
    @Operation(
        summary = "Get Platform Default Password Policy",
        description = "Retrieves the default password policy configured for the platform with standardized API response format",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password policy retrieved successfully",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Default password policy not found",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = com.onified.ai.platform_management.dto.ApiResponse.class))
            )
        }
    )
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
                CustomErrorResponse errorDetails = new CustomErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        MessageConstants.STATUS_ERROR,
                        "Default platform password policy not found.",
                        null
                );
                ApiResponse<CustomErrorResponse> errorResponse = new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        MessageConstants.STATUS_ERROR,
                        errorDetails
                );
                return new ResponseEntity(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            CustomErrorResponse errorDetails = new CustomErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    MessageConstants.STATUS_ERROR,
                    "An unexpected error occurred: " + ex.getMessage(),
                    null
            );
            ApiResponse<CustomErrorResponse> errorResponse = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    MessageConstants.STATUS_ERROR,
                    errorDetails
            );
            return new ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    @Operation(
        summary = "Get All Password Policies",
        description = "Retrieves all password policies in the system",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "List of password policies retrieved successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            )
        }
    )
    public ResponseEntity<List<PasswordPolicy>> getAllPasswordPolicies() {
        List<PasswordPolicy> policies = passwordPolicyService.getAllPasswordPolicies();
        return ResponseEntity.ok(policies);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Password Policy by ID",
        description = "Retrieves a specific password policy by its unique identifier",
        parameters = {
            @Parameter(name = "id", description = "Unique identifier of the password policy", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password policy found and retrieved successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Password policy not found"
            )
        }
    )
    public ResponseEntity<PasswordPolicy> getPasswordPolicyById(@PathVariable Long id) {
        Optional<PasswordPolicy> policy = passwordPolicyService.getPasswordPolicyById(id);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{policyName}")
    @Operation(
        summary = "Get Password Policy by Name",
        description = "Retrieves a password policy by its name",
        parameters = {
            @Parameter(name = "policyName", description = "Name of the password policy", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password policy found and retrieved successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Password policy not found"
            )
        }
    )
    public ResponseEntity<PasswordPolicy> getPasswordPolicyByName(@PathVariable String policyName) {
        Optional<PasswordPolicy> policy = passwordPolicyService.getPasswordPolicyByName(policyName);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/default")
    @Operation(
        summary = "Get Default Password Policy",
        description = "Retrieves the currently configured default password policy",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Default password policy retrieved successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "No default password policy configured"
            )
        }
    )
    public ResponseEntity<PasswordPolicy> getDefaultPasswordPolicy() {
        Optional<PasswordPolicy> policy = passwordPolicyService.getDefaultPasswordPolicy();
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(
        summary = "Create New Password Policy",
        description = "Creates a new password policy with the specified configuration",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Password policy configuration",
            required = true,
            content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Password policy created successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid password policy configuration"
            )
        }
    )
    public ResponseEntity<PasswordPolicy> createPasswordPolicy(@RequestBody PasswordPolicy passwordPolicy) {
        try {
            PasswordPolicy createdPolicy = passwordPolicyService.createPasswordPolicy(passwordPolicy);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update Password Policy",
        description = "Updates an existing password policy with new configuration",
        parameters = {
            @Parameter(name = "id", description = "Unique identifier of the password policy to update", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated password policy configuration",
            required = true,
            content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
        ),
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password policy updated successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid password policy configuration"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Password policy not found"
            )
        }
    )
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
    @Operation(
        summary = "Delete Password Policy",
        description = "Deletes a password policy by its ID. Cannot delete the default policy.",
        parameters = {
            @Parameter(name = "id", description = "Unique identifier of the password policy to delete", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "Password policy deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Cannot delete default password policy or invalid request"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Password policy not found"
            )
        }
    )
    public ResponseEntity<Void> deletePasswordPolicy(@PathVariable Long id) {
        try {
            passwordPolicyService.deletePasswordPolicy(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/default")
    @Operation(
        summary = "Set Password Policy as Default",
        description = "Sets a specific password policy as the default policy for the platform",
        parameters = {
            @Parameter(name = "id", description = "Unique identifier of the password policy to set as default", required = true)
        },
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password policy set as default successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request or policy not found"
            )
        }
    )
    public ResponseEntity<PasswordPolicy> setAsDefault(@PathVariable Long id) {
        try {
            PasswordPolicy defaultPolicy = passwordPolicyService.setAsDefault(id);
            return ResponseEntity.ok(defaultPolicy);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/default/ensure")
    @Operation(
        summary = "Ensure Default Password Policy Exists",
        description = "Creates a default password policy if none exists, or returns the existing default policy",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Default password policy ensured and retrieved successfully",
                content = @Content(schema = @Schema(implementation = PasswordPolicy.class))
            )
        }
    )
    public ResponseEntity<PasswordPolicy> ensureDefaultPolicyExists() {
        PasswordPolicy defaultPolicy = passwordPolicyService.getOrCreateDefaultPolicy();
        return ResponseEntity.ok(defaultPolicy);
    }
}