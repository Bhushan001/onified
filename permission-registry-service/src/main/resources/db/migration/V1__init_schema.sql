-- Actions
CREATE TABLE actions (
    action_code VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Scopes
CREATE TABLE scopes (
    scope_code VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Roles
CREATE TABLE roles (
    role_id VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    app_code VARCHAR(255),
    module_code VARCHAR(255),
    role_function VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    inheritance_depth INTEGER DEFAULT 0,
    tenant_customizable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Permission Bundle Units
CREATE TABLE permission_bundle_units (
    pbu_id VARCHAR(255) PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    api_endpoint VARCHAR(500),
    action_code VARCHAR(255),
    scope_code VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    version VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (action_code) REFERENCES actions(action_code),
    FOREIGN KEY (scope_code) REFERENCES scopes(scope_code)
);

-- Role Inheritance
CREATE TABLE role_inheritance (
    parent_role_id VARCHAR(255) NOT NULL,
    child_role_id VARCHAR(255) NOT NULL,
    approved_by VARCHAR(255),
    approval_date TIMESTAMP,
    PRIMARY KEY (parent_role_id, child_role_id),
    FOREIGN KEY (parent_role_id) REFERENCES roles(role_id),
    FOREIGN KEY (child_role_id) REFERENCES roles(role_id)
);

-- Contextual Behaviors
CREATE TABLE contextual_behaviors (
    id SERIAL PRIMARY KEY,
    behavior_id VARCHAR(255) UNIQUE NOT NULL,
    behavior_code VARCHAR(255),
    display_name VARCHAR(255),
    condition_logic JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- General Constraints
CREATE TABLE general_constraints (
    constraint_id VARCHAR(255) PRIMARY KEY,
    constraint_name VARCHAR(255) NOT NULL,
    table_name VARCHAR(255),
    column_name VARCHAR(255),
    value_type VARCHAR(255),
    table_value JSONB,
    custom_value JSONB,
    rule_logic JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Field Constraints
CREATE TABLE field_constraints (
    constraint_id VARCHAR(255) PRIMARY KEY,
    entity_name VARCHAR(255) NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    access_type VARCHAR(100),
    condition_logic JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Role Contextual Behaviors
CREATE TABLE role_contextual_behaviors (
    role_id VARCHAR(255) NOT NULL,
    behavior_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, behavior_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (behavior_id) REFERENCES contextual_behaviors(behavior_id)
);

-- Role General Constraints
CREATE TABLE role_general_constraints (
    role_id VARCHAR(255) NOT NULL,
    constraint_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, constraint_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (constraint_id) REFERENCES general_constraints(constraint_id)
);

-- Role Field Constraints
CREATE TABLE role_field_constraints (
    role_id VARCHAR(255) NOT NULL,
    constraint_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, constraint_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (constraint_id) REFERENCES field_constraints(constraint_id)
);

-- PBU Contextual Behaviors
CREATE TABLE pbu_contextual_behaviors (
    pbu_id VARCHAR(255) NOT NULL,
    behavior_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (pbu_id, behavior_id),
    FOREIGN KEY (pbu_id) REFERENCES permission_bundle_units(pbu_id),
    FOREIGN KEY (behavior_id) REFERENCES contextual_behaviors(behavior_id)
);

-- PBU General Constraints
CREATE TABLE pbu_general_constraints (
    pbu_id VARCHAR(255) NOT NULL,
    constraint_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (pbu_id, constraint_id),
    FOREIGN KEY (pbu_id) REFERENCES permission_bundle_units(pbu_id),
    FOREIGN KEY (constraint_id) REFERENCES general_constraints(constraint_id)
);

-- PBU Field Constraints
CREATE TABLE pbu_field_constraints (
    pbu_id VARCHAR(255) NOT NULL,
    constraint_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (pbu_id, constraint_id),
    FOREIGN KEY (pbu_id) REFERENCES permission_bundle_units(pbu_id),
    FOREIGN KEY (constraint_id) REFERENCES field_constraints(constraint_id)
); 