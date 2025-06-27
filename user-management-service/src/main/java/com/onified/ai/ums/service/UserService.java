package com.onified.ai.ums.service;

import com.onified.ai.ums.constants.ErrorConstants;
import com.onified.ai.ums.client.PermissionRegistryFeignClient;
import com.onified.ai.ums.dto.*;
import com.onified.ai.ums.entity.User;
import com.onified.ai.ums.entity.UserAttribute;
import com.onified.ai.ums.entity.UserAttributeId;
import com.onified.ai.ums.exception.DuplicateUsernameException;
import com.onified.ai.ums.exception.UserNotFoundException;
import com.onified.ai.ums.mapper.UserMapper;
import com.onified.ai.ums.model.ApiResponse;
import com.onified.ai.ums.repository.UserAttributeRepository;
import com.onified.ai.ums.repository.UserRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAttributeRepository userAttributeRepository;
    private final PermissionRegistryFeignClient permissionRegistryFeignClient;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder


    private boolean doesRoleExist(String roleName) {
        try {
            ApiResponse<Object> response = permissionRegistryFeignClient.getRoleById(roleName);
            return response != null && response.getStatusCode() == HttpStatus.OK.value();
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (FeignException ex) {
            System.err.println("Error communicating with Permission Registry Service: " + ex.getMessage());
            throw new RuntimeException("Failed to validate role with Permission Registry Service: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(String.format(ErrorConstants.DUPLICATE_USERNAME, request.getUsername()));
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUsernameException(String.format(ErrorConstants.DUPLICATE_EMAIL, request.getEmail()));
        }

        User user = UserMapper.toUserEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // IMPORTANT: HASH PASSWORD HERE

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                if (!doesRoleExist(roleName)) {
                    throw new UserNotFoundException(String.format(ErrorConstants.ROLE_NOT_FOUND_FOR_USER, roleName, "N/A (during creation)"));
                }
            }
            user.setRoles(request.getRoles());
        }

        User savedUser = userRepository.save(user);
        return UserMapper.toUserResponse(savedUser);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, id)));
        return UserMapper.toUserResponse(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, username)));
        return UserMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, id)));

        UserMapper.updateUserEntityFromDto(request, user);

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            if (!request.getUsername().equals(user.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateUsernameException(String.format(ErrorConstants.DUPLICATE_USERNAME, request.getUsername()));
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateUsernameException(String.format(ErrorConstants.DUPLICATE_EMAIL, request.getEmail()));
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // IMPORTANT: HASH PASSWORD HERE
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, id));
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse assignRoleToUser(UUID userId, RoleAssignmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, userId)));

        if (!doesRoleExist(request.getRoleId())) {
            throw new UserNotFoundException(String.format(ErrorConstants.ROLE_NOT_FOUND_FOR_USER, request.getRoleId(), userId));
        }

        user.addRole(request.getRoleId());
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse removeRoleFromUser(UUID userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, userId)));

        if (!user.getRoles().contains(roleName)) {
            throw new UserNotFoundException(String.format(ErrorConstants.ROLE_NOT_FOUND_FOR_USER, roleName, userId));
        }

        user.removeRole(roleName);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse addOrUpdateUserAttribute(UUID userId, UserAttributeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, userId)));

        UserAttributeId attributeId = new UserAttributeId(userId, request.getAttributeName());
        Optional<UserAttribute> existingAttribute = userAttributeRepository.findById(attributeId);

        UserAttribute attribute;
        if (existingAttribute.isPresent()) {
            attribute = existingAttribute.get();
            attribute.setAttributeValue(request.getAttributeValue());
        } else {
            attribute = new UserAttribute(userId, request.getAttributeName(), request.getAttributeValue());
            attribute.setUser(user);
        }
        user.addAttribute(attribute);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse removeUserAttribute(UUID userId, String attributeName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND, userId)));

        UserAttributeId attributeId = new UserAttributeId(userId, attributeName);
        Optional<UserAttribute> attributeToRemoveOpt = userAttributeRepository.findById(attributeId);

        if (attributeToRemoveOpt.isEmpty()) {
            throw new UserNotFoundException(String.format(ErrorConstants.ATTRIBUTE_NOT_FOUND_FOR_USER, attributeName, userId));
        }

        UserAttribute attributeToRemove = attributeToRemoveOpt.get();
        user.getAttributes().remove(attributeToRemove);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserResponse(updatedUser);
    }

    // New method for Authentication Service to retrieve user details including password hash
    public UserAuthDetailsResponse getUserAuthDetailsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, username)));

        return new UserAuthDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(), // This will now return the HASHED password
                user.getRoles().stream().toList()
        );
    }

    // List all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::toUserResponse)
            .collect(Collectors.toList());
    }
}