-- Добавляем дополнительные компоненты для ПК

-- Добавляем высокопроизводительные и бюджетные процессоры Intel и AMD
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- Серия Intel Core 12-го поколения (более бюджетные)
('Intel Core i5-12400F', 'Процессор Intel Core i5-12400F, 6 ядер, 12 потоков, без встроенной графики', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "LGA1700", "frequency": "2.5", "max_frequency": "4.4", "tdp": "65", "cache": "18", "technology": "10nm", "integrated_graphics": "None", "performance": "25000", "overclocking_support": "No", "max_memory_frequency": "4800", "max_memory_channels": "2", "ram_type": "DDR4,DDR5"}', 
true),

('Intel Core i3-12100', 'Процессор Intel Core i3-12100, 4 ядра, 8 потоков, со встроенной графикой UHD 730', 8999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "4", "threads": "8", "socket": "LGA1700", "frequency": "3.3", "max_frequency": "4.3", "tdp": "60", "cache": "12", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 730", "performance": "18000", "overclocking_support": "No", "max_memory_frequency": "4800", "max_memory_channels": "2", "ram_type": "DDR4,DDR5"}', 
true),

-- Серия AMD Ryzen 5000
('AMD Ryzen 7 5800X3D', 'Процессор AMD Ryzen 7 5800X3D, 8 ядер, 16 потоков, 3D V-Cache', 29999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "8", "threads": "16", "socket": "AM4", "frequency": "3.4", "max_frequency": "4.5", "tdp": "105", "cache": "96", "technology": "7nm", "integrated_graphics": "None", "performance": "36000", "overclocking_support": "Yes", "max_memory_frequency": "3200", "max_memory_channels": "2", "ram_type": "DDR4"}', 
true),

('AMD Ryzen 5 5600G', 'Процессор AMD Ryzen 5 5600G, 6 ядер, 12 потоков, со встроенной графикой Radeon Vega 7', 13999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "AM4", "frequency": "3.9", "max_frequency": "4.4", "tdp": "65", "cache": "19", "technology": "7nm", "integrated_graphics": "AMD Radeon Vega 7", "performance": "23000", "overclocking_support": "Yes", "max_memory_frequency": "3200", "max_memory_channels": "2", "ram_type": "DDR4"}', 
true),

-- Серия Intel для рабочих станций
('Intel Core i9-13980HX', 'Процессор Intel Core i9-13980HX, 24 ядра (8P+16E), 32 потока, для мощных рабочих станций', 92999.99, 5, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "24", "threads": "32", "socket": "BGA", "frequency": "2.2", "max_frequency": "5.6", "tdp": "157", "cache": "36", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics", "performance": "58000", "overclocking_support": "Yes", "max_memory_frequency": "5600", "max_memory_channels": "2", "ram_type": "DDR5"}', 
true);

-- Добавляем материнские платы бюджетного и среднего ценового сегмента
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- Intel B760 (средний сегмент для 12-13 поколения)
('MSI MAG B760M Mortar WiFi', 'Материнская плата MSI MAG B760M Mortar WiFi, формат mATX, для Intel 12-13 поколения', 15999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "B760", "form_factor": "mATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7200", "pcie_version": "5.0", "sata_ports": "4", "m2_slots": "3", "usb_ports": "8", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "overclocking_support": "Limited", "xmp_support": "Yes", "ram_slots": "4", "m2_supported_keys": "M,B,E"}', 
true),

-- Intel H610 (бюджетный сегмент для 12-13 поколения)
('ASUS PRIME H610M-K D4', 'Материнская плата ASUS PRIME H610M-K D4, формат mATX, бюджетная модель для Intel 12-13 поколения', 8999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "H610", "form_factor": "mATX", "memory_slots": "2", "max_memory": "64", "ram_type": "DDR4", "max_ram_frequency": "3200", "pcie_version": "4.0", "sata_ports": "4", "m2_slots": "1", "usb_ports": "6", "wifi": "No", "bluetooth": "No", "lan": "1 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "overclocking_support": "No", "xmp_support": "No", "ram_slots": "2", "m2_supported_keys": "M"}', 
true),

-- AMD B650 (средний сегмент для Ryzen 7000)
('Gigabyte B650 AORUS ELITE AX', 'Материнская плата Gigabyte B650 AORUS ELITE AX, формат ATX, для AMD Ryzen 7000', 19999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Gigabyte'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "B650", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "6600", "pcie_version": "4.0", "sata_ports": "4", "m2_slots": "4", "usb_ports": "10", "wifi": "Wi-Fi 6E", "bluetooth": "5.2", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "overclocking_support": "Yes", "xmp_support": "Yes", "ram_slots": "4", "m2_supported_keys": "M,B"}', 
true),

