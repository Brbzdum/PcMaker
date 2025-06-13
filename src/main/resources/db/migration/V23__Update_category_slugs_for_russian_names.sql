-- Обновление slug'ов категорий с русскими названиями

-- Наушники (ID 49) - используем другой slug, так как 'headsets' уже занят (ID 41)
UPDATE categories SET slug = 'headphones' WHERE id = 49;

-- Коврики для мыши (ID 50)
UPDATE categories SET slug = 'mousepads' WHERE id = 50;

-- Микрофоны (ID 51)
UPDATE categories SET slug = 'microphones' WHERE id = 51;

-- Обновляем другие категории с русскими названиями, если они есть
-- Здесь можно добавить другие категории, если потребуется 