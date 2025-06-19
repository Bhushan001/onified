package com.onified.ai.ums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDetailsResponse {
    private UUID id;
    private String username;
    private String passwordHash;
    private List<String> roles; // Or Set<String> if that's how it's stored in UMS entity
    // You might add user status here if you want to prevent login for inactive users
}
