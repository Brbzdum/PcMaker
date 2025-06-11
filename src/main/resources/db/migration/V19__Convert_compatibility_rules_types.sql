-- Convert source_type and target_type columns from PostgreSQL enum to varchar in compatibility_rules table
ALTER TABLE compatibility_rules ALTER COLUMN source_type TYPE varchar USING source_type::varchar;
ALTER TABLE compatibility_rules ALTER COLUMN target_type TYPE varchar USING target_type::varchar; 