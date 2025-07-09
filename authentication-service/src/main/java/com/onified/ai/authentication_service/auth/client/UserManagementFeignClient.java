package com.onified.ai.authentication_service.auth.client;

import com.onified.ai.authentication_service.dto.UserAuthDetailsResponse;
import com.onified.ai.authentication_service.dto.UserCreateRequest;
import com.onified.ai.authentication_service.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-management-service", url = "${feign.client.config.user-management-service.url}")
public interface UserManagementFeignClient {

    /**
     * Calls the User Management Service to get authentication-specific user details
     * (username, hashed password, roles).
     * This assumes UMS has an endpoint like GET /users/auth-details/{username}
     * that returns ApiResponse<UserAuthDetailsResponse>.
     *
     * You will need to add this endpoint to your User Management Service.
     */
    @GetMapping("/api/users/auth-details/{username}")
    ResponseEntity<Object> getUserAuthDetailsByUsername(@PathVariable("username") String username);

    @PostMapping("/api/users")
    ResponseEntity<Object> createUser(@RequestBody UserCreateRequest request);
}