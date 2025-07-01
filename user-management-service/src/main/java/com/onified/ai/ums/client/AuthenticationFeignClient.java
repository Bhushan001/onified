package com.onified.ai.ums.client;

import com.onified.ai.ums.model.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "authentication-service", url = "${feign.client.config.authentication-service.url}")
public interface AuthenticationFeignClient {

    /**
     * Delete a user from Keycloak
     * This calls the authentication service to remove the user from Keycloak
     */
    @DeleteMapping("/api/auth/keycloak/user/{username}")
    ApiResponse<String> deleteUserFromKeycloak(@PathVariable("username") String username);
} 