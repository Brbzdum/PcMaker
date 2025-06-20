-- Добавляем обновленные готовые сборки ПК с типами сборок

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

    -- 1. Игровая конфигурация высокого уровня
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Ultimate Gaming Rig 4K',
        'Топовая игровая конфигурация для игр в 4K с максимальными настройками графики и трассировкой лучей',
        true,
        379999.94,
        62000,
        'GAMING',
        true
    );

    -- Добавляем компоненты в топовую игровую конфигурацию
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig 4K' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Intel Core i9-14900K'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'ASUS ROG Maximus Z790 Hero'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4090'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'G.Skill Trident Z5 RGB DDR5-7200 32GB'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Seasonic Focus GX-1000'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'Corsair 5000D Airflow'), 1),
        ((SELECT id FROM high_end_config), (SELECT id FROM products WHERE title = 'NZXT Kraken X73'), 1);

    -- 2. Игровая конфигурация среднего уровня AMD
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'AMD Gaming Beast 1440p',
        'Мощная игровая конфигурация на базе AMD для игр в 1440p с высокими настройками',
        true,
        219999.93,
        52000,
        'GAMING',
        true
    );

    -- Добавляем компоненты в среднюю игровую конфигурацию AMD
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'AMD Gaming Beast 1440p' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 9 7950X3D'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'ASUS ROG Crosshair X670E Hero'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'AMD Radeon RX 7900 XTX'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Corsair Dominator Platinum RGB DDR5-6000 32GB'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Western Digital Black SN850X 1TB'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Corsair RM850x'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'be quiet! Pure Base 500DX'), 1),
        ((SELECT id FROM mid_range_config), (SELECT id FROM products WHERE title = 'Corsair iCUE H150i ELITE CAPELLIX'), 1);

    -- 3. Бюджетная игровая конфигурация
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Budget Gamer 1080p',
        'Доступная игровая конфигурация для комфортных игр в Full HD разрешении',
        true,
        119999.93,
        25000,
        'GAMING',
        true
    );

    -- Добавляем компоненты в бюджетную игровую конфигурацию
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gamer 1080p' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Intel Core i5-12400F'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'MSI MAG B760M Mortar WiFi'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4060'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Kingston FURY Beast DDR5-6000 32GB'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Kingston NV2 1TB'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'EVGA SuperNOVA 650 G5'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Cooler Master MasterBox TD500 Mesh'), 1),
        ((SELECT id FROM budget_config), (SELECT id FROM products WHERE title = 'Cooler Master Hyper 212 RGB Black Edition'), 1);

    -- 4. Компактная игровая конфигурация Mini-ITX
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Compact Gaming Mini-ITX',
        'Компактная мощная игровая конфигурация в формате Mini-ITX для небольших пространств',
        true,
        179999.93,
        39000,
        'GAMING',
        true
    );

    -- Добавляем компоненты в компактную игровую конфигурацию
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming Mini-ITX' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 7 7800X3D'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Gigabyte B650 AORUS ELITE AX'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'AMD Radeon RX 7800 XT'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'G.Skill Trident Z5 RGB DDR5-7200 32GB'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'Thermaltake Toughpower GF1 550W'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'NZXT H210i'), 1),
        ((SELECT id FROM compact_config), (SELECT id FROM products WHERE title = 'NZXT Kraken X63'), 1);

    -- 5. Рабочая станция для контент-креаторов
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Content Creator Workstation Pro',
        'Профессиональная рабочая станция для видеомонтажа, 3D-рендеринга и дизайна',
        true,
        309999.94,
        54000,
        'PROFESSIONAL',
        true
    );

    -- Добавляем компоненты в рабочую станцию
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation Pro' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 9 7900X'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'MSI MEG X670E ACE'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4080 Super'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Corsair Dominator Platinum RGB DDR5-6000 32GB'), 2),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Western Digital Black SN850X 1TB'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'be quiet! Straight Power 11 750W'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'Corsair 5000D Airflow'), 1),
        ((SELECT id FROM workstation_config), (SELECT id FROM products WHERE title = 'be quiet! Dark Rock Pro 4'), 1);

    -- 6. Офисный компьютер
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Office Professional',
        'Надежный и энергоэффективный компьютер для офисных задач и работы с документами',
        true,
        59999.93,
        18000,
        'OFFICE',
        true
    );

    -- Добавляем компоненты в офисный компьютер
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office Professional' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Intel Core i3-12100'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'ASUS PRIME H610M-K D4'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'G.Skill Ripjaws V DDR4-3200 16GB'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Western Digital Green 480GB'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Cooler Master MWE Gold 500W'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Cooler Master MasterBox TD500 Mesh'), 1),
        ((SELECT id FROM office_config), (SELECT id FROM products WHERE title = 'Cooler Master Hyper 212 RGB Black Edition'), 1);

    -- 7. Мультимедийная станция AMD
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'AMD Multimedia Center',
        'Универсальная конфигурация для домашних развлечений и мультимедийных задач',
        true,
        89999.93,
        23000,
        'MULTIMEDIA',
        true
    );

    -- Добавляем компоненты в мультимедийную станцию
    WITH multimedia_config AS (SELECT id FROM pc_configurations WHERE name = 'AMD Multimedia Center' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'AMD Ryzen 5 5600G'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'ASRock X570 Steel Legend'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'Corsair Vengeance RGB Pro DDR4-3600 32GB'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'Crucial P5 Plus 1TB'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'Thermaltake Toughpower GF1 550W'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'be quiet! Pure Base 500DX'), 1),
        ((SELECT id FROM multimedia_config), (SELECT id FROM products WHERE title = 'Thermaltake TH240 ARGB'), 1);

    -- 8. Экстремальная игровая конфигурация с периферией
    INSERT INTO pc_configurations (user_id, name, description, is_compatible, total_price, total_performance, category, is_public)
    VALUES (
        admin_id,
        'Extreme Gaming Setup Full',
        'Полная игровая экосистема с топовыми компонентами и премиальной периферией',
        true,
        499999.81,
        62000,
        'GAMING',
        true
    );

    -- Добавляем компоненты в экстремальную игровую конфигурацию
    WITH extreme_config AS (SELECT id FROM pc_configurations WHERE name = 'Extreme Gaming Setup Full' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    VALUES
        -- Основные компоненты
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Intel Core i9-14900K'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'ASUS ROG Maximus Z790 Hero'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'NVIDIA GeForce RTX 4090'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'G.Skill Trident Z5 RGB DDR5-7200 32GB'), 2),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Samsung 990 PRO 2TB'), 2),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Seasonic Focus GX-1000'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'NZXT H7 Flow'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'NZXT Kraken X73'), 1),
        -- Периферия
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'LG 27GP950-B'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Logitech G915 TKL'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Logitech G Pro X Superlight'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'SteelSeries Arctis Pro Wireless'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'Razer Goliathus Extended Chroma'), 1),
        ((SELECT id FROM extreme_config), (SELECT id FROM products WHERE title = 'HyperX QuadCast S'), 1);

END $$; 