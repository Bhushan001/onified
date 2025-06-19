package com.onified.ai.authentication_service.service;

import com.onified.ai.authentication_service.auth.client.UserManagementFeignClient;
import com.onified.ai.authentication_service.constants.ErrorConstants;
import com.onified.ai.authentication_service.dto.LoginRequest;
import com.onified.ai.authentication_service.dto.LoginResponse;
import com.onified.ai.authentication_service.dto.UserAuthDetailsResponse;
import com.onified.ai.authentication_service.exception.BadCredentialsException;
import com.onified.ai.authentication_service.exception.UserNotFoundException;
import com.onified.ai.authentication_service.model.ApiResponse;
import com.onified.ai.authentication_service.security.JwtUtil;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserManagementFeignClient userManagementFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserManagementFeignClient userManagementFeignClient,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userManagementFeignClient = userManagementFeignClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse loginUser(LoginRequest request) {
        UserAuthDetailsResponse userAuthDetails;
        try {
            // 1. Fetch user authentication details from User Management Service
            ApiResponse<UserAuthDetailsResponse> umsResponse =
                    userManagementFeignClient.getUserAuthDetailsByUsername(request.getUsername());

            if (umsResponse == null || umsResponse.getStatusCode() != HttpStatus.OK.value() || umsResponse.getBody() == null) {
                throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, request.getUsername()));
            }
            userAuthDetails = umsResponse.getBody();

        } catch (FeignException.NotFound ex) {
            // User not found in UMS
            throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_USERNAME, request.getUsername()));
        } catch (FeignException ex) {
            // Other Feign errors (e.g., UMS down, network issues)
            System.err.println("Error communicating with User Management Service: " + ex.getMessage());
            throw new RuntimeException(ErrorConstants.UMS_COMMUNICATION_ERROR + ex.getMessage(), ex);
        }

        // 2. Verify password (THIS IS WHERE HASHING IS CRUCIAL)
        // passwordEncoder.matches(rawPassword, hashedPasswordFromUMS)
        if (!passwordEncoder.matches(request.getPassword(), userAuthDetails.getPasswordHash())) {
            throw new BadCredentialsException(ErrorConstants.INVALID_CREDENTIALS);
        }

        // 3. Generate JWT
        String token = jwtUtil.generateToken(userAuthDetails.getId(), userAuthDetails.getUsername(), userAuthDetails.getRoles());

        return new LoginResponse(token, userAuthDetails.getUsername());
    }
}
