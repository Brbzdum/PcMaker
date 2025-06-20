-- Сбрасываем и заполняем все данные системы
-- Этот скрипт сначала удаляет все существующие данные, а затем заполняет таблицы новыми данными

-- Сначала отключаем проверку внешних ключей
SET session_replication_role = 'replica';

-- Очищаем таблицы (в обратном порядке зависимостей)
TRUNCATE TABLE order_items CASCADE;
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE cart_items CASCADE;
TRUNCATE TABLE carts CASCADE;
TRUNCATE TABLE config_components CASCADE;
TRUNCATE TABLE pc_configurations CASCADE;
TRUNCATE TABLE reviews CASCADE;
TRUNCATE TABLE compatibility_rules CASCADE;
TRUNCATE TABLE products CASCADE;
TRUNCATE TABLE categories CASCADE;
TRUNCATE TABLE manufacturers CASCADE;

-- Возвращаем проверку внешних ключей
SET session_replication_role = 'origin';

-- Заполняем таблицу производителей
INSERT INTO manufacturers (name, description, rating) VALUES
('Intel', 'Ведущий производитель процессоров', 4.7),
('AMD', 'Производитель процессоров и видеокарт', 4.5),
('NVIDIA', 'Лидер в производстве графических процессоров', 4.8),
('ASUS', 'Тайваньский производитель компьютерной техники', 4.6),
('MSI', 'Производитель материнских плат и компьютерной периферии', 4.4),
('Gigabyte', 'Производитель материнских плат и видеокарт', 4.3),
('Kingston', 'Производитель памяти и накопителей', 4.2),
('Corsair', 'Производитель комплектующих для ПК', 4.5),
('Crucial', 'Производитель памяти и SSD накопителей', 4.1),
('Samsung', 'Производитель электроники и комплектующих', 4.7),
('Western Digital', 'Производитель жестких дисков и SSD', 4.3),
('Seagate', 'Производитель жестких дисков', 4.0),
('Thermaltake', 'Производитель корпусов и систем охлаждения', 4.2),
('NZXT', 'Производитель корпусов и компонентов для ПК', 4.4),
('Cooler Master', 'Производитель корпусов и систем охлаждения', 4.3),
('be quiet!', 'Производитель корпусов и блоков питания', 4.6),
('EVGA', 'Производитель видеокарт и блоков питания', 4.4),
('Seasonic', 'Производитель блоков питания', 4.7),
('ASRock', 'Производитель материнских плат', 4.2),
('G.Skill', 'Производитель оперативной памяти', 4.5),
('Logitech', 'Производитель компьютерной периферии', 4.6),
('Razer', 'Производитель игровой периферии', 4.5),
('SteelSeries', 'Производитель игровой периферии', 4.4),
('HyperX', 'Производитель игровой периферии', 4.3),
('BenQ', 'Производитель мониторов', 4.2),
('Dell', 'Производитель компьютерной техники и периферии', 4.4),
('LG', 'Производитель электроники и мониторов', 4.5),
('HP', 'Производитель компьютеров и периферии', 4.3),
('Canon', 'Производитель фототехники и принтеров', 4.6),
('Epson', 'Производитель принтеров и сканеров', 4.5);

