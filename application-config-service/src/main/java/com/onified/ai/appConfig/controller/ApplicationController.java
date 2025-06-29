package com.onified.ai.appConfig.controller;

import com.onified.ai.appConfig.dto.ApplicationRequestDTO;
import com.onified.ai.appConfig.dto.ApplicationResponseDTO;
import com.onified.ai.appConfig.entity.Application;
import com.onified.ai.appConfig.model.ApiResponse;
import com.onified.ai.appConfig.service.ApplicationModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management", description = "APIs for managing applications in the Onified platform")
public class ApplicationController {

    private final ApplicationModuleService applicationModuleService;

    @PostMapping
    @Operation(
        summary = "Create a new application",
        description = "Creates a new application with the provided details. The application code must be unique."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Application created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 201,
                        "status": "SUCCESS",
                        "body": {
                            "appCode": "APP001",
                            "displayName": "User Management System",
                            "isActive": true
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - Application code already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> createApplication(
            @Parameter(description = "Application details", required = true)
            @RequestBody ApplicationRequestDTO requestDTO) {
        Application application = new Application(requestDTO.getAppCode(), requestDTO.getDisplayName(), requestDTO.getIsActive());
        Application createdApp = applicationModuleService.createApplication(application);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdApp));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{appCode}")
    @Operation(
        summary = "Get application by code",
        description = "Retrieves an application by its unique application code."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Application found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> getApplicationByAppCode(
            @Parameter(description = "Unique application code", required = true, example = "APP001")
            @PathVariable String appCode) {
        Application application = applicationModuleService.getApplicationByAppCode(appCode);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(application));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(
        summary = "Get all applications",
        description = "Retrieves a list of all applications in the system."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Applications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<ApplicationResponseDTO>>> getAllApplications() {
        List<Application> applications = applicationModuleService.getAllApplications();
        List<ApplicationResponseDTO> responseDTOs = applications.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ApplicationResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{appCode}")
    @Operation(
        summary = "Update an application",
        description = "Updates an existing application with new details. The application code in the path must match the one in the request body."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Application updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> updateApplication(
            @Parameter(description = "Application code to update", required = true, example = "APP001")
            @PathVariable String appCode,
            @Parameter(description = "Updated application details", required = true)
            @RequestBody ApplicationRequestDTO requestDTO) {
        Application application = new Application(requestDTO.getAppCode(), requestDTO.getDisplayName(), requestDTO.getIsActive());
        Application updatedApp = applicationModuleService.updateApplication(appCode, application);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedApp));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{appCode}")
    @Operation(
        summary = "Delete an application",
        description = "Deletes an application by its application code. This operation is irreversible."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", 
            description = "Application deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 204,
                        "status": "SUCCESS",
                        "body": "Application deleted successfully."
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> deleteApplication(
            @Parameter(description = "Application code to delete", required = true, example = "APP001")
            @PathVariable String appCode) {
        applicationModuleService.deleteApplication(appCode);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Application deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ApplicationResponseDTO convertToResponseDTO(Application application) {
        return new ApplicationResponseDTO(application.getAppCode(), application.getDisplayName(), application.getIsActive());
    }
}

