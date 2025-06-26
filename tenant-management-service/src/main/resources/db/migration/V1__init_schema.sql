CREATE TABLE tenant_config (
    tenant_id VARCHAR(255) PRIMARY KEY,
    branding TEXT DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE tenant_app_subscriptions (
    tenant_id VARCHAR(255) NOT NULL,
    app_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, app_id),
    FOREIGN KEY (tenant_id) REFERENCES tenant_config(tenant_id) ON DELETE CASCADE
); 