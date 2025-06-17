package com.onified.ai.permission_registry.client;

import com.onified.ai.permission_registry.model.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client for calling the Application Config Service.
 * Point this to the port where your Application Config Service is running (e.g., 9081).
 */
@FeignClient(name = "application-config-service", url = "http://localhost:9081")
public interface ApplicationConfigServiceClient {

    /**
     * Checks if a given application code exists and is active.
     * Corresponds to GET /api/applications/{appCode} in Application Config Service.
     * Returns the full ApiResponse, from which the body needs to be extracted.
     */
    @GetMapping("/api/applications/{appCode}")
    ApiResponse<ApplicationResponseForClient> getApplicationByAppCode(@PathVariable("appCode") String appCode);

    /**
     * Retrieves application modules by appCode.
     * Corresponds to GET /api/modules/by-app/{appCode} in Application Config Service.
     * Returns the full ApiResponse, from which the list of modules needs to be extracted.
     */
    @GetMapping("/api/modules/by-app/{appCode}")
    ApiResponse<List<ModuleResponseForClient>> getAppModulesByAppCode(@PathVariable("appCode") String appCode);

    /**
     * Inner DTO representing a simplified Application Response from Application Config Service.
     * We only need 'appCode' and 'isActive' for validation purposes here.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ApplicationResponseForClient {
        private String appCode;
        private String displayName; // Include display name as it's typically in the response
        private Boolean isActive;
    }

    /**
     * Inner DTO representing a simplified Module Response from Application Config Service.
     * We only need 'moduleCode' and 'isActive' for validation purposes here.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ModuleResponseForClient {
        private Integer moduleId; // Include ID to match structure, though not directly used for validation logic
        private String appCode;
        private String moduleCode;
        private Boolean isActive;
    }
}
