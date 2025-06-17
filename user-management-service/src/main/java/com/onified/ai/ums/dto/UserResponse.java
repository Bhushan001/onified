package com.onified.ai.ums.dto;

import com.onified.ai.ums.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<String> roles;
    private Set<UserAttributeResponse> attributes;

    // Inner DTO for UserAttribute
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAttributeResponse {
        private String attributeName;
        private String attributeValue;
    }
}
