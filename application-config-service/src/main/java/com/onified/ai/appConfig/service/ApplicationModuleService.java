package com.onified.ai.appConfig.service;

import com.onified.ai.appConfig.constants.ErrorMessages;
import com.onified.ai.appConfig.entity.AppModule;
import com.onified.ai.appConfig.entity.Application;
import com.onified.ai.appConfig.exception.BadRequestException;
import com.onified.ai.appConfig.exception.ConflictException;
import com.onified.ai.appConfig.exception.ResourceNotFoundException;
import com.onified.ai.appConfig.repository.AppModuleRepository;
import com.onified.ai.appConfig.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationModuleService {

    private final ApplicationRepository applicationRepository;
    private final AppModuleRepository appModuleRepository;

    public Application createApplication(Application application) {
        if (applicationRepository.existsById(application.getAppCode())) {
            throw new ConflictException(String.format(ErrorMessages.APPLICATION_ALREADY_EXISTS, application.getAppCode()));
        }
        return applicationRepository.save(application);
    }

    public Application getApplicationByAppCode(String appCode) {
        return applicationRepository.findById(appCode)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.APPLICATION_NOT_FOUND, appCode)));
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application updateApplication(String appCode, Application updatedApplication) {
        return applicationRepository.findById(appCode).map(existingApp -> {
            existingApp.setDisplayName(updatedApplication.getDisplayName());
            existingApp.setIsActive(updatedApplication.getIsActive());
            return applicationRepository.save(existingApp);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.APPLICATION_NOT_FOUND, appCode)));
    }

    public void deleteApplication(String appCode) {
        if (!applicationRepository.existsById(appCode)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.APPLICATION_NOT_FOUND, appCode));
        }
        appModuleRepository.findByAppCode(appCode).forEach(appModuleRepository::delete);
        applicationRepository.deleteById(appCode);
    }

    public AppModule createAppModule(AppModule appModule) {
        if (!applicationRepository.existsById(appModule.getAppCode())) {
            throw new BadRequestException(String.format(ErrorMessages.MODULE_APP_NOT_FOUND, appModule.getAppCode()));
        }
        if (appModuleRepository.findByAppCodeAndModuleCode(appModule.getAppCode(), appModule.getModuleCode()).isPresent()) {
            throw new ConflictException(String.format(ErrorMessages.MODULE_ALREADY_EXISTS, appModule.getAppCode(), appModule.getModuleCode()));
        }
        return appModuleRepository.save(appModule);
    }

    public AppModule getAppModuleById(Integer moduleId) {
        return appModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_FOUND, moduleId)));
    }

    public List<AppModule> getAppModulesByAppCode(String appCode) {
        if (!applicationRepository.existsById(appCode)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.APPLICATION_NOT_FOUND, appCode));
        }
        return appModuleRepository.findByAppCode(appCode);
    }

    public AppModule updateAppModule(Integer moduleId, AppModule updatedAppModule) {
        return appModuleRepository.findById(moduleId).map(existingAppModule -> {
            if (!applicationRepository.existsById(updatedAppModule.getAppCode())) {
                throw new BadRequestException(String.format(ErrorMessages.MODULE_APP_NOT_FOUND, updatedAppModule.getAppCode()));
            }
            if (!existingAppModule.getAppCode().equals(updatedAppModule.getAppCode()) ||
                    !existingAppModule.getModuleCode().equals(updatedAppModule.getModuleCode())) {
                if (appModuleRepository.findByAppCodeAndModuleCode(updatedAppModule.getAppCode(), updatedAppModule.getModuleCode())
                        .filter(m -> !m.getModuleId().equals(moduleId)).isPresent()) {
                    throw new ConflictException(String.format(ErrorMessages.MODULE_ALREADY_EXISTS, updatedAppModule.getAppCode(), updatedAppModule.getModuleCode()));
                }
            }

            existingAppModule.setAppCode(updatedAppModule.getAppCode());
            existingAppModule.setModuleCode(updatedAppModule.getModuleCode());
            existingAppModule.setIsActive(updatedAppModule.getIsActive());
            return appModuleRepository.save(existingAppModule);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_FOUND, moduleId)));
    }

    public void deleteAppModule(Integer moduleId) {
        if (!appModuleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_FOUND, moduleId));
        }
        appModuleRepository.deleteById(moduleId);
    }
}

