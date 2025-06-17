package com.onified.ai.appConfig.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
public class Application extends Auditable {

    @Id
    private String appCode;

    private String displayName;

    private Boolean isActive;

    public Application(String appCode, String displayName, Boolean isActive) {
        this.appCode = appCode;
        this.displayName = displayName;
        this.isActive = isActive;
    }
}