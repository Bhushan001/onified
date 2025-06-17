package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action extends Auditable {

    @Id
    private String actionCode;
    private String displayName;
    private String description;
    private Boolean isActive;

}