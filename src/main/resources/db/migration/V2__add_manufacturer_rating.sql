-- Добавление поля rating в таблицу manufacturers
ALTER TABLE manufacturers ADD COLUMN rating DOUBLE PRECISION NOT NULL DEFAULT 0.0;

-- Обновление существующих записей в таблице manufacturers
UPDATE manufacturers m
SET rating = (
    SELECT COALESCE(AVG(r.rating), 0.0) 
    FROM products p
    JOIN reviews r ON p.id = r.product_id
    WHERE p.manufacturer_id = m.id
); 