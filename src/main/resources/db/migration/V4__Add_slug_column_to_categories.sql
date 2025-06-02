-- Добавление столбца slug в таблицу categories
ALTER TABLE categories ADD COLUMN IF NOT EXISTS slug VARCHAR(255) UNIQUE; 