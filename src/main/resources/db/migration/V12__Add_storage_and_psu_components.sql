-- Добавляем SSD накопители
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Samsung 990 PRO 2TB', 'SSD накопитель Samsung 990 PRO, 2 ТБ, M.2 NVMe PCIe 4.0, высокоскоростной', 19999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "2000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "7450", "write_speed": "6900", "tlc": "Yes", "dram": "Yes", "endurance": "1200", "performance": "7450"}', 
true),

('Western Digital Black SN850X 1TB', 'SSD накопитель WD Black SN850X, 1 ТБ, M.2 NVMe PCIe 4.0, для геймеров', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Western Digital'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "7300", "write_speed": "6300", "tlc": "Yes", "dram": "Yes", "endurance": "600", "performance": "7300"}', 
true),

('Crucial P5 Plus 2TB', 'SSD накопитель Crucial P5 Plus, 2 ТБ, M.2 NVMe PCIe 4.0, высокая производительность', 17999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Crucial'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "2000", "type": "SSD", "form_factor": "M.2", "interface": "NVMe PCIe 4.0", "read_speed": "6600", "write_speed": "5000", "tlc": "Yes", "dram": "Yes", "endurance": "1200", "performance": "6600"}', 
true),

('Samsung 870 EVO 1TB', 'SSD накопитель Samsung 870 EVO, 1 ТБ, 2.5" SATA III, надежный и быстрый', 8999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "1000", "type": "SSD", "form_factor": "2.5 inch", "interface": "SATA III", "read_speed": "560", "write_speed": "530", "tlc": "Yes", "dram": "Yes", "endurance": "600", "performance": "560"}', 
true),

('Seagate Barracuda 4TB', 'Жесткий диск Seagate Barracuda, 4 ТБ, 3.5" SATA III, для хранения данных', 7499.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'Seagate'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "4000", "type": "HDD", "form_factor": "3.5 inch", "interface": "SATA III", "read_speed": "190", "write_speed": "180", "rpm": "5400", "cache": "256", "performance": "190"}', 
true),

('Western Digital Blue 2TB', 'Жесткий диск Western Digital Blue, 2 ТБ, 3.5" SATA III, надежное хранилище', 4999.99, 45, 
(SELECT id FROM manufacturers WHERE name = 'Western Digital'), 
(SELECT id FROM categories WHERE name = 'Накопители'), 
'STORAGE', 
'{"capacity": "2000", "type": "HDD", "form_factor": "3.5 inch", "interface": "SATA III", "read_speed": "175", "write_speed": "170", "rpm": "5400", "cache": "256", "performance": "175"}', 
true);

-- Добавляем блоки питания
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Seasonic PRIME TX-1000', 'Блок питания Seasonic PRIME TX-1000, 1000 Вт, 80 PLUS Titanium, модульный', 24999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Seasonic'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "1000", "efficiency": "80 PLUS Titanium", "modular": "Full", "fan_size": "135", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "95"}', 
true),

('be quiet! Dark Power Pro 12 850W', 'Блок питания be quiet! Dark Power Pro 12, 850 Вт, 80 PLUS Titanium, модульный', 22999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "850", "efficiency": "80 PLUS Titanium", "modular": "Full", "fan_size": "135", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "94"}', 
true),

('Corsair RM850x', 'Блок питания Corsair RM850x, 850 Вт, 80 PLUS Gold, модульный', 15999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "850", "efficiency": "80 PLUS Gold", "modular": "Full", "fan_size": "135", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "90"}', 
true),

('EVGA SuperNOVA 750 G6', 'Блок питания EVGA SuperNOVA 750 G6, 750 Вт, 80 PLUS Gold, модульный', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'EVGA'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "750", "efficiency": "80 PLUS Gold", "modular": "Full", "fan_size": "135", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "89"}', 
true),

('Corsair RM650', 'Блок питания Corsair RM650, 650 Вт, 80 PLUS Gold, модульный', 9999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "650", "efficiency": "80 PLUS Gold", "modular": "Full", "fan_size": "135", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "88"}', 
true),

('be quiet! Pure Power 11 FM 650W', 'Блок питания be quiet! Pure Power 11 FM, 650 Вт, 80 PLUS Gold, модульный', 8999.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'be quiet!'), 
(SELECT id FROM categories WHERE name = 'Блоки питания'), 
'PSU', 
'{"wattage": "650", "efficiency": "80 PLUS Gold", "modular": "Full", "fan_size": "120", "connectors": "24-pin ATX, 8-pin CPU, 4+4-pin CPU, 6+2-pin PCIe, SATA, Molex", "protections": "OPP, OVP, UVP, OCP, OTP, SCP", "performance": "87"}', 
true); 