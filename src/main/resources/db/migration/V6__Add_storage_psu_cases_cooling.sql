-- Добавляем накопители SSD NVMe (6 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Samsung 990 PRO 2TB', 'SSD накопитель Samsung 990 PRO, 2 ТБ, M.2 NVMe PCIe 4.0, высокая производительность', 24999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "2000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "7450", "write_speed": "6900", "tlc": "Yes", "dram": "Yes", "endurance": "1200", "performance": "7450", "m2_required": "1"}', 
true),

('Western Digital Black SN850X 1TB', 'SSD накопитель WD Black SN850X, 1 ТБ, M.2 NVMe PCIe 4.0, игровая серия', 14999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Western Digital'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "7300", "write_speed": "6600", "tlc": "Yes", "dram": "Yes", "endurance": "600", "performance": "7300", "m2_required": "1"}', 
true),

('Crucial P5 Plus 1TB', 'SSD накопитель Crucial P5 Plus, 1 ТБ, M.2 NVMe PCIe 4.0, оптимальная цена', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "6600", "write_speed": "5000", "tlc": "Yes", "dram": "Yes", "endurance": "600", "performance": "6600", "m2_required": "1"}', 
true),

('Kingston NV2 1TB', 'SSD накопитель Kingston NV2, 1 ТБ, M.2 NVMe PCIe 4.0, бюджетная модель', 6999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Kingston'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "3500", "write_speed": "2100", "tlc": "Yes", "dram": "No", "endurance": "600", "performance": "3500", "m2_required": "1"}', 
true),

('Samsung 980 500GB', 'SSD накопитель Samsung 980, 500 ГБ, M.2 NVMe PCIe 3.0, компактный объем', 5999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "500", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 3.0", "read_speed": "3500", "write_speed": "3000", "tlc": "Yes", "dram": "No", "endurance": "300", "performance": "3500", "m2_required": "1"}', 
true),

('Western Digital Green 480GB', 'SSD накопитель Western Digital Green, 480 ГБ, 2.5" SATA III, бюджетное решение', 3499.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'Western Digital'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "480", "type": "SSD", "form_factor": "2.5", "interface": "SATA III", "read_speed": "545", "write_speed": "430", "tlc": "Yes", "dram": "No", "endurance": "100", "performance": "545", "m2_required": "0"}', 
true);

-- Добавляем блоки питания (6 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Seasonic Focus GX-1000', 'Блок питания Seasonic Focus GX-1000, 1000 Вт, 80+ Gold, модульный', 19999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Seasonic'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "1000", "efficiency": "80+ Gold", "modular": "Yes", "fan_size": "135", "form_factor": "ATX", "length": "150", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "4x 8-pin", "sata_connectors": "8", "molex_connectors": "4", "warranty": "10"}', 
true),

('Corsair RM850x', 'Блок питания Corsair RM850x, 850 Вт, 80+ Gold, полностью модульный', 16999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "850", "efficiency": "80+ Gold", "modular": "Yes", "fan_size": "135", "form_factor": "ATX", "length": "160", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "4x 8-pin", "sata_connectors": "8", "molex_connectors": "4", "warranty": "10"}', 
true),

('be quiet! Straight Power 11 750W', 'Блок питания be quiet! Straight Power 11, 750 Вт, 80+ Gold, модульный', 14999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "750", "efficiency": "80+ Gold", "modular": "Yes", "fan_size": "135", "form_factor": "ATX", "length": "150", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "4x 8-pin", "sata_connectors": "6", "molex_connectors": "3", "warranty": "5"}', 
true),

('EVGA SuperNOVA 650 G5', 'Блок питания EVGA SuperNOVA 650 G5, 650 Вт, 80+ Gold, полностью модульный', 11999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'EVGA'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "650", "efficiency": "80+ Gold", "modular": "Yes", "fan_size": "135", "form_factor": "ATX", "length": "150", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "4x 8-pin", "sata_connectors": "6", "molex_connectors": "3", "warranty": "10"}', 
true),

('Thermaltake Toughpower GF1 550W', 'Блок питания Thermaltake Toughpower GF1, 550 Вт, 80+ Gold, полностью модульный', 8999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "550", "efficiency": "80+ Gold", "modular": "Yes", "fan_size": "140", "form_factor": "ATX", "length": "140", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "2x 8-pin", "sata_connectors": "6", "molex_connectors": "3", "warranty": "10"}', 
true),

('Cooler Master MWE Gold 500W', 'Блок питания Cooler Master MWE Gold, 500 Вт, 80+ Gold, не модульный', 6999.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"power": "500", "efficiency": "80+ Gold", "modular": "No", "fan_size": "120", "form_factor": "ATX", "length": "140", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_connectors": "2x 8-pin", "sata_connectors": "4", "molex_connectors": "2", "warranty": "5"}', 
true);

-- Добавляем корпуса (7 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NZXT H7 Flow', 'Корпус NZXT H7 Flow, ATX Mid Tower, с отличной вентиляцией', 12999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "480x230x505", "weight": "12.8", "material": "Steel, Tempered Glass", "max_gpu_length": "400", "max_cpu_cooler_height": "185", "max_psu_length": "200", "included_fans": "2", "performance": "90", "max_gpu_height": "165", "psu_form_factor": "ATX"}', 
true),

