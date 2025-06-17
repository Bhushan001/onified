package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "role_contextual_behaviors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RoleContextualBehavior.RoleContextualBehaviorId.class)
public class RoleContextualBehavior {

    @Id
    @Column(name = "role_id")
    private String roleId;

    @Id
    @Column(name = "behavior_id")
    private String behaviorId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleContextualBehaviorId implements Serializable {
        private String roleId;
        private String behaviorId;
    }
}