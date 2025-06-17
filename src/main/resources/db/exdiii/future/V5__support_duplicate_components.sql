-- Миграция для поддержки дублирующихся компонентов в конфигурации

-- 1. Создаем временную таблицу для хранения текущих данных
CREATE TABLE temp_config_components AS
SELECT * FROM config_components;

-- 2. Удаляем существующую таблицу
DROP TABLE config_components;

-- 3. Создаем новую таблицу с автоинкрементным ID
CREATE TABLE config_components (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL REFERENCES pc_configurations(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Создаем уникальный индекс для предотвращения дублирования компонентов одного типа
-- Этот индекс можно будет удалить, когда вы решите полностью поддерживать дубликаты
CREATE UNIQUE INDEX idx_unique_component_type_per_config ON config_components (config_id, (
    SELECT component_type FROM products WHERE id = product_id
));

-- 5. Восстанавливаем данные
INSERT INTO config_components (config_id, product_id, quantity, created_at, updated_at)
SELECT config_id, product_id, quantity, created_at, updated_at
FROM temp_config_components;

-- 6. Удаляем временную таблицу
DROP TABLE temp_config_components;

-- 7. Создаем индексы для оптимизации запросов
CREATE INDEX idx_config_components_config ON config_components(config_id);
CREATE INDEX idx_config_components_product ON config_components(product_id);

-- 8. Создаем триггер для обновления timestamp
CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON config_components
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Комментарий: когда вы будете готовы полностью поддерживать дубликаты (например, несколько плашек RAM),
-- вам нужно будет удалить уникальный индекс idx_unique_component_type_per_config
-- с помощью команды: DROP INDEX idx_unique_component_type_per_config; 