package com.onified.ai.ums.client;

import com.onified.ai.ums.model.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "permission-registry-service", url = "${feign.client.config.permission-registry-service.url}")
public interface PermissionRegistryFeignClient {

    @GetMapping("/api/roles/{roleId}")
    ApiResponse<Object> getRoleById(@PathVariable("roleId") String roleId);
}