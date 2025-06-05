-- Convert component_type column from PostgreSQL enum to varchar
ALTER TABLE products ALTER COLUMN component_type TYPE varchar USING component_type::varchar; 