-- Исправляем связи между конфигурациями и компонентами, используя существующие продукты

-- Сначала очищаем существующие компоненты конфигураций
DELETE FROM config_components;

-- Получаем ID пользователя admin
DO $$
DECLARE
    admin_id BIGINT;
    
    -- Переменные для хранения ID продуктов
    cpu_intel_id BIGINT;
    cpu_amd_id BIGINT;
    gpu_nvidia_id BIGINT;
    gpu_amd_id BIGINT;
    mb_intel_id BIGINT;
    mb_amd_id BIGINT;
    ram_id BIGINT;
    ssd_id BIGINT;
    hdd_id BIGINT;
    psu_high_id BIGINT;
    psu_mid_id BIGINT;
    case_id BIGINT;
    cooler_liquid_id BIGINT;
    cooler_air_id BIGINT;
BEGIN
    -- Получаем ID пользователя admin
    SELECT id INTO admin_id FROM users WHERE username = 'admin';
    
    -- Если admin не существует, создаем его
    IF admin_id IS NULL THEN
        INSERT INTO users (email, name, username, password, active)
        VALUES ('admin@pcmaker.com', 'Admin', 'admin', '$2a$10$uWp7nePLCS4nOCgR/Gw1bOeIGV3ffiBP6.A5E5Q3W.pUgcKAKY6Xe', true)
        RETURNING id INTO admin_id;
    END IF;
    
    -- Получаем ID существующих продуктов
    -- Процессоры
    SELECT id INTO cpu_intel_id FROM products WHERE title = 'Intel Core i9-14900K' OR title = 'Intel Core i9-13900K' LIMIT 1;
    SELECT id INTO cpu_amd_id FROM products WHERE title = 'AMD Ryzen 9 7950X3D' OR title = 'AMD Ryzen 7 7800X3D' LIMIT 1;
    
    -- Видеокарты
    SELECT id INTO gpu_nvidia_id FROM products WHERE title = 'NVIDIA GeForce RTX 4090' OR title = 'NVIDIA GeForce RTX 4080 Super' LIMIT 1;
    SELECT id INTO gpu_amd_id FROM products WHERE title = 'AMD Radeon RX 7900 XTX' LIMIT 1;
    
    -- Материнские платы
    SELECT id INTO mb_intel_id FROM products WHERE title = 'ASUS ROG Maximus Z790 Hero' OR title LIKE '%Z790%' LIMIT 1;
    SELECT id INTO mb_amd_id FROM products WHERE title = 'ASUS ROG Crosshair X670E Hero' OR title LIKE '%X670E%' LIMIT 1;
    
    -- Оперативная память
    SELECT id INTO ram_id FROM products WHERE title LIKE '%DDR5%' AND component_type = 'RAM' LIMIT 1;
    
    -- Накопители
    SELECT id INTO ssd_id FROM products WHERE title = 'Samsung 990 PRO 2TB' OR title LIKE '%SSD%' AND component_type = 'STORAGE' LIMIT 1;
    SELECT id INTO hdd_id FROM products WHERE title LIKE '%HDD%' OR title LIKE '%Western Digital Blue%' AND component_type = 'STORAGE' LIMIT 1;
    
    -- Блоки питания
    SELECT id INTO psu_high_id FROM products WHERE title = 'Seasonic PRIME TX-1000' OR (title LIKE '%850%' AND component_type = 'PSU') LIMIT 1;
    SELECT id INTO psu_mid_id FROM products WHERE title = 'Corsair RM650' OR (title LIKE '%650%' AND component_type = 'PSU') LIMIT 1;
    
    -- Корпусы
    SELECT id INTO case_id FROM products WHERE title = 'Corsair 5000D Airflow' OR component_type = 'CASE' LIMIT 1;
    
    -- Охлаждение
    SELECT id INTO cooler_liquid_id FROM products WHERE title LIKE '%Kraken%' OR (title LIKE '%Liquid%' AND component_type = 'COOLER') LIMIT 1;
    SELECT id INTO cooler_air_id FROM products WHERE title = 'be quiet! Dark Rock Pro 4' OR (title LIKE '%Air%' AND component_type = 'COOLER') LIMIT 1;
    
    -- Проверяем, что у нас есть хотя бы по одному компоненту каждого типа
    IF cpu_intel_id IS NULL THEN
        SELECT id INTO cpu_intel_id FROM products WHERE component_type = 'CPU' LIMIT 1;
    END IF;
    
    IF gpu_nvidia_id IS NULL THEN
        SELECT id INTO gpu_nvidia_id FROM products WHERE component_type = 'GPU' LIMIT 1;
    END IF;
    
    IF mb_intel_id IS NULL THEN
        SELECT id INTO mb_intel_id FROM products WHERE component_type = 'MB' LIMIT 1;
    END IF;
    
    IF ram_id IS NULL THEN
        SELECT id INTO ram_id FROM products WHERE component_type = 'RAM' LIMIT 1;
    END IF;
    
    IF ssd_id IS NULL THEN
        SELECT id INTO ssd_id FROM products WHERE component_type = 'STORAGE' LIMIT 1;
    END IF;
    
    IF psu_high_id IS NULL THEN
        SELECT id INTO psu_high_id FROM products WHERE component_type = 'PSU' LIMIT 1;
    END IF;
    
    IF case_id IS NULL THEN
        SELECT id INTO case_id FROM products WHERE component_type = 'CASE' LIMIT 1;
    END IF;
    
    IF cooler_liquid_id IS NULL THEN
        SELECT id INTO cooler_liquid_id FROM products WHERE component_type = 'COOLER' LIMIT 1;
    END IF;
    
    -- Добавляем компоненты к существующим конфигурациям
    -- Ultimate Gaming Rig
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_intel_id, 1 FROM high_end_config WHERE cpu_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, gpu_nvidia_id, 1 FROM high_end_config WHERE gpu_nvidia_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_intel_id, 1 FROM high_end_config WHERE mb_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM high_end_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM high_end_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_high_id, 1 FROM high_end_config WHERE psu_high_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM high_end_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH high_end_config AS (SELECT id FROM pc_configurations WHERE name = 'Ultimate Gaming Rig' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_liquid_id, 1 FROM high_end_config WHERE cooler_liquid_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    -- Mid-Range Gaming PC
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_amd_id, 1 FROM mid_range_config WHERE cpu_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, gpu_nvidia_id, 1 FROM mid_range_config WHERE gpu_nvidia_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_amd_id, 1 FROM mid_range_config WHERE mb_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM mid_range_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM mid_range_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_high_id, 1 FROM mid_range_config WHERE psu_high_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM mid_range_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH mid_range_config AS (SELECT id FROM pc_configurations WHERE name = 'Mid-Range Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_air_id, 1 FROM mid_range_config WHERE cooler_air_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    -- Budget Gaming PC
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_intel_id, 1 FROM budget_config WHERE cpu_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, gpu_nvidia_id, 1 FROM budget_config WHERE gpu_nvidia_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_intel_id, 1 FROM budget_config WHERE mb_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM budget_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM budget_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_mid_id, 1 FROM budget_config WHERE psu_mid_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM budget_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH budget_config AS (SELECT id FROM pc_configurations WHERE name = 'Budget Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_air_id, 1 FROM budget_config WHERE cooler_air_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    -- Compact Gaming PC
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_amd_id, 1 FROM compact_config WHERE cpu_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, gpu_amd_id, 1 FROM compact_config WHERE gpu_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_amd_id, 1 FROM compact_config WHERE mb_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM compact_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM compact_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_mid_id, 1 FROM compact_config WHERE psu_mid_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM compact_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH compact_config AS (SELECT id FROM pc_configurations WHERE name = 'Compact Gaming PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_air_id, 1 FROM compact_config WHERE cooler_air_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    -- Content Creator Workstation
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_amd_id, 1 FROM workstation_config WHERE cpu_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, gpu_nvidia_id, 1 FROM workstation_config WHERE gpu_nvidia_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_amd_id, 1 FROM workstation_config WHERE mb_amd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM workstation_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM workstation_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, hdd_id, 1 FROM workstation_config WHERE hdd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_high_id, 1 FROM workstation_config WHERE psu_high_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM workstation_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH workstation_config AS (SELECT id FROM pc_configurations WHERE name = 'Content Creator Workstation' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_liquid_id, 1 FROM workstation_config WHERE cooler_liquid_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    -- Office PC
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cpu_intel_id, 1 FROM office_config WHERE cpu_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, mb_intel_id, 1 FROM office_config WHERE mb_intel_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ram_id, 1 FROM office_config WHERE ram_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, ssd_id, 1 FROM office_config WHERE ssd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, hdd_id, 1 FROM office_config WHERE hdd_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, psu_mid_id, 1 FROM office_config WHERE psu_mid_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, case_id, 1 FROM office_config WHERE case_id IS NOT NULL
    ON CONFLICT DO NOTHING;
    
    WITH office_config AS (SELECT id FROM pc_configurations WHERE name = 'Office PC' AND user_id = admin_id)
    INSERT INTO config_components (config_id, product_id, quantity)
    SELECT id, cooler_air_id, 1 FROM office_config WHERE cooler_air_id IS NOT NULL
    ON CONFLICT DO NOTHING;
END $$; 