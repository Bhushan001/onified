package com.onified.ai.permission_registry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.IdClass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_inheritance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RoleInheritance.RoleInheritanceId.class)
public class RoleInheritance {

    @Id
    @Column(name = "parent_role_id")
    private String parentRoleId;

    @Id
    @Column(name = "child_role_id")
    private String childRoleId;
    private String approvedBy;

    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime approvalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInheritanceId implements Serializable {
        private String parentRoleId;
        private String childRoleId;
    }
}
