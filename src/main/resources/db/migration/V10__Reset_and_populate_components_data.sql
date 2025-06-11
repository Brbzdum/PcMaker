-- Сбрасываем и заполняем данные компонентов ПК
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
('G.Skill', 'Производитель оперативной памяти', 4.5);

-- Заполняем таблицу категорий
INSERT INTO categories (name, description, is_pc_component, slug) VALUES
('Процессоры', 'Центральные процессоры (CPU)', true, 'processors'),
('Видеокарты', 'Графические процессоры (GPU)', true, 'graphic-cards'),
('Материнские платы', 'Основные платы компьютера', true, 'motherboards'),
('Оперативная память', 'Модули RAM', true, 'ram'),
('Накопители', 'SSD и HDD накопители', true, 'storage'),
('Блоки питания', 'Источники питания компьютера', true, 'power-supplies'),
('Корпуса', 'Корпуса для ПК', true, 'cases'),
('Системы охлаждения', 'Кулеры и системы жидкостного охлаждения', true, 'cooling');

-- Заполняем базовые правила совместимости
INSERT INTO compatibility_rules (source_type, target_type, rule_type, source_property, target_property, comparison_operator, description)
VALUES
('CPU', 'MB', 'EXACT_MATCH', 'socket', 'socket', 'EQUALS', 'Сокет процессора должен совпадать с сокетом материнской платы'),
('RAM', 'MB', 'EXACT_MATCH', 'type', 'ram_type', 'EQUALS', 'Тип оперативной памяти должен совпадать с поддерживаемым типом на материнской плате'),
('RAM', 'MB', 'RANGE_CHECK', 'frequency', 'max_ram_frequency', 'LESS_THAN_EQUALS', 'Частота оперативной памяти не должна превышать максимальную поддерживаемую частоту материнской платы'),
('GPU', 'MB', 'EXACT_MATCH', 'interface', 'pcie_version', 'EQUALS', 'Интерфейс видеокарты должен совпадать с версией PCIe на материнской плате'),
('PSU', 'MB', 'SUBSET_CHECK', 'connectors', 'required_connectors', 'CONTAINS', 'Блок питания должен иметь все необходимые разъемы для материнской платы'),
('PSU', 'GPU', 'GREATER_THAN', 'wattage', 'power', 'GREATER_THAN_EQUALS', 'Мощность блока питания должна быть не меньше требуемой мощности видеокарты'),
('CASE', 'MB', 'EXACT_MATCH', 'form_factor', 'form_factor', 'EQUALS', 'Форм-фактор корпуса должен совпадать с форм-фактором материнской платы'),
('CASE', 'GPU', 'GREATER_THAN', 'max_gpu_length', 'length', 'GREATER_THAN_EQUALS', 'Максимальная длина видеокарты, поддерживаемая корпусом, должна быть не меньше длины видеокарты');

-- Добавляем процессоры Intel
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Intel Core i9-14900K', 'Процессор Intel Core i9-14900K, 24 ядра (8P+16E), 32 потока, 6.0 ГГц макс. турбо', 59999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "24", "threads": "32", "socket": "LGA1700", "frequency": "3.2", "max_frequency": "6.0", "tdp": "125", "cache": "36", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "45000"}', 
true),

('Intel Core i7-14700K', 'Процессор Intel Core i7-14700K, 20 ядер (8P+12E), 28 потоков, 5.6 ГГц макс. турбо', 42999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "20", "threads": "28", "socket": "LGA1700", "frequency": "3.4", "max_frequency": "5.6", "tdp": "125", "cache": "33", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "39500"}', 
true),

('Intel Core i5-14600K', 'Процессор Intel Core i5-14600K, 14 ядер (6P+8E), 20 потоков, 5.3 ГГц макс. турбо', 31999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "14", "threads": "20", "socket": "LGA1700", "frequency": "3.5", "max_frequency": "5.3", "tdp": "125", "cache": "24", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "32000"}', 
true),

('Intel Core i9-13900K', 'Процессор Intel Core i9-13900K, 24 ядра (8P+16E), 32 потока, 5.8 ГГц макс. турбо', 53999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Intel'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "24", "threads": "32", "socket": "LGA1700", "frequency": "3.0", "max_frequency": "5.8", "tdp": "125", "cache": "36", "technology": "10nm", "integrated_graphics": "Intel UHD Graphics 770", "performance": "42500"}', 
true);