-- AMD X570 (высокий сегмент для Ryzen 5000)
('ASRock X570 Steel Legend', 'Материнская плата ASRock X570 Steel Legend, формат ATX, для AMD Ryzen 5000', 17999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASRock'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM4", "chipset": "X570", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR4", "max_ram_frequency": "4666", "pcie_version": "4.0", "sata_ports": "8", "m2_slots": "2", "usb_ports": "8", "wifi": "No", "bluetooth": "No", "lan": "1 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "overclocking_support": "Yes", "xmp_support": "Yes", "ram_slots": "4", "m2_supported_keys": "M"}', 
true);

-- Добавляем видеокарты среднего и бюджетного сегмента
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- NVIDIA бюджетные решения
('NVIDIA GeForce RTX 4060', 'Видеокарта NVIDIA GeForce RTX 4060, 8 ГБ GDDR6, бюджетное решение с поддержкой трассировки лучей', 31999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "8", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1830", "boost_clock": "2460", "length": "200", "width": "112", "power": "115", "recommended_psu": "550", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 8-pin", "performance": "25000", "pcie_lanes": "8"}', 
true),

('NVIDIA GeForce RTX 3050', 'Видеокарта NVIDIA GeForce RTX 3050, 8 ГБ GDDR6, доступное решение с базовой поддержкой трассировки лучей', 24999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "8", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1552", "boost_clock": "1777", "length": "190", "width": "110", "power": "130", "recommended_psu": "550", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 8-pin", "performance": "18000", "pcie_lanes": "8"}', 
true),

-- AMD бюджетные решения
('AMD Radeon RX 6700 XT', 'Видеокарта AMD Radeon RX 6700 XT, 12 ГБ GDDR6, оптимальное соотношение цены и производительности', 42999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "12", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "2321", "boost_clock": "2581", "length": "267", "width": "120", "power": "230", "recommended_psu": "650", "ports": "HDMI 2.1, 2x DisplayPort 1.4", "power_connectors": "1x 8-pin, 1x 6-pin", "performance": "29000", "pcie_lanes": "16"}', 
true),

('AMD Radeon RX 6600', 'Видеокарта AMD Radeon RX 6600, 8 ГБ GDDR6, отличное решение для игр в разрешении 1080p', 26999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "8", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1626", "boost_clock": "2491", "length": "190", "width": "110", "power": "132", "recommended_psu": "500", "ports": "HDMI 2.1, 2x DisplayPort 1.4", "power_connectors": "1x 8-pin", "performance": "21000", "pcie_lanes": "8"}', 
true);

-- Добавляем оперативную память DDR4 (для совместимости со старыми системами)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Corsair Vengeance RGB Pro DDR4-3600 32GB', 'Модуль памяти Corsair Vengeance RGB Pro, DDR4-3600, 32 ГБ (2x16 ГБ)', 14999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR4", "modules": "2", "frequency": "3600", "timings": "18-22-22-42", "voltage": "1.35", "rgb": "Yes", "performance": "7200", "power_consumption": "6", "xmp_support": "Yes"}', 
true),

('G.Skill Ripjaws V DDR4-3200 16GB', 'Модуль памяти G.Skill Ripjaws V, DDR4-3200, 16 ГБ (2x8 ГБ)', 8999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'G.Skill'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "16", "type": "DDR4", "modules": "2", "frequency": "3200", "timings": "16-18-18-38", "voltage": "1.35", "rgb": "No", "performance": "6400", "power_consumption": "5", "xmp_support": "Yes"}', 
true),

('Kingston FURY Beast DDR4-3200 16GB', 'Модуль памяти Kingston FURY Beast, DDR4-3200, 16 ГБ (2x8 ГБ)', 7999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Kingston'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "16", "type": "DDR4", "modules": "2", "frequency": "3200", "timings": "16-18-18-36", "voltage": "1.35", "rgb": "No", "performance": "6400", "power_consumption": "5", "xmp_support": "Yes"}', 
true),

('Crucial Ballistix DDR4-3600 32GB', 'Модуль памяти Crucial Ballistix, DDR4-3600, 32 ГБ (2x16 ГБ)', 13999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR4", "modules": "2", "frequency": "3600", "timings": "16-18-18-38", "voltage": "1.35", "rgb": "No", "performance": "7200", "power_consumption": "6", "xmp_support": "Yes"}', 
true);

-- Добавляем SSD накопители бюджетного и среднего сегмента
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- Бюджетные NVMe
('Kingston NV2 1TB', 'SSD накопитель Kingston NV2, 1 ТБ, M.2 NVMe PCIe 4.0, бюджетная модель', 6999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Kingston'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "3500", "write_speed": "2100", "tlc": "Yes", "dram": "No", "endurance": "600", "performance": "3500", "power_consumption": "6", "m2_key": "M"}', 
true),

('Crucial P3 2TB', 'SSD накопитель Crucial P3, 2 ТБ, M.2 NVMe PCIe 3.0, доступная цена при хорошей емкости', 10999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "2000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 3.0", "read_speed": "3500", "write_speed": "3000", "tlc": "Yes", "dram": "No", "endurance": "800", "performance": "3500", "power_consumption": "5", "m2_key": "M"}', 
true),

