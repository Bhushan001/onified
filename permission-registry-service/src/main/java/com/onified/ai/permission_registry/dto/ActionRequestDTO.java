package com.onified.ai.permission_registry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequestDTO {
    private String actionCode;
    private String displayName;
    private String description;
    private Boolean isActive;
}