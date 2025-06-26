# Flyway Migration Guide

This guide explains how to use Flyway database migrations in the Onified platform microservices.

## Overview

Flyway is a database migration tool that helps manage database schema changes in a version-controlled manner. All Spring Boot services in the Onified platform now use Flyway for database schema management.

## Configuration

### Dependencies

All services include the Flyway dependency in their `pom.xml`:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### Application Configuration

Each service has Flyway configured in `application.yml`:

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    validate-on-migrate: true
    locations: classpath:db/migration
    table: flyway_schema_history
    baseline-version: 0
    baseline-description: Initial baseline
  jpa:
    hibernate:
      ddl-auto: validate  # Changed from 'update' to 'validate'
```

## Migration File Structure

Migrations are stored in `src/main/resources/db/migration/` with the following naming convention:

```
V{version}__{description}.sql
```

Examples:
- `V1__Create_auditable_table.sql`
- `V2__Create_password_policy_table.sql`
- `V3__Add_user_preferences.sql`

## Current Migrations

### Platform Management Service
- `V1__Create_auditable_table.sql` - Creates base auditable table and update trigger
- `V2__Create_password_policy_table.sql` - Creates password policy table with indexes and default data

### Tenant Management Service
- `V1__Create_tenant_config_table.sql` - Creates tenant configuration and app subscriptions tables

### User Management Service
- `V1__Create_users_table.sql` - Creates users table with audit fields

### Authentication Service
- `V1__Create_auth_tables.sql` - Creates authentication sessions table

### Permission Registry Service
- `V1__Create_permissions_tables.sql` - Creates permissions, roles, and role_permissions tables

### Application Config Service
- `V1__Create_app_config_tables.sql` - Creates application configuration table

## Creating New Migrations

### 1. Create Migration File

Create a new SQL file in the appropriate service's migration directory:

```sql
-- V3__Add_new_feature.sql
CREATE TABLE new_feature (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Migration Best Practices

- **Version Numbers**: Use sequential version numbers (V1, V2, V3, etc.)
- **Descriptive Names**: Use clear, descriptive names for the migration
- **Idempotent**: Ensure migrations can be run multiple times safely
- **Rollback Consideration**: Design migrations that can be rolled back if needed
- **Data Migration**: Include data migration scripts when changing existing data

### 3. Common Migration Patterns

#### Adding a New Table
```sql
CREATE TABLE new_table (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_new_table_name ON new_table(name);

CREATE TRIGGER update_new_table_updated_at
    BEFORE UPDATE ON new_table
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

#### Adding a Column
```sql
ALTER TABLE existing_table 
ADD COLUMN new_column VARCHAR(255) DEFAULT 'default_value';
```

#### Modifying a Column
```sql
ALTER TABLE existing_table 
ALTER COLUMN existing_column TYPE VARCHAR(500);
```

#### Adding Foreign Key
```sql
ALTER TABLE child_table 
ADD CONSTRAINT fk_child_parent 
FOREIGN KEY (parent_id) REFERENCES parent_table(id);
```

## Running Migrations

### Automatic Migration

Migrations run automatically when the Spring Boot application starts. Flyway will:

1. Check the `flyway_schema_history` table
2. Apply any pending migrations in version order
3. Update the schema history table

### Manual Migration Commands

You can also run migrations manually using Maven:

```bash
# Clean and migrate
mvn flyway:clean flyway:migrate

# Migrate only
mvn flyway:migrate

# Validate current state
mvn flyway:validate

# Show migration info
mvn flyway:info
```

### Database-Specific Commands

```bash
# For a specific service
cd platform-management-service
mvn flyway:migrate

# For all services
./build-all.sh
```

## Migration States

### Pending
Migrations that haven't been applied yet.

### Applied
Migrations that have been successfully applied.

### Failed
Migrations that failed during execution.

### Out of Order
Migrations applied in the wrong order (should be avoided).

## Troubleshooting

### Common Issues

1. **Migration Already Applied**
   - Error: "Migration V1__Create_table.sql has already been applied"
   - Solution: Check `flyway_schema_history` table or use `flyway:info`

2. **Migration Failed**
   - Check application logs for SQL errors
   - Fix the migration script and restart the application
   - Consider using `flyway:repair` if needed

3. **Version Conflicts**
   - Ensure migration versions are sequential
   - Don't reuse version numbers

### Recovery Steps

1. **Check Migration Status**
   ```bash
   mvn flyway:info
   ```

2. **Repair Corrupted State**
   ```bash
   mvn flyway:repair
   ```

3. **Clean and Restart** (Development only)
   ```bash
   mvn flyway:clean flyway:migrate
   ```

## Production Considerations

### Before Deployment

1. **Test Migrations**: Always test migrations in a staging environment
2. **Backup Database**: Create database backup before applying migrations
3. **Review Scripts**: Ensure migrations are production-ready
4. **Downtime Planning**: Plan for potential downtime during migration

### During Deployment

1. **Monitor Logs**: Watch application logs for migration progress
2. **Verify Success**: Check that all migrations applied successfully
3. **Rollback Plan**: Have a rollback strategy ready

### After Deployment

1. **Verify Data**: Ensure data integrity after migration
2. **Performance Check**: Monitor application performance
3. **Cleanup**: Remove any temporary migration artifacts

## Best Practices

### Development

1. **Version Control**: Always commit migration files to version control
2. **Testing**: Test migrations with real data
3. **Documentation**: Document complex migrations
4. **Review**: Have migrations reviewed by team members

### Naming Conventions

1. **File Names**: Use descriptive, lowercase names with underscores
2. **Table Names**: Use snake_case for table and column names
3. **Index Names**: Prefix with `idx_` followed by table and column names
4. **Constraint Names**: Use descriptive constraint names

### SQL Standards

1. **Consistency**: Use consistent SQL formatting
2. **Comments**: Add comments for complex migrations
3. **Error Handling**: Consider error scenarios
4. **Performance**: Optimize for performance where possible

## Migration Tools

### IDE Integration

- **IntelliJ IDEA**: Built-in Flyway support
- **Eclipse**: Flyway plugin available
- **VS Code**: Flyway extension available

### Command Line Tools

- **Flyway CLI**: Standalone command-line tool
- **Maven Plugin**: Integrated with Maven build process
- **Gradle Plugin**: Available for Gradle projects

## Monitoring and Logging

### Migration Logs

Flyway logs migration activities to the application log:

```
INFO  - Flyway Community Edition 9.22.3 by Redgate
INFO  - Database: jdbc:postgresql://localhost:5432/platform_mgmt_db (PostgreSQL 15.1)
INFO  - Successfully validated 2 migrations (execution time 00:00.012s)
INFO  - Creating Schema History table "public"."flyway_schema_history" ...
INFO  - Current version of schema "public": << Empty Schema >>
INFO  - Migrating schema "public" to version "1 - Create auditable table"
INFO  - Migrating schema "public" to version "2 - Create password policy table"
INFO  - Successfully applied 2 migrations to schema "public" (execution time 00:00.045s)
```

### Health Checks

Flyway provides health check endpoints:

```bash
# Check migration health
curl http://localhost:9081/actuator/health

# Get detailed health info
curl http://localhost:9081/actuator/health/flyway
```

## Conclusion

Flyway provides a robust, version-controlled approach to database schema management. By following these guidelines, you can ensure smooth database migrations across all Onified platform services.

For more information, refer to the [official Flyway documentation](https://flywaydb.org/documentation/). 