-- Заполняем таблицу категорий
INSERT INTO categories (name, description, is_pc_component, slug, is_peripheral) VALUES
-- Компоненты ПК
('Процессоры', 'Центральные процессоры (CPU)', true, 'processors', false),
('Видеокарты', 'Графические процессоры (GPU)', true, 'graphic-cards', false),
('Материнские платы', 'Основные платы компьютера', true, 'motherboards', false),
('Оперативная память', 'Модули RAM', true, 'ram', false),
('Накопители', 'SSD и HDD накопители', true, 'storage', false),
('Блоки питания', 'Источники питания компьютера', true, 'power-supplies', false),
('Корпуса', 'Корпуса для ПК', true, 'cases', false),
('Системы охлаждения', 'Кулеры и системы жидкостного охлаждения', true, 'cooling', false),
-- Периферийные устройства
('Мониторы', 'Дисплеи для отображения информации', false, 'monitors', true),
('Клавиатуры', 'Устройства ввода для ПК', false, 'keyboards', true),
('Мыши', 'Манипуляторы для управления курсором', false, 'mice', true),
('Гарнитуры', 'Наушники с микрофоном для компьютера', false, 'headsets', true),
('Колонки', 'Акустические системы для ПК', false, 'speakers', true),
('Web-камеры', 'Камеры для видеосвязи', false, 'webcams', true),
('Принтеры', 'Устройства для печати документов', false, 'printers', true),
('Сканеры', 'Устройства для оцифровки изображений', false, 'scanners', true),
('Игровые контроллеры', 'Джойстики и геймпады для игр', false, 'gamepads', true),
('Сетевое оборудование', 'Маршрутизаторы, коммутаторы и сетевые адаптеры', false, 'network', true),
('Наушники', 'Устройства для прослушивания аудио', false, 'headphones', true),
('Коврики для мыши', 'Поверхности для комфортного использования мыши', false, 'mousepads', true),
('Микрофоны', 'Устройства для записи звука', false, 'microphones', true);

-- Добавляем процессоры Intel (10 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Intel Core i9-14900K', 'Процессор Intel Core i9-14900K, 24 ядра (8P+16E), 32 потока, 6.0 ГГц макс. турбо', 59999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "24", "threads": "32", "socket": "LGA1700", "frequency": "3.2", "max_frequency": "6.0", "tdp": "125", "cache": "36", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "45000", "power": "125", "max_ram_frequency": "5600", "supported_chipsets": "Z790,B760,H610"}', 
true),

('Intel Core i7-14700K', 'Процессор Intel Core i7-14700K, 20 ядер (8P+12E), 28 потоков, 5.6 ГГц макс. турбо', 42999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "20", "threads": "28", "socket": "LGA1700", "frequency": "3.4", "max_frequency": "5.6", "tdp": "125", "cache": "33", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "39500", "power": "125", "max_ram_frequency": "5600", "supported_chipsets": "Z790,B760,H610"}', 
true),

('Intel Core i5-14600K', 'Процессор Intel Core i5-14600K, 14 ядер (6P+8E), 20 потоков, 5.3 ГГц макс. турбо', 31999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "14", "threads": "20", "socket": "LGA1700", "frequency": "3.5", "max_frequency": "5.3", "tdp": "125", "cache": "24", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "32000", "power": "125", "max_ram_frequency": "5600", "supported_chipsets": "Z790,B760,H610"}', 
true),

('Intel Core i5-12400F', 'Процессор Intel Core i5-12400F, 6 ядер, 12 потоков, без встроенной графики', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "LGA1700", "frequency": "2.5", "max_frequency": "4.4", "tdp": "65", "cache": "18", "technology": "10nm", "integrated_graphics": "None", "performance": "25000", "power": "65", "max_ram_frequency": "4800", "supported_chipsets": "Z790,B760,H610"}', 
true),

('Intel Core i3-12100', 'Процессор Intel Core i3-12100, 4 ядра, 8 потоков, со встроенной графикой UHD 730', 8999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "4", "threads": "8", "socket": "LGA1700", "frequency": "3.3", "max_frequency": "4.3", "tdp": "60", "cache": "12", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 730", "performance": "18000", "power": "60", "max_ram_frequency": "4800", "supported_chipsets": "Z790,B760,H610"}', 
true);

-- Добавляем процессоры AMD (5 продуктов)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('AMD Ryzen 9 7950X3D', 'Процессор AMD Ryzen 9 7950X3D, 16 ядер, 32 потока, 5.7 ГГц макс. турбо, 3D V-Cache', 64999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "16", "threads": "32", "socket": "AM5", "frequency": "4.2", "max_frequency": "5.7", "tdp": "120", "cache": "144", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "46000", "power": "120", "max_ram_frequency": "5200", "supported_chipsets": "X670E,X670,B650E,B650"}', 
true),

