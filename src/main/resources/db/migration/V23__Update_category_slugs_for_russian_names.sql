-- Обновление slug'ов категорий с русскими названиями

-- Наушники - используем slug 'headphones'
UPDATE categories SET slug = 'headphones' WHERE name = 'Наушники' AND slug != 'headphones';

-- Коврики для мыши
UPDATE categories SET slug = 'mousepads' WHERE name = 'Коврики для мыши' AND slug != 'mousepads';

-- Микрофоны
UPDATE categories SET slug = 'microphones' WHERE name = 'Микрофоны' AND slug != 'microphones';

-- Игровые контроллеры
UPDATE categories SET slug = 'gamepads' WHERE name = 'Игровые контроллеры' AND slug != 'gamepads';

-- Сетевое оборудование
UPDATE categories SET slug = 'network' WHERE name = 'Сетевое оборудование' AND slug != 'network';

-- Обновляем другие категории с русскими названиями, если они есть
-- Здесь можно добавить другие категории, если потребуется 