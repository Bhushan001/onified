package com.onified.ai.appConfig.controller;

import com.onified.ai.appConfig.dto.ModuleRequestDTO;
import com.onified.ai.appConfig.dto.ModuleResponseDTO;
import com.onified.ai.appConfig.entity.AppModule;
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
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Module Management", description = "APIs for managing application modules in the Onified platform")
public class AppModuleController {

    private final ApplicationModuleService applicationModuleService;

    @PostMapping
    @Operation(
        summary = "Create a new application module",
        description = "Creates a new module for an existing application. The module code must be unique within the application."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Module created successfully",
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
                            "moduleId": 1,
                            "appCode": "APP001",
                            "moduleCode": "MOD001",
                            "isActive": true
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - Module code already exists in the application"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> createAppModule(
            @Parameter(description = "Module details", required = true)
            @RequestBody ModuleRequestDTO requestDTO) {
        AppModule appModule = new AppModule(null, requestDTO.getAppCode(), requestDTO.getModuleCode(), requestDTO.getIsActive());
        AppModule createdAppModule = applicationModuleService.createAppModule(appModule);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdAppModule));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{moduleId}")
    @Operation(
        summary = "Get module by ID",
        description = "Retrieves a module by its unique module ID."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Module found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Module not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> getAppModuleById(
            @Parameter(description = "Unique module identifier", required = true, example = "1")
            @PathVariable Integer moduleId) {
        AppModule appModule = applicationModuleService.getAppModuleById(moduleId);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(appModule));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-app/{appCode}")
    @Operation(
        summary = "Get modules by application code",
        description = "Retrieves all modules for a specific application."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Modules retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<ModuleResponseDTO>>> getAppModulesByAppCode(
            @Parameter(description = "Application code to get modules for", required = true, example = "APP001")
            @PathVariable String appCode) {
        List<AppModule> appModules = applicationModuleService.getAppModulesByAppCode(appCode);
        List<ModuleResponseDTO> responseDTOs = appModules.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ModuleResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{moduleId}")
    @Operation(
        summary = "Update an application module",
        description = "Updates an existing module with new details. The module ID in the path must match the one being updated."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Module updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Module not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> updateAppModule(
            @Parameter(description = "Module ID to update", required = true, example = "1")
            @PathVariable Integer moduleId,
            @Parameter(description = "Updated module details", required = true)
            @RequestBody ModuleRequestDTO requestDTO) {
        AppModule appModule = new AppModule(moduleId, requestDTO.getAppCode(), requestDTO.getModuleCode(), requestDTO.getIsActive());
        AppModule updatedAppModule = applicationModuleService.updateAppModule(moduleId, appModule);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedAppModule));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{moduleId}")
    @Operation(
        summary = "Delete an application module",
        description = "Deletes a module by its module ID. This operation is irreversible."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204", 
            description = "Module deleted successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.onified.ai.appConfig.model.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "statusCode": 204,
                        "status": "SUCCESS",
                        "body": "Module deleted successfully."
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Module not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<String>> deleteAppModule(
            @Parameter(description = "Module ID to delete", required = true, example = "1")
            @PathVariable Integer moduleId) {
        applicationModuleService.deleteAppModule(moduleId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Module deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ModuleResponseDTO convertToResponseDTO(AppModule appModule) {
        return new ModuleResponseDTO(appModule.getModuleId(), appModule.getAppCode(), appModule.getModuleCode(), appModule.getIsActive());
    }
}