('AMD Ryzen 9 7900X', 'Процессор AMD Ryzen 9 7900X, 12 ядер, 24 потока, 5.6 ГГц макс. турбо', 47999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "12", "threads": "24", "socket": "AM5", "frequency": "4.7", "max_frequency": "5.6", "tdp": "170", "cache": "76", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "41000", "power": "170", "max_ram_frequency": "5200", "supported_chipsets": "X670E,X670,B650E,B650"}', 
true),

('AMD Ryzen 7 7800X3D', 'Процессор AMD Ryzen 7 7800X3D, 8 ядер, 16 потоков, 5.0 ГГц макс. турбо, 3D V-Cache', 42999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "8", "threads": "16", "socket": "AM5", "frequency": "4.2", "max_frequency": "5.0", "tdp": "120", "cache": "96", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "37000", "power": "120", "max_ram_frequency": "5200", "supported_chipsets": "X670E,X670,B650E,B650"}', 
true),

('AMD Ryzen 5 7600X', 'Процессор AMD Ryzen 5 7600X, 6 ядер, 12 потоков, 5.3 ГГц макс. турбо', 27999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "AM5", "frequency": "4.7", "max_frequency": "5.3", "tdp": "105", "cache": "38", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "30000", "power": "105", "max_ram_frequency": "5200", "supported_chipsets": "X670E,X670,B650E,B650"}', 
true),

('AMD Ryzen 5 5600G', 'Процессор AMD Ryzen 5 5600G, 6 ядер, 12 потоков, со встроенной графикой Radeon Vega 7', 13999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "AM4", "frequency": "3.9", "max_frequency": "4.4", "tdp": "65", "cache": "19", "technology": "7nm", "integrated_graphics": "AMD Radeon Vega 7", "performance": "23000", "power": "65", "max_ram_frequency": "3200", "supported_chipsets": "X570,B550,A520"}', 
true);

-- Добавляем материнские платы Intel (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('ASUS ROG Maximus Z790 Hero', 'Материнская плата ASUS ROG Maximus Z790 Hero для процессоров Intel 12-14 поколения', 49999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7800", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "12", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 5.0 x16, PCIe 4.0 x16, PCIe 3.0 x16", "pcie_x16_slots": "3", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "Z790"}', 
true),

('MSI MPG Z790 Carbon WiFi', 'Материнская плата MSI MPG Z790 Carbon WiFi для процессоров Intel 12-14 поколения', 38999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "4", "usb_ports": "10", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 5.0 x16, PCIe 4.0 x16", "pcie_x16_slots": "2", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "Z790"}', 
true),

('MSI MAG B760M Mortar WiFi', 'Материнская плата MSI MAG B760M Mortar WiFi, формат mATX, для Intel 12-13 поколения', 15999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "B760", "form_factor": "mATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7200", "pcie_version": "5.0", "sata_ports": "4", "m2_slots": "3", "usb_ports": "8", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 5.0 x16, PCIe 4.0 x16", "pcie_x16_slots": "2", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "B760"}', 
true),

('ASUS PRIME H610M-K D4', 'Материнская плата ASUS PRIME H610M-K D4, формат mATX, бюджетная модель для Intel 12-13 поколения', 8999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "H610", "form_factor": "mATX", "memory_slots": "2", "max_memory": "64", "ram_type": "DDR4", "max_ram_frequency": "3200", "pcie_version": "4.0", "sata_ports": "4", "m2_slots": "1", "usb_ports": "6", "wifi": "No", "bluetooth": "No", "lan": "1 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 4.0 x16", "pcie_x16_slots": "1", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "2", "supported_chipsets": "H610"}', 
true);

-- Добавляем материнские платы AMD (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('ASUS ROG Crosshair X670E Hero', 'Материнская плата ASUS ROG Crosshair X670E Hero для процессоров AMD Ryzen 7000', 55999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7800", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "12", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "10 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 5.0 x16, PCIe 4.0 x16, PCIe 3.0 x16", "pcie_x16_slots": "3", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "X670E"}', 
true),

('MSI MEG X670E ACE', 'Материнская плата MSI MEG X670E ACE для процессоров AMD Ryzen 7000', 52999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "11", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "10 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 5.0 x16, PCIe 4.0 x16", "pcie_x16_slots": "2", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "X670E"}', 
true),

('Gigabyte B650 AORUS ELITE AX', 'Материнская плата Gigabyte B650 AORUS ELITE AX, формат ATX, для AMD Ryzen 7000', 19999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Gigabyte'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "B650", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "6600", "pcie_version": "4.0", "sata_ports": "4", "m2_slots": "4", "usb_ports": "10", "wifi": "Wi-Fi 6E", "bluetooth": "5.2", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 4.0 x16, PCIe 3.0 x16", "pcie_x16_slots": "2", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "B650"}', 
true),

('ASRock X570 Steel Legend', 'Материнская плата ASRock X570 Steel Legend, формат ATX, для AMD Ryzen 5000', 17999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASRock'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM4", "chipset": "X570", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR4", "max_ram_frequency": "4666", "pcie_version": "4.0", "sata_ports": "8", "m2_slots": "2", "usb_ports": "8", "wifi": "No", "bluetooth": "No", "lan": "1 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU", "pcie_slots": "PCIe 4.0 x16, PCIe 3.0 x16", "pcie_x16_slots": "2", "storage_interfaces": "SATA III, M.2 NVMe", "ram_slots": "4", "supported_chipsets": "X570"}', 
true);

-- Добавляем видеокарты NVIDIA (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('NVIDIA GeForce RTX 4090', 'Видеокарта NVIDIA GeForce RTX 4090, 24 ГБ GDDR6X, высочайшая производительность для игр и рендеринга', 159999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "24", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "2235", "boost_clock": "2520", "length": "336", "width": "140", "height": "61", "power": "450", "recommended_psu": "850", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 16-pin", "performance": "62000", "vram_size": "24", "slots_required": "3"}', 
true),

('NVIDIA GeForce RTX 4080 Super', 'Видеокарта NVIDIA GeForce RTX 4080 Super, 16 ГБ GDDR6X, высокая производительность для требовательных игр', 119999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "16", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "2205", "boost_clock": "2505", "length": "304", "width": "137", "height": "54", "power": "320", "recommended_psu": "750", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 16-pin", "performance": "54000", "vram_size": "16", "slots_required": "2.5"}', 
true),

('NVIDIA GeForce RTX 4070', 'Видеокарта NVIDIA GeForce RTX 4070, 12 ГБ GDDR6X, отличный выбор для игр в разрешении 1440p', 65999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "12", "memory_type": "GDDR6X", "interface": "PCIe 4.0", "core_clock": "1920", "boost_clock": "2475", "length": "244", "width": "112", "height": "40", "power": "200", "recommended_psu": "650", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 8-pin, 1x 6-pin", "performance": "35000", "vram_size": "12", "slots_required": "2"}', 
true),

('NVIDIA GeForce RTX 4060', 'Видеокарта NVIDIA GeForce RTX 4060, 8 ГБ GDDR6, бюджетное решение с поддержкой трассировки лучей', 31999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'NVIDIA'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "8", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1830", "boost_clock": "2460", "length": "200", "width": "112", "height": "40", "power": "115", "recommended_psu": "550", "ports": "HDMI 2.1, 3x DisplayPort 1.4a", "power_connectors": "1x 8-pin", "performance": "25000", "vram_size": "8", "slots_required": "2"}', 
true);

-- Добавляем видеокарты AMD (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('AMD Radeon RX 7900 XTX', 'Видеокарта AMD Radeon RX 7900 XTX, 24 ГБ GDDR6, флагманская модель с высокой производительностью', 129999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "24", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1855", "boost_clock": "2499", "length": "287", "width": "123", "height": "51", "power": "355", "recommended_psu": "800", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "52000", "vram_size": "24", "slots_required": "2.5"}', 
true),

('AMD Radeon RX 7900 XT', 'Видеокарта AMD Radeon RX 7900 XT, 20 ГБ GDDR6, мощная модель для требовательных игр', 99999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "20", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1500", "boost_clock": "2394", "length": "276", "width": "123", "height": "51", "power": "300", "recommended_psu": "750", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "47000", "vram_size": "20", "slots_required": "2.5"}', 
true),

('AMD Radeon RX 7800 XT', 'Видеокарта AMD Radeon RX 7800 XT, 16 ГБ GDDR6, оптимальный выбор для игр в высоком разрешении', 64999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "16", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1295", "boost_clock": "2124", "length": "267", "width": "120", "height": "50", "power": "263", "recommended_psu": "700", "ports": "HDMI 2.1, 2x DisplayPort 2.1", "power_connectors": "2x 8-pin", "performance": "39000", "vram_size": "16", "slots_required": "2"}', 
true),

('AMD Radeon RX 6600', 'Видеокарта AMD Radeon RX 6600, 8 ГБ GDDR6, отличное решение для игр в разрешении 1080p', 26999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Видеокарты'), 
'GPU', 
'{"memory": "8", "memory_type": "GDDR6", "interface": "PCIe 4.0", "core_clock": "1626", "boost_clock": "2491", "length": "190", "width": "110", "height": "40", "power": "132", "recommended_psu": "500", "ports": "HDMI 2.1, 2x DisplayPort 1.4", "power_connectors": "1x 8-pin", "performance": "21000", "vram_size": "8", "slots_required": "2"}', 
true);

-- Добавляем оперативную память DDR5 (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Corsair Dominator Platinum RGB DDR5-6000 32GB', 'Модуль памяти Corsair Dominator Platinum RGB, DDR5-6000, 32 ГБ (2x16 ГБ)', 29999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "6000", "timings": "36-36-36-76", "voltage": "1.35", "rgb": "Yes", "performance": "12000", "height": "44"}', 
true),

('G.Skill Trident Z5 RGB DDR5-7200 32GB', 'Модуль памяти G.Skill Trident Z5 RGB, DDR5-7200, 32 ГБ (2x16 ГБ)', 32999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'G.Skill'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "7200", "timings": "34-42-42-108", "voltage": "1.4", "rgb": "Yes", "performance": "14400", "height": "44"}', 
true),

('Kingston FURY Beast DDR5-6000 32GB', 'Модуль памяти Kingston FURY Beast, DDR5-6000, 32 ГБ (2x16 ГБ)', 24999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Kingston'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "6000", "timings": "40-40-40-80", "voltage": "1.35", "rgb": "No", "performance": "12000", "height": "35"}', 
true),

('Crucial DDR5-5600 32GB', 'Модуль памяти Crucial, DDR5-5600, 32 ГБ (2x16 ГБ)', 19999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR5", "modules": "2", "frequency": "5600", "timings": "46-45-45-90", "voltage": "1.25", "rgb": "No", "performance": "11200", "height": "31"}', 
true);

-- Добавляем оперативную память DDR4 (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Corsair Vengeance RGB Pro DDR4-3600 32GB', 'Модуль памяти Corsair Vengeance RGB Pro, DDR4-3600, 32 ГБ (2x16 ГБ)', 14999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "32", "type": "DDR4", "modules": "2", "frequency": "3600", "timings": "18-22-22-42", "voltage": "1.35", "rgb": "Yes", "performance": "7200", "height": "51"}', 
true),

('G.Skill Ripjaws V DDR4-3200 16GB', 'Модуль памяти G.Skill Ripjaws V, DDR4-3200, 16 ГБ (2x8 ГБ)', 8999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'G.Skill'), 
(SELECT id FROM categories WHERE name = 'Оперативная память'), 
'RAM', 
'{"capacity": "16", "type": "DDR4", "modules": "2", "frequency": "3200", "timings": "16-18-18-38", "voltage": "1.35", "rgb": "No", "performance": "6400", "height": "42"}', 
true); 