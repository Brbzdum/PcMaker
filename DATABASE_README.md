# Database Initialization Guide

## Overview

The database schema initialization has been updated to ensure that the database structure is created before any data is inserted. This is done using Flyway migrations.

## Key Changes

1. Added a new migration file `V0__Init_schema.sql` that creates all database tables, indexes, functions, and triggers.
2. Enabled Flyway migrations in `application.properties` by setting `spring.flyway.enabled=true`.
3. Updated the application configuration to ensure Flyway is properly initialized before any other components.

## How It Works

1. When the application starts, Flyway will run all migrations in order, starting with `V0__Init_schema.sql`.
2. The schema will be created first, followed by role initialization from `V1__Init_roles.sql`.
3. All subsequent migrations will run in order.
4. After all migrations are complete, the `DataInitializer` will run to create any additional required data.

## Running the Application

1. Make sure PostgreSQL is running and the database `computer_store` is created.
2. Configure database connection settings in `application.properties` if needed.
3. Run the application using Maven:

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

## Troubleshooting

If you encounter any issues with the database initialization:

1. Check the application logs for error messages.
2. Verify that the database exists and is accessible with the configured credentials.
3. If there are migration errors, you may need to repair the Flyway schema:

```bash
./mvnw flyway:repair
```

4. In case of persistent issues, you can clean the database and start fresh:

```bash
./mvnw flyway:clean
```

**Warning:** Using `flyway:clean` will delete all data in the database! 