package com.onified.platform.metadata.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metadata_database")
@Getter
@Setter
public class MetadataDatabase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "database_id")
    private UUID databaseId;

    @Column(name = "database_name", nullable = false, length = 200)
    private String databaseName;

    @Column(name = "database_type", nullable = false, length = 50)
    private String databaseType;

    @Column(name = "host", nullable = false, length = 200)
    private String host;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "schema_name", length = 100)
    private String schemaName;

    @Column(name = "connection_pool_size")
    private Integer connectionPoolSize = 10;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "database", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MetadataSharedDatabase> sharedDatabases;

    @Override
    public String toString() {
        return "MetadataDatabase{" +
                "databaseId=" + databaseId +
                ", databaseName='" + databaseName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", connectionPoolSize=" + connectionPoolSize +
                ", isActive=" + isActive +
                ", " + super.toString() +
                '}';
    }
}
