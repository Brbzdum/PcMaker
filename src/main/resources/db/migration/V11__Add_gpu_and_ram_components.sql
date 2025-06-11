-- Добавляем видеокарты NVIDIA
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NVIDIA GeForce RTX 4090', 'Видеокарта NVIDIA GeForce RTX 4090, 24 ГБ GDDR6X, высочайшая производительность для игр и рендеринга', 159999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "24", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "2235", "boost_clock": "2520", "length": "336", "width": "140", "power": "450", "recommended_psu": "850", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 16-pin", "performance": "62000"}', 
true),

('NVIDIA GeForce RTX 4080 Super', 'Видеокарта NVIDIA GeForce RTX 4080 Super, 16 ГБ GDDR6X, высокая производительность для требовательных игр', 119999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "16", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "2205", "boost_clock": "2505", "length": "304", "width": "137", "power": "320", "recommended_psu": "750", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 16-pin", "performance": "54000"}', 
true),

('NVIDIA GeForce RTX 4070 Ti Super', 'Видеокарта NVIDIA GeForce RTX 4070 Ti Super, 16 ГБ GDDR6X, оптимальное сочетание цены и производительности', 89999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "16", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "2310", "boost_clock": "2610", "length": "285", "width": "125", "power": "285", "recommended_psu": "700", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 16-pin", "performance": "45000"}', 
true),

('NVIDIA GeForce RTX 4070', 'Видеокарта NVIDIA GeForce RTX 4070, 12 ГБ GDDR6X, отличный выбор для игр в разрешении 1440p', 65999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "12", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "1920", "boost_clock": "2475", "length": "244", "width": "112", "power": "200", "recommended_psu": "650", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 8-pin, 1x 6-pin", "performance": "35000"}', 
true);

-- Добавляем видеокарты AMD
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('AMD Radeon RX 7900 XTX', 'Видеокарта AMD Radeon RX 7900 XTX, 24 ГБ GDDR6, флагманская модель с высокой производительностью', 129999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "24", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1855", "boost_clock": "2499", "length": "287", "width": "123", "power": "355", "recommended_psu": "800", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "52000"}', 
true),

('AMD Radeon RX 7900 XT', 'Видеокарта AMD Radeon RX 7900 XT, 20 ГБ GDDR6, мощная модель для требовательных игр', 99999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "20", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1500", "boost_clock": "2394", "length": "276", "width": "123", "power": "300", "recommended_psu": "750", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "47000"}', 
true),

('AMD Radeon RX 7800 XT', 'Видеокарта AMD Radeon RX 7800 XT, 16 ГБ GDDR6, оптимальный выбор для игр в высоком разрешении', 64999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "16", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1295", "boost_clock": "2124", "length": "267", "width": "120", "power": "263", "recommended_psu": "700", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "39000"}', 
true),

('AMD Radeon RX 7700 XT', 'Видеокарта AMD Radeon RX 7700 XT, 12 ГБ GDDR6, хороший выбор для игр в разрешении 1440p', 49999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "12", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1220", "boost_clock": "2171", "length": "251", "width": "116", "power": "245", "recommended_psu": "650", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "32000"}', 
true);

-- Добавляем оперативную память (DDR5)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Corsair Dominator Platinum RGB DDR5-6000 32GB', 'Модуль памяти Corsair Dominator Platinum RGB, DDR5-6000, 32 ГБ (2x16 ГБ)', 29999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "6000", "timings": "36-36-36-76", "voltage": "1.35", "rgb": "Yes", "performance": "12000"}', 
true),

('G.Skill Trident Z5 RGB DDR5-7200 32GB', 'Модуль памяти G.Skill Trident Z5 RGB, DDR5-7200, 32 ГБ (2x16 ГБ)', 32999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'G.Skill'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "7200", "timings": "34-42-42-108", "voltage": "1.4", "rgb": "Yes", "performance": "14400"}', 
true),

('Kingston FURY Beast DDR5-6000 32GB', 'Модуль памяти Kingston FURY Beast, DDR5-6000, 32 ГБ (2x16 ГБ)', 24999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Kingston'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "6000", "timings": "40-40-40-80", "voltage": "1.35", "rgb": "No", "performance": "12000"}', 
true),

('Crucial DDR5-5600 32GB', 'Модуль памяти Crucial, DDR5-5600, 32 ГБ (2x16 ГБ)', 19999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "5600", "timings": "46-45-45-90", "voltage": "1.25", "rgb": "No", "performance": "11200"}', 
true),

('Corsair Vengeance RGB DDR5-6400 64GB', 'Модуль памяти Corsair Vengeance RGB, DDR5-6400, 64 ГБ (2x32 ГБ)', 39999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "64", "type": "DDR5", "modules": "2", "frequency": "6400", "timings": "32-39-39-76", "voltage": "1.4", "rgb": "Yes", "performance": "12800"}', 
true); 