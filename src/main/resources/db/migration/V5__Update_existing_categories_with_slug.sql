-- Обновление существующих категорий, добавляя им slug на основе имени
-- Получаем все категории без slug
UPDATE categories 
SET slug = LOWER(REGEXP_REPLACE(name, '[^a-zA-Z0-9]', '-', 'g'))
WHERE slug IS NULL;

-- Удаляем повторяющиеся дефисы
UPDATE categories 
SET slug = REGEXP_REPLACE(slug, '-{2,}', '-', 'g');

-- Удаляем дефисы в начале и конце
UPDATE categories 
SET slug = TRIM(BOTH '-' FROM slug);

-- Обрабатываем дубликаты slug по одному
DO $$
DECLARE
    duplicate_slug text;
    slug_count integer;
    current_id bigint;
BEGIN
    -- Находим дубликаты
    FOR duplicate_slug IN
        SELECT slug
        FROM categories
        GROUP BY slug
        HAVING COUNT(*) > 1
    LOOP
        -- Для каждого дубликата slug обрабатываем все записи, кроме первой
        slug_count := 1;
        FOR current_id IN
            SELECT id
            FROM categories
            WHERE slug = duplicate_slug
            ORDER BY id
            OFFSET 1 -- Пропускаем первую запись
        LOOP
            -- Обновляем slug с числовым суффиксом
            UPDATE categories
            SET slug = duplicate_slug || '-' || slug_count
            WHERE id = current_id;
            
            slug_count := slug_count + 1;
        END LOOP;
    END LOOP;
END $$; 