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
@Table(name = "pbu_contextual_behaviors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PbuContextualBehavior.PbuContextualBehaviorId.class)
public class PbuContextualBehavior {

    @Id
    @Column(name = "pbu_id")
    private String pbuId;

    @Id
    @Column(name = "behavior_id")
    private String behaviorId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PbuContextualBehaviorId implements Serializable {
        private String pbuId;
        private String behaviorId;
    }
}

