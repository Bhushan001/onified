package com.onified.ai.appConfig.controller;

import com.onified.ai.appConfig.dto.ApplicationRequestDTO;
import com.onified.ai.appConfig.dto.ApplicationResponseDTO;
import com.onified.ai.appConfig.entity.Application;
import com.onified.ai.appConfig.model.ApiResponse;
import com.onified.ai.appConfig.service.ApplicationModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationModuleService applicationModuleService;


    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> createApplication(@RequestBody ApplicationRequestDTO requestDTO) {
        Application application = new Application(requestDTO.getAppCode(), requestDTO.getDisplayName(), requestDTO.getIsActive());
        Application createdApp = applicationModuleService.createApplication(application);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdApp));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{appCode}")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> getApplicationByAppCode(@PathVariable String appCode) {
        Application application = applicationModuleService.getApplicationByAppCode(appCode);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(application));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
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
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> updateApplication(@PathVariable String appCode, @RequestBody ApplicationRequestDTO requestDTO) {
        Application application = new Application(requestDTO.getAppCode(), requestDTO.getDisplayName(), requestDTO.getIsActive());
        Application updatedApp = applicationModuleService.updateApplication(appCode, application);
        ApiResponse<ApplicationResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedApp));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{appCode}")
    public ResponseEntity<ApiResponse<String>> deleteApplication(@PathVariable String appCode) {
        applicationModuleService.deleteApplication(appCode);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Application deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ApplicationResponseDTO convertToResponseDTO(Application application) {
        return new ApplicationResponseDTO(application.getAppCode(), application.getDisplayName(), application.getIsActive());
    }
}

