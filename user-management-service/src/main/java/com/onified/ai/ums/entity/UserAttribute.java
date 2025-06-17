package com.onified.ai.ums.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_attributes")
@Data
@NoArgsConstructor
public class UserAttribute {

    @EmbeddedId
    private UserAttributeId id;

    @Column(name = "attribute_value", nullable = false)
    private String attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Maps the 'userId' part of the composite key to the User entity's ID
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public UserAttribute(UUID userId, String attributeName, String attributeValue) {
        this.id = new UserAttributeId(userId, attributeName);
        this.attributeValue = attributeValue;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Important for entities with composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAttribute that = (UserAttribute) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserAttribute{" +
                "id=" + id +
                ", attributeValue='" + attributeValue + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
