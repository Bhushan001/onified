-- Onified Platform - Database Creation Script
-- This script creates all required databases for the Onified platform
-- It will be automatically executed when the PostgreSQL container starts

-- Create databases for each service
-- Note: PostgreSQL doesn't support CREATE DATABASE IF NOT EXISTS, so we'll use a different approach

-- Platform Management Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'platform_mgmt_db') THEN
        CREATE DATABASE platform_mgmt_db;
        RAISE NOTICE 'Created database: platform_mgmt_db';
    ELSE
        RAISE NOTICE 'Database platform_mgmt_db already exists';
    END IF;
END
$$;

-- Tenant Management Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tenant_management_db') THEN
        CREATE DATABASE tenant_management_db;
        RAISE NOTICE 'Created database: tenant_management_db';
    ELSE
        RAISE NOTICE 'Database tenant_management_db already exists';
    END IF;
END
$$;

-- User Management Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_management_db') THEN
        CREATE DATABASE user_management_db;
        RAISE NOTICE 'Created database: user_management_db';
    ELSE
        RAISE NOTICE 'Database user_management_db already exists';
    END IF;
END
$$;

-- Permission Registry Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'permission_registry_db') THEN
        CREATE DATABASE permission_registry_db;
        RAISE NOTICE 'Created database: permission_registry_db';
    ELSE
        RAISE NOTICE 'Database permission_registry_db already exists';
    END IF;
END
$$;

-- Application Config Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'application_config_db') THEN
        CREATE DATABASE application_config_db;
        RAISE NOTICE 'Created database: application_config_db';
    ELSE
        RAISE NOTICE 'Database application_config_db already exists';
    END IF;
END
$$;

-- Authentication Service Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'authentication_db') THEN
        CREATE DATABASE authentication_db;
        RAISE NOTICE 'Created database: authentication_db';
    ELSE
        RAISE NOTICE 'Database authentication_db already exists';
    END IF;
END
$$;

-- Keycloak Database
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak_db') THEN
        CREATE DATABASE keycloak_db;
        RAISE NOTICE 'Created database: keycloak_db';
    ELSE
        RAISE NOTICE 'Database keycloak_db already exists';
    END IF;
END
$$;

-- List all created databases
SELECT datname as "Database Name", 
       pg_size_pretty(pg_database_size(datname)) as "Size"
FROM pg_database 
WHERE datname IN (
    'platform_mgmt_db',
    'tenant_management_db', 
    'user_management_db',
    'permission_registry_db',
    'application_config_db',
    'authentication_db',
    'keycloak_db'
)
ORDER BY datname; 