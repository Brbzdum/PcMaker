-- Сначала удаляем значения по умолчанию, которые зависят от типа order_status
ALTER TABLE orders ALTER COLUMN status DROP DEFAULT;
ALTER TABLE order_status_history ALTER COLUMN status DROP DEFAULT;

-- Затем меняем тип столбцов
ALTER TABLE order_status_history ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(50);

-- Удаляем тип order_status, если он больше не используется
DROP TYPE IF EXISTS order_status; 