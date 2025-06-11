-- Добавляем готовые сборки ПК разных категорий

-- Создаем тестового пользователя, если его еще нет
INSERT INTO users (email, name, username, password, active)
VALUES ('admin@pcmaker.com', 'Admin', 'admin', '$2a$10$uWp7nePLCS4nOCgR/Gw1bOeIGV3ffiBP6.A5E5Q3W.pUgcKAKY6Xe', true)
ON CONFLICT (username) DO NOTHING;

-- Получаем ID пользователя
DO $$
DECLARE
    admin_id BIGINT;
BEGIN
    SELECT id INTO admin_id FROM users WHERE username = 'admin';

    -- Игровая конфигурация высокого уровня
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Ultimate Gaming Rig',
        'Мощная игровая конфигурация для игр в 4K с максимальными настройками графики',
        true,
        349999.99,
        45000
    );

    -- Добавляем компоненты в игровую конфигурацию
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Intel Core i9-14900K'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'ASUS ROG Maximus Z790 Hero'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4090'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'G.Skill Trident Z5 RGB DDR5-7200 32GB'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Seasonic PRIME TX-1000'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Corsair 5000D Airflow'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'NZXT Kraken X73'), 1);

    -- Игровая конфигурация среднего уровня
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Mid-Range Gaming PC',
        'Сбалансированная игровая конфигурация для игр в 1440p с высокими настройками графики',
        true,
        199999.99,
        32000
    );

    -- Добавляем компоненты в среднюю игровую конфигурацию
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 7 7800X3D'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Gigabyte X670E AORUS Master'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4070 Ti Super'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Kingston FURY Beast DDR5-6000 32GB'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Western Digital Black SN850X 1TB'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Corsair RM850x'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'be quiet! Pure Base 500DX'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'be quiet! Dark Rock Pro 4'), 1);

    -- Бюджетная игровая конфигурация
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Budget Gaming PC',
        'Доступная игровая конфигурация для игр в 1080p с средними настройками графики',
        true,
        99999.99,
        22000
    );

    -- Добавляем компоненты в бюджетную игровую конфигурацию
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Intel Core i5-12400F'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'ASUS PRIME H610M-K D4'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 3050'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'G.Skill Ripjaws V DDR4-3200 16GB'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Kingston NV2 1TB'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'be quiet! Pure Power 11 FM 650W'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Deepcool MATREXX 55 MESH'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'ID-COOLING SE-224-XT'), 1);

    -- Компактная игровая конфигурация
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Compact Gaming PC',
        'Компактная мощная игровая конфигурация для небольших рабочих мест',
        true,
        159999.99,
        30000
    );

    -- Добавляем компоненты в компактную игровую конфигурацию
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 7 7800X3D'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Gigabyte B650 AORUS ELITE AX'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'AMD Radeon RX 6700 XT'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Kingston FURY Beast DDR5-6000 32GB'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Corsair RM650'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Cooler Master NR200P'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Noctua NH-L9i'), 1);

    -- Рабочая станция для дизайнеров и видеомонтажа
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Content Creator Workstation',
        'Мощная рабочая станция для дизайнеров, видеомонтажа и 3D-моделирования',
        true,
        299999.99,
        42000
    );

    -- Добавляем компоненты в рабочую станцию
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 9 7950X3D'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'ASUS ROG Crosshair X670E Hero'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4080 Super'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Corsair Vengeance RGB DDR5-6400 64GB'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 2),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Western Digital Blue 2TB'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'be quiet! Dark Power Pro 12 850W'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Phanteks Enthoo Pro 2'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Corsair iCUE H150i ELITE CAPELLIX'), 1);

    -- Офисный компьютер
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance)
    VALUES (
        admin_id,
        'Office PC',
        'Надежный компьютер для офисных задач и работы с документами',
        true,
        59999.99,
        15000
    );

    -- Добавляем компоненты в офисный компьютер
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Intel Core i3-12100'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'ASUS PRIME H610M-K D4'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Kingston FURY Beast DDR4-3200 16GB'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Western Digital Green 480GB'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Western Digital Blue 2TB'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'be quiet! Pure Power 11 FM 650W'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Deepcool MATREXX 55 MESH'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'ID-COOLING SE-224-XT'), 1);
END $$; 