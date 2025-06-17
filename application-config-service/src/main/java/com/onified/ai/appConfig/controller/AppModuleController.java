package com.onified.ai.appConfig.controller;

import com.onified.ai.appConfig.dto.ModuleRequestDTO;
import com.onified.ai.appConfig.dto.ModuleResponseDTO;
import com.onified.ai.appConfig.entity.AppModule;
import com.onified.ai.appConfig.model.ApiResponse;
import com.onified.ai.appConfig.service.ApplicationModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class AppModuleController {

    private final ApplicationModuleService applicationModuleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> createAppModule(@RequestBody ModuleRequestDTO requestDTO) {
        AppModule appModule = new AppModule(null, requestDTO.getAppCode(), requestDTO.getModuleCode(), requestDTO.getIsActive());
        AppModule createdAppModule = applicationModuleService.createAppModule(appModule);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), "SUCCESS", convertToResponseDTO(createdAppModule));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> getAppModuleById(@PathVariable Integer moduleId) {
        AppModule appModule = applicationModuleService.getAppModuleById(moduleId);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(appModule));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-app/{appCode}")
    public ResponseEntity<ApiResponse<List<ModuleResponseDTO>>> getAppModulesByAppCode(@PathVariable String appCode) {
        List<AppModule> appModules = applicationModuleService.getAppModulesByAppCode(appCode);
        List<ModuleResponseDTO> responseDTOs = appModules.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        ApiResponse<List<ModuleResponseDTO>> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", responseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<ApiResponse<ModuleResponseDTO>> updateAppModule(@PathVariable Integer moduleId, @RequestBody ModuleRequestDTO requestDTO) {
        AppModule appModule = new AppModule(moduleId, requestDTO.getAppCode(), requestDTO.getModuleCode(), requestDTO.getIsActive());
        AppModule updatedAppModule = applicationModuleService.updateAppModule(moduleId, appModule);
        ApiResponse<ModuleResponseDTO> response = new ApiResponse<>(
                HttpStatus.OK.value(), "SUCCESS", convertToResponseDTO(updatedAppModule));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<ApiResponse<String>> deleteAppModule(@PathVariable Integer moduleId) {
        applicationModuleService.deleteAppModule(moduleId);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(), "SUCCESS", "Module deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    private ModuleResponseDTO convertToResponseDTO(AppModule appModule) {
        return new ModuleResponseDTO(appModule.getModuleId(), appModule.getAppCode(), appModule.getModuleCode(), appModule.getIsActive());
    }
}

