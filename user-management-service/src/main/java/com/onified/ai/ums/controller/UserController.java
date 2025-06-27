package com.onified.ai.ums.controller;

import com.onified.ai.ums.dto.*;
import com.onified.ai.ums.model.ApiResponse;
import com.onified.ai.ums.model.CustomErrorResponse;
import com.onified.ai.ums.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth-details/{username}")
    public ResponseEntity<ApiResponse<UserAuthDetailsResponse>> getUserAuthDetailsByUsername(@PathVariable String username) {
        UserAuthDetailsResponse userAuthDetails = userService.getUserAuthDetailsByUsername(username);
        ApiResponse<UserAuthDetailsResponse> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                userAuthDetails
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse userResponse = userService.createUser(request);
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
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
    public ResponseEntity<?> assignRoleToUser(@PathVariable UUID id, @Valid @RequestBody RoleAssignmentRequest request) {
        try {
            UserResponse userResponse = userService.assignRoleToUser(id, request);
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable UUID id, @PathVariable String roleName) {
        try {
            UserResponse userResponse = userService.removeRoleFromUser(id, roleName);
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    MessageConstants.STATUS_SUCCESS,
                    userResponse
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FeignException fe) {
            String errorBody = fe.contentUTF8();
            try {
                ObjectMapper mapper = new ObjectMapper();
                CustomErrorResponse customError = mapper.readValue(errorBody, CustomErrorResponse.class);
                return ResponseEntity.status(fe.status()).body(customError);
            } catch (Exception ex) {
                return ResponseEntity.status(fe.status()).body(
                    new CustomErrorResponse(String.valueOf(fe.status()), errorBody)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomErrorResponse(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Internal server error"
                ));
        }
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        ApiResponse<List<UserResponse>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                MessageConstants.STATUS_SUCCESS,
                users
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
