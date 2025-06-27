package com.onified.ai.tenant_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class TenantManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TenantManagementServiceApplication.class, args);
    }
} 