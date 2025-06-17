package com.onified.ai.ums.mapper;

import com.onified.ai.ums.dto.UserCreateRequest;
import com.onified.ai.ums.dto.UserResponse;
import com.onified.ai.ums.dto.UserUpdateRequest;
import com.onified.ai.ums.entity.User;
import com.onified.ai.ums.entity.UserAttribute;

import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
        // Private constructor to prevent instantiation
    }

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setStatus(user.getStatus());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        userResponse.setRoles(user.getRoles());

        if (user.getAttributes() != null) {
            userResponse.setAttributes(
                    user.getAttributes().stream()
                            .map(UserMapper::toUserAttributeResponse)
                            .collect(Collectors.toSet())
            );
        } else {
            userResponse.setAttributes(java.util.Collections.emptySet());
        }

        return userResponse;
    }

    public static UserResponse.UserAttributeResponse toUserAttributeResponse(UserAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        return new UserResponse.UserAttributeResponse(
                attribute.getId().getAttributeName(),
                attribute.getAttributeValue()
        );
    }

    public static User toUserEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStatus(User.UserStatus.ACTIVE);
        return user;
    }

    public static void updateUserEntityFromDto(UserUpdateRequest request, User user) {
        if (request == null || user == null) {
            return;
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
    }
}