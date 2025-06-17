package com.onified.ai.ums.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data // @Data generates equals/hashCode/toString, which is good for @Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "attribute_name")
    private String attributeName;

    // Lombok's @Data correctly implements equals() and hashCode() for all fields.
    // This is crucial for composite keys.
}
