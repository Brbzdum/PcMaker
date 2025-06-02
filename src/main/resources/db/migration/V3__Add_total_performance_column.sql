-- Добавление столбца total_performance в таблицу pc_configurations
ALTER TABLE pc_configurations ADD COLUMN IF NOT EXISTS total_performance DOUBLE PRECISION DEFAULT 0.0; 