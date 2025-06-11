-- Добавляем корпуса
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NZXT H7 Flow', 'Корпус NZXT H7 Flow, ATX Mid Tower, с отличной вентиляцией', 12999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "480x230x505", "weight": "12.8", "material": "Steel, Tempered Glass", "max_gpu_length": "400", "max_cpu_cooler_height": "185", "max_psu_length": "200", "included_fans": "2", "performance": "90"}', 
true),

('Corsair 5000D Airflow', 'Корпус Corsair 5000D Airflow, ATX Mid Tower, оптимизированный для охлаждения', 14999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "520x245x520", "weight": "13.8", "material": "Steel, Tempered Glass", "max_gpu_length": "420", "max_cpu_cooler_height": "170", "max_psu_length": "225", "included_fans": "3", "performance": "92"}', 
true),

('be quiet! Pure Base 500DX', 'Корпус be quiet! Pure Base 500DX, ATX Mid Tower, с RGB подсветкой', 9999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "463x232x463", "weight": "8.2", "material": "Steel, Tempered Glass", "max_gpu_length": "369", "max_cpu_cooler_height": "190", "max_psu_length": "225", "included_fans": "3", "performance": "85"}', 
true),

('Thermaltake Core P3', 'Корпус Thermaltake Core P3, ATX Open Frame, с панорамным обзором компонентов', 11999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Open Frame", "dimensions": "512x333x470", "weight": "10.9", "material": "Steel, Tempered Glass", "max_gpu_length": "450", "max_cpu_cooler_height": "180", "max_psu_length": "220", "included_fans": "0", "performance": "75"}', 
true),

('Cooler Master MasterBox TD500 Mesh', 'Корпус Cooler Master MasterBox TD500 Mesh, ATX Mid Tower, с передней сеткой для вентиляции', 7999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "493x217x468", "weight": "7.6", "material": "Steel, Tempered Glass", "max_gpu_length": "410", "max_cpu_cooler_height": "165", "max_psu_length": "180", "included_fans": "3", "performance": "80"}', 
true),

('NZXT H210i', 'Корпус NZXT H210i, Mini-ITX, компактный и стильный', 8499.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "Mini-ITX", "type": "Mini Tower", "dimensions": "349x210x372", "weight": "6.0", "material": "Steel, Tempered Glass", "max_gpu_length": "325", "max_cpu_cooler_height": "165", "max_psu_length": "210", "included_fans": "2", "performance": "70"}', 
true);

-- Добавляем системы охлаждения
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NZXT Kraken X73', 'Система жидкостного охлаждения NZXT Kraken X73, 360 мм радиатор', 16999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "360", "fan_size": "120", "fan_count": "3", "max_tdp": "350", "rgb": "Yes", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "21-36", "performance": "300"}', 
true),

('Corsair iCUE H150i ELITE CAPELLIX', 'Система жидкостного охлаждения Corsair iCUE H150i ELITE CAPELLIX, 360 мм радиатор', 17999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "360", "fan_size": "120", "fan_count": "3", "max_tdp": "380", "rgb": "Yes", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "10-36", "performance": "310"}', 
true),

('be quiet! Dark Rock Pro 4', 'Воздушный кулер be quiet! Dark Rock Pro 4, двойной вентилятор', 8999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "162.8", "fan_size": "120,135", "fan_count": "2", "max_tdp": "250", "rgb": "No", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "12-24", "performance": "250"}', 
true),

('Cooler Master Hyper 212 RGB Black Edition', 'Воздушный кулер Cooler Master Hyper 212 RGB Black Edition', 4999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "159", "fan_size": "120", "fan_count": "1", "max_tdp": "150", "rgb": "Yes", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "8-27", "performance": "150"}', 
true),

('NZXT Kraken X63', 'Система жидкостного охлаждения NZXT Kraken X63, 280 мм радиатор', 13999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "280", "fan_size": "140", "fan_count": "2", "max_tdp": "300", "rgb": "Yes", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "21-36", "performance": "280"}', 
true),

('Thermaltake TH240 ARGB', 'Система жидкостного охлаждения Thermaltake TH240 ARGB, 240 мм радиатор', 8999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "240", "fan_size": "120", "fan_count": "2", "max_tdp": "250", "rgb": "Yes", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "14-28", "performance": "250"}', 
true); 