-- Портативные SSD
('Samsung T7 Shield 1TB', 'Внешний SSD Samsung T7 Shield, 1 ТБ, портативный и прочный', 10999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "External", "interface": "USB 3.2 Gen2", "read_speed": "1050", "write_speed": "1000", "tlc": "Yes", "dram": "Yes", "endurance": "600", "performance": "1050", "power_consumption": "2"}', 
true),

-- Бюджетные SATA SSD
('Western Digital Green 480GB', 'SSD накопитель Western Digital Green, 480 ГБ, 2.5" SATA III, бюджетное решение', 3499.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'Western Digital'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "480", "type": "SSD", "form_factor": "2.5", "interface": "SATA III", "read_speed": "545", "write_speed": "430", "tlc": "Yes", "dram": "No", "endurance": "100", "performance": "545", "power_consumption": "2"}', 
true);

-- Добавляем бюджетные и компактные корпуса
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- Компактные Mini-ITX корпуса
('Cooler Master NR200P', 'Корпус Cooler Master NR200P, Mini-ITX, компактный и с хорошей вентиляцией', 8999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "Mini-ITX", "type": "Mini Tower", "dimensions": "376x185x274", "weight": "4.3", "material": "Steel, Tempered Glass/Mesh", "max_gpu_length": "330", "max_cpu_cooler_height": "155", "max_psu_length": "130", "included_fans": "2", "performance": "75", "airflow_rating": "85", "drive_bays": "2.5 and 3.5", "radiator_support": "240,280"}', 
true),

-- Бюджетные ATX корпуса
('Deepcool MATREXX 55 MESH', 'Корпус Deepcool MATREXX 55 MESH, ATX Mid Tower, с сетчатой передней панелью для хорошей вентиляцией', 5999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "ATX", "type": "Mid Tower", "dimensions": "440x210x480", "weight": "5.7", "material": "Steel, Tempered Glass", "max_gpu_length": "370", "max_cpu_cooler_height": "168", "max_psu_length": "170", "included_fans": "1", "performance": "70", "airflow_rating": "75", "drive_bays": "2.5 and 3.5", "radiator_support": "120,240,280,360"}', 
true),

-- Корпуса с поддержкой E-ATX
('Phanteks Enthoo Pro 2', 'Корпус Phanteks Enthoo Pro 2, E-ATX Full Tower, с превосходной вентиляцией и огромным пространством', 18999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Cooler Master'), 
(SELECT id FROM categories WHERE name = 'Корпуса'), 
'CASE', 
'{"form_factor": "E-ATX", "type": "Full Tower", "dimensions": "580x240x560", "weight": "13.8", "material": "Steel, Tempered Glass", "max_gpu_length": "503", "max_cpu_cooler_height": "195", "max_psu_length": "250", "included_fans": "3", "performance": "95", "airflow_rating": "95", "drive_bays": "2.5 and 3.5", "radiator_support": "120,140,240,280,360,420,480"}', 
true);

-- Добавляем бюджетные системы охлаждения
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
-- Бюджетные воздушные кулеры
('DeepCool AK400', 'Воздушный кулер DeepCool AK400, эффективный и доступный', 2999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "155", "fan_size": "120", "fan_count": "1", "max_tdp": "220", "rgb": "No", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "28", "performance": "220", "socket": "LGA1700,LGA1200,AM5,AM4"}', 
true),

('ID-COOLING SE-224-XT', 'Воздушный кулер ID-COOLING SE-224-XT, отличное охлаждение по низкой цене', 2499.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "154", "fan_size": "120", "fan_count": "1", "max_tdp": "180", "rgb": "No", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "31.6", "performance": "180", "socket": "LGA1700,LGA1200,AM5,AM4"}', 
true),

-- Бюджетные СЖО
('ARCTIC Liquid Freezer II 240', 'Система жидкостного охлаждения ARCTIC Liquid Freezer II 240, 240 мм радиатор, оптимальное соотношение цены и производительности', 8499.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Liquid", "radiator_size": "240", "fan_size": "120", "fan_count": "2", "max_tdp": "250", "rgb": "No", "socket_support": "LGA1700,LGA1200,AM5,AM4", "noise_level": "22.5", "performance": "250", "socket": "LGA1700,LGA1200,AM5,AM4"}', 
true),

-- Низкопрофильные кулеры для компактных систем
('Noctua NH-L9i', 'Воздушный кулер Noctua NH-L9i, низкопрофильный для компактных систем', 4999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Thermaltake'), 
(SELECT id FROM categories WHERE name = 'Системы охлаждения'), 
'COOLER', 
'{"type": "Air", "height": "37", "fan_size": "92", "fan_count": "1", "max_tdp": "95", "rgb": "No", "socket_support": "LGA1700,LGA1200", "noise_level": "23.6", "performance": "95", "socket": "LGA1700,LGA1200"}', 
true); 