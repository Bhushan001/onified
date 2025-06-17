package com.onified.ai.ums.controller;

import com.onified.ai.ums.dto.*;
import com.onified.ai.ums.model.ApiResponse;
import com.onified.ai.ums.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = userService.createUser(request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse userResponse = userService.getUserById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse userResponse = userService.getUserByUsername(username);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse userResponse = userService.updateUser(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoleToUser(@PathVariable UUID id, @Valid @RequestBody RoleAssignmentRequest request) {
        UserResponse userResponse = userService.assignRoleToUser(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<ApiResponse<UserResponse>> removeRoleFromUser(@PathVariable UUID id, @PathVariable String roleName) {
        UserResponse userResponse = userService.removeRoleFromUser(id, roleName);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/attributes")
    public ResponseEntity<ApiResponse<UserResponse>> addOrUpdateUserAttribute(@PathVariable UUID id, @Valid @RequestBody UserAttributeRequest request) {
        UserResponse userResponse = userService.addOrUpdateUserAttribute(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/attributes/{attributeName}")
    public ResponseEntity<ApiResponse<UserResponse>> removeUserAttribute(@PathVariable UUID id, @PathVariable String attributeName) {
        UserResponse userResponse = userService.removeUserAttribute(id, attributeName);
        ApiResponse<UserResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