('Corsair 5000D Airflow', 'Корпус Corsair 5000D Airflow, ATX Mid Tower, оптимизированный для охлаждения', 14999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "520x245x520", "weight": "13.8", "material": "Steel, Tempered Glass", "max_gpu_length": "420", "max_cpu_cooler_height": "170", "max_psu_length": "225", "included_fans": "3", "performance": "92", "max_gpu_height": "165", "psu_form_factor": "ATX"}', 
true),

('be quiet! Pure Base 500DX', 'Корпус be quiet! Pure Base 500DX, ATX Mid Tower, с RGB подсветкой', 9999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "463x232x463", "weight": "8.2", "material": "Steel, Tempered Glass", "max_gpu_length": "369", "max_cpu_cooler_height": "190", "max_psu_length": "225", "included_fans": "3", "performance": "85", "max_gpu_height": "165", "psu_form_factor": "ATX"}', 
true),

('Cooler Master MasterBox TD500 Mesh', 'Корпус Cooler Master MasterBox TD500 Mesh, ATX Mid Tower, с передней сеткой для вентиляции', 7999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "493x217x468", "weight": "7.6", "material": "Steel, Tempered Glass", "max_gpu_length": "410", "max_cpu_cooler_height": "165", "max_psu_length": "180", "included_fans": "3", "performance": "80", "max_gpu_height": "165", "psu_form_factor": "ATX"}', 
true),

('NZXT H210i', 'Корпус NZXT H210i, Mini-ITX, компактный и стильный', 8499.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "Mini-ITX", "type": "Mini Tower", "dimensions": "349x210x372", "weight": "6.0", "material": "Steel, Tempered Glass", "max_gpu_length": "325", "max_cpu_cooler_height": "165", "max_psu_length": "210", "included_fans": "2", "performance": "70", "max_gpu_height": "140", "psu_form_factor": "SFX"}', 
true),

('Cooler Master NR200P', 'Корпус Cooler Master NR200P, Mini-ITX, компактный и с хорошей вентиляцией', 8999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "Mini-ITX", "type": "Mini Tower", "dimensions": "376x185x274", "weight": "4.3", "material": "Steel, Tempered Glass/Mesh", "max_gpu_length": "330", "max_cpu_cooler_height": "155", "max_psu_length": "130", "included_fans": "2", "performance": "75", "max_gpu_height": "156", "psu_form_factor": "SFX"}', 
true),

('Thermaltake Core P3', 'Корпус Thermaltake Core P3, ATX Open Frame, с панорамным обзором компонентов', 11999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Open Frame", "dimensions": "512x333x470", "weight": "10.9", "material": "Steel, Tempered Glass", "max_gpu_length": "450", "max_cpu_cooler_height": "180", "max_psu_length": "220", "included_fans": "0", "performance": "75", "max_gpu_height": "165", "psu_form_factor": "ATX"}', 
true);

-- Добавляем системы охлаждения (6 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NZXT Kraken X73', 'Система жидкостного охлаждения NZXT Kraken X73, 360 мм радиатор', 16999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "360", "fan_size": "120", "fan_count": "3", "max_tdp": "350", "rgb": "Yes", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "21-36", "performance": "300", "height": "52", "ram_clearance": "64"}', 
true),

('Corsair iCUE H150i ELITE CAPELLIX', 'Система жидкостного охлаждения Corsair iCUE H150i ELITE CAPELLIX, 360 мм радиатор', 17999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "360", "fan_size": "120", "fan_count": "3", "max_tdp": "380", "rgb": "Yes", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "10-36", "performance": "310", "height": "54", "ram_clearance": "64"}', 
true),

('be quiet! Dark Rock Pro 4', 'Воздушный кулер be quiet! Dark Rock Pro 4, двойной вентилятор', 8999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "162.8", "fan_size": "120,135", "fan_count": "2", "max_tdp": "250", "rgb": "No", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "12-24", "performance": "250", "ram_clearance": "40"}', 
true),

('Cooler Master Hyper 212 RGB Black Edition', 'Воздушный кулер Cooler Master Hyper 212 RGB Black Edition', 4999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "159", "fan_size": "120", "fan_count": "1", "max_tdp": "150", "rgb": "Yes", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "8-27", "performance": "150", "ram_clearance": "32"}', 
true),

('NZXT Kraken X63', 'Система жидкостного охлаждения NZXT Kraken X63, 280 мм радиатор', 13999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'NZXT'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "280", "fan_size": "140", "fan_count": "2", "max_tdp": "300", "rgb": "Yes", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "21-36", "performance": "280", "height": "52", "ram_clearance": "64"}', 
true),

('Thermaltake TH240 ARGB', 'Система жидкостного охлаждения Thermaltake TH240 ARGB, 240 мм радиатор', 8999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "240", "fan_size": "120", "fan_count": "2", "max_tdp": "250", "rgb": "Yes", "socket": "LGA1700,LGA1200,AM5,AM4", "noise_level": "14-28", "performance": "250", "height": "52", "ram_clearance": "64"}', 
true); 