-- Добавляем процессоры AMD
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('AMD Ryzen 9 7950X3D', 'Процессор AMD Ryzen 9 7950X3D, 16 ядер, 32 потока, 5.7 ГГц макс. турбо, 3D V-Cache', 64999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "16", "threads": "32", "socket": "AM5", "frequency": "4.2", "max_frequency": "5.7", "tdp": "120", "cache": "144", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "46000"}', 
true),

('AMD Ryzen 9 7900X', 'Процессор AMD Ryzen 9 7900X, 12 ядер, 24 потока, 5.6 ГГц макс. турбо', 47999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "12", "threads": "24", "socket": "AM5", "frequency": "4.7", "max_frequency": "5.6", "tdp": "170", "cache": "76", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "41000"}', 
true),

('AMD Ryzen 7 7800X3D', 'Процессор AMD Ryzen 7 7800X3D, 8 ядер, 16 потоков, 5.0 ГГц макс. турбо, 3D V-Cache', 42999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "8", "threads": "16", "socket": "AM5", "frequency": "4.2", "max_frequency": "5.0", "tdp": "120", "cache": "96", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "37000"}', 
true),

('AMD Ryzen 5 7600X', 'Процессор AMD Ryzen 5 7600X, 6 ядер, 12 потоков, 5.3 ГГц макс. турбо', 27999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'AMD'), 
(SELECT id FROM categories WHERE name = 'Процессоры'), 
'CPU', 
'{"cores": "6", "threads": "12", "socket": "AM5", "frequency": "4.7", "max_frequency": "5.3", "tdp": "105", "cache": "38", "technology": "5nm", "integrated_graphics": "AMD Radeon Graphics", "performance": "30000"}', 
true);

-- Добавляем материнские платы для Intel
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('ASUS ROG Maximus Z790 Hero', 'Материнская плата ASUS ROG Maximus Z790 Hero для процессоров Intel 12-14 поколения', 49999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7800", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "12", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('MSI MPG Z790 Carbon WiFi', 'Материнская плата MSI MPG Z790 Carbon WiFi для процессоров Intel 12-14 поколения', 38999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "4", "usb_ports": "10", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('Gigabyte Z790 AORUS Elite AX', 'Материнская плата Gigabyte Z790 AORUS Elite AX для процессоров Intel 12-14 поколения', 32999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Gigabyte'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "4", "usb_ports": "12", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('ASRock Z790 PG Riptide', 'Материнская плата ASRock Z790 PG Riptide для процессоров Intel 12-14 поколения', 26999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'ASRock'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "LGA1700", "chipset": "Z790", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7200", "pcie_version": "5.0", "sata_ports": "8", "m2_slots": "4", "usb_ports": "8", "wifi": "No", "bluetooth": "No", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true);

-- Добавляем материнские платы для AMD
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('ASUS ROG Crosshair X670E Hero', 'Материнская плата ASUS ROG Crosshair X670E Hero для процессоров AMD Ryzen 7000', 55999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7800", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "12", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "10 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('MSI MEG X670E ACE', 'Материнская плата MSI MEG X670E ACE для процессоров AMD Ryzen 7000', 52999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'MSI'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "5", "usb_ports": "11", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "10 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('Gigabyte X670E AORUS Master', 'Материнская плата Gigabyte X670E AORUS Master для процессоров AMD Ryzen 7000', 47999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Gigabyte'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7600", "pcie_version": "5.0", "sata_ports": "6", "m2_slots": "4", "usb_ports": "13", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "10 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true),

('ASRock X670E Taichi', 'Материнская плата ASRock X670E Taichi для процессоров AMD Ryzen 7000', 42999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'ASRock'), 
(SELECT id FROM categories WHERE name = 'Материнские платы'), 
'MB', 
'{"socket": "AM5", "chipset": "X670E", "form_factor": "ATX", "memory_slots": "4", "max_memory": "128", "ram_type": "DDR5", "max_ram_frequency": "7000", "pcie_version": "5.0", "sata_ports": "8", "m2_slots": "4", "usb_ports": "10", "wifi": "Wi-Fi 6E", "bluetooth": "5.3", "lan": "2.5 Gigabit", "required_connectors": "24-pin ATX, 8-pin CPU"}', 
true); 