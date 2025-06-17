-- Добавляем категории периферийных устройств
INSERT INTO categories (name, description, is_pc_component, slug, is_peripheral)
SELECT name, description, false, slug, true
FROM (VALUES
    ('Мониторы', 'Дисплеи для отображения информации', 'monitors'),
    ('Клавиатуры', 'Устройства ввода для ПК', 'keyboards'),
    ('Мыши', 'Манипуляторы для управления курсором', 'mice'),
    ('Гарнитуры', 'Наушники с микрофоном для компьютера', 'headsets'),
    ('Колонки', 'Акустические системы для ПК', 'speakers'),
    ('Web-камеры', 'Камеры для видеосвязи', 'webcams'),
    ('Принтеры', 'Устройства для печати документов', 'printers'),
    ('Сканеры', 'Устройства для оцифровки изображений', 'scanners'),
    ('Игровые контроллеры', 'Джойстики и геймпады для игр', 'gamepads'),
    ('Сетевое оборудование', 'Маршрутизаторы, коммутаторы и сетевые адаптеры', 'network'),
    ('Наушники', 'Устройства для прослушивания аудио', 'headphones'),
    ('Коврики для мыши', 'Поверхности для комфортного использования мыши', 'mousepads'),
    ('Микрофоны', 'Устройства для записи звука', 'microphones')
) AS new_categories(name, description, slug)
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE categories.name = new_categories.name
);

-- Обновляем существующие категории периферии, устанавливая флаг is_peripheral
UPDATE categories 
SET is_peripheral = true
WHERE name IN (
    'Мониторы', 'Клавиатуры', 'Мыши', 'Гарнитуры', 'Колонки', 'Web-камеры', 
    'Принтеры', 'Сканеры', 'Игровые контроллеры', 'Сетевое оборудование',
    'Наушники', 'Коврики для мыши', 'Микрофоны'
);

-- Добавляем производителей периферийных устройств
INSERT INTO manufacturers (name, description, rating) VALUES
('Logitech', 'Производитель компьютерной периферии', 4.6),
('Razer', 'Производитель игровой периферии', 4.5),
('SteelSeries', 'Производитель игровой периферии', 4.4),
('HyperX', 'Производитель игровой периферии', 4.3),
('BenQ', 'Производитель мониторов', 4.2),
('Dell', 'Производитель компьютерной техники и периферии', 4.4),
('LG', 'Производитель электроники и мониторов', 4.5),
('HP', 'Производитель компьютеров и периферии', 4.3),
('Canon', 'Производитель фототехники и принтеров', 4.6),
('Epson', 'Производитель принтеров и сканеров', 4.5)
ON CONFLICT (name) DO NOTHING;

-- Добавляем мониторы
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('LG UltraGear 27GP850-B', 'Монитор LG UltraGear 27", IPS, 2560x1440, 165 Гц, 1 мс, HDR10', 34999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'LG'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
NULL, 
'{"screen_size": "27", "resolution": "2560x1440", "panel_type": "IPS", "refresh_rate": "165", "response_time": "1", "ports": "HDMI 2.0, DisplayPort 1.4, USB 3.0", "hdr": "HDR10", "color_gamut": "sRGB 99%", "brightness": "400", "contrast": "1000:1", "speakers": "No", "vesa": "100x100", "adjustable_stand": "Yes", "gsync_freesync": "Both"}', 
true),

('Dell S2721DGF', 'Монитор Dell 27", IPS, 2560x1440, 165 Гц, 1 мс, HDR400', 32999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
NULL, 
'{"screen_size": "27", "resolution": "2560x1440", "panel_type": "IPS", "refresh_rate": "165", "response_time": "1", "ports": "HDMI 2.0, DisplayPort 1.4, USB 3.0", "hdr": "HDR400", "color_gamut": "sRGB 98%", "brightness": "400", "contrast": "1000:1", "speakers": "No", "vesa": "100x100", "adjustable_stand": "Yes", "gsync_freesync": "Both"}', 
true),

('BenQ EX2510S', 'Монитор BenQ MOBIUZ 24.5", IPS, 1920x1080, 165 Гц, 1 мс, HDR10', 21999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'BenQ'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
NULL, 
'{"screen_size": "24.5", "resolution": "1920x1080", "panel_type": "IPS", "refresh_rate": "165", "response_time": "1", "ports": "HDMI 2.0, DisplayPort 1.2", "hdr": "HDR10", "color_gamut": "sRGB 99%", "brightness": "400", "contrast": "1000:1", "speakers": "Yes", "vesa": "100x100", "adjustable_stand": "Yes", "gsync_freesync": "FreeSync"}', 
true),

('LG 34WP65C-B', 'Монитор LG 34" Curved, VA, 3440x1440, 160 Гц, 1 мс, HDR10', 39999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'LG'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
NULL, 
'{"screen_size": "34", "resolution": "3440x1440", "panel_type": "VA", "refresh_rate": "160", "response_time": "1", "ports": "HDMI 2.0, DisplayPort 1.4, USB 3.0", "hdr": "HDR10", "color_gamut": "sRGB 99%", "brightness": "300", "contrast": "3000:1", "speakers": "Yes", "vesa": "100x100", "adjustable_stand": "Yes", "gsync_freesync": "FreeSync", "curve": "1800R"}', 
true);

-- Добавляем клавиатуры
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech G Pro X', 'Клавиатура Logitech G Pro X механическая, TKL, сменные переключатели', 11999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
NULL, 
'{"type": "Mechanical", "layout": "TKL", "switches": "GX Clicky Blue", "hot_swappable": "Yes", "rgb": "Yes", "connection": "USB-C", "wireless": "No", "battery_life": "N/A", "macro_keys": "No", "media_controls": "Yes", "wrist_rest": "No", "anti_ghosting": "Yes", "nkro": "Yes", "water_resistant": "No"}', 
true),

('Razer BlackWidow V3', 'Клавиатура Razer BlackWidow V3 механическая, полноразмерная, Razer Green', 13999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
NULL, 
'{"type": "Mechanical", "layout": "Full Size", "switches": "Razer Green", "hot_swappable": "No", "rgb": "Yes", "connection": "USB", "wireless": "No", "battery_life": "N/A", "macro_keys": "No", "media_controls": "Yes", "wrist_rest": "Yes", "anti_ghosting": "Yes", "nkro": "Yes", "water_resistant": "No"}', 
true),

('SteelSeries Apex Pro', 'Клавиатура SteelSeries Apex Pro механическая, полноразмерная, регулируемый actuation point', 17999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
NULL, 
'{"type": "Mechanical", "layout": "Full Size", "switches": "OmniPoint Adjustable", "hot_swappable": "No", "rgb": "Yes", "connection": "USB", "wireless": "No", "battery_life": "N/A", "macro_keys": "No", "media_controls": "Yes", "wrist_rest": "Yes", "anti_ghosting": "Yes", "nkro": "Yes", "water_resistant": "No", "special_features": "OLED Smart Display, Adjustable actuation point 0.4-3.6mm"}', 
true),

('HyperX Alloy Origins Core', 'Клавиатура HyperX Alloy Origins Core механическая, TKL, HyperX Red', 8999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
NULL, 
'{"type": "Mechanical", "layout": "TKL", "switches": "HyperX Red", "hot_swappable": "No", "rgb": "Yes", "connection": "USB-C", "wireless": "No", "battery_life": "N/A", "macro_keys": "No", "media_controls": "No", "wrist_rest": "No", "anti_ghosting": "Yes", "nkro": "Yes", "water_resistant": "No", "special_features": "Aircraft-grade aluminum body"}', 
true);

-- Добавляем мыши
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech G Pro X Superlight', 'Мышь Logitech G Pro X Superlight игровая беспроводная, 63 г', 12999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
NULL, 
'{"type": "Gaming", "sensor": "HERO 25K", "dpi": "25600", "polling_rate": "1000", "buttons": "5", "weight": "63", "rgb": "No", "connection": "Wireless", "battery_life": "70", "grip_style": "All", "hand_orientation": "Right", "programmable": "Yes", "shape": "Ambidextrous", "special_features": "PTFE feet, Ultralight"}', 
true),

('Razer DeathAdder V2', 'Мышь Razer DeathAdder V2 игровая, 82 г, проводная', 5999.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
NULL, 
'{"type": "Gaming", "sensor": "Focus+ 20K", "dpi": "20000", "polling_rate": "1000", "buttons": "8", "weight": "82", "rgb": "Yes", "connection": "Wired", "battery_life": "N/A", "grip_style": "Palm", "hand_orientation": "Right", "programmable": "Yes", "shape": "Ergonomic", "special_features": "PTFE feet, Speedflex Cable"}', 
true),

('SteelSeries Aerox 3 Wireless', 'Мышь SteelSeries Aerox 3 Wireless игровая, 66 г, перфорированный корпус', 9999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
NULL, 
'{"type": "Gaming", "sensor": "TrueMove Air", "dpi": "18000", "polling_rate": "1000", "buttons": "6", "weight": "66", "rgb": "Yes", "connection": "Wireless/Bluetooth", "battery_life": "200", "grip_style": "Claw/Fingertip", "hand_orientation": "Right", "programmable": "Yes", "shape": "Ambidextrous", "special_features": "IP54 Water Resistant, Honeycomb Design"}', 
true),

('HyperX Pulsefire Haste', 'Мышь HyperX Pulsefire Haste игровая, 59 г, перфорированный корпус', 4999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
NULL, 
'{"type": "Gaming", "sensor": "PixArt PAW3335", "dpi": "16000", "polling_rate": "1000", "buttons": "6", "weight": "59", "rgb": "Yes", "connection": "Wired", "battery_life": "N/A", "grip_style": "Claw/Fingertip", "hand_orientation": "Ambidextrous", "programmable": "Yes", "shape": "Ambidextrous", "special_features": "Honeycomb Design, PTFE feet"}', 
true);

-- Добавляем гарнитуры
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('HyperX Cloud II', 'Гарнитура HyperX Cloud II игровая, виртуальный 7.1, красная', 7999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
NULL, 
'{"type": "Gaming", "sound": "Stereo", "virtual_surround": "7.1", "driver_size": "53", "frequency_response": "15-25000", "microphone": "Detachable", "microphone_noise_cancelling": "Yes", "connection": "3.5mm/USB", "wireless": "No", "battery_life": "N/A", "impedance": "60", "weight": "320", "rgb": "No", "special_features": "Memory foam ear pads, Aluminum frame"}', 
true),

('Logitech G Pro X', 'Гарнитура Logitech G Pro X игровая, с Blue VO!CE технологией микрофона', 11999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
NULL, 
'{"type": "Gaming", "sound": "Stereo", "virtual_surround": "7.1", "driver_size": "50", "frequency_response": "20-20000", "microphone": "Detachable", "microphone_noise_cancelling": "Yes", "connection": "3.5mm/USB", "wireless": "No", "battery_life": "N/A", "impedance": "35", "weight": "320", "rgb": "No", "special_features": "Blue VO!CE microphone technology, DTS Headphone:X 2.0"}', 
true),

('SteelSeries Arctis 7', 'Гарнитура SteelSeries Arctis 7 беспроводная игровая, белая', 14999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
NULL, 
'{"type": "Gaming", "sound": "Stereo", "virtual_surround": "7.1", "driver_size": "40", "frequency_response": "20-20000", "microphone": "Retractable", "microphone_noise_cancelling": "Yes", "connection": "Wireless/3.5mm", "wireless": "Yes", "battery_life": "24", "impedance": "32", "weight": "354", "rgb": "No", "special_features": "Discord-certified ClearCast microphone, Ski Goggle Headband"}', 
true),

('Razer BlackShark V2 Pro', 'Гарнитура Razer BlackShark V2 Pro беспроводная игровая, черная', 15999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
NULL, 
'{"type": "Gaming", "sound": "Stereo", "virtual_surround": "THX Spatial Audio", "driver_size": "50", "frequency_response": "12-28000", "microphone": "Detachable", "microphone_noise_cancelling": "Yes", "connection": "Wireless/3.5mm", "wireless": "Yes", "battery_life": "24", "impedance": "32", "weight": "320", "rgb": "No", "special_features": "Razer TriForce Titanium 50mm Drivers, FlowKnit Memory Foam Ear Cushions"}', 
true);

-- Добавляем принтеры
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Canon PIXMA TS8350', 'МФУ Canon PIXMA TS8350, цветная струйная печать, сканер, копир', 15999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Canon'), 
(SELECT id FROM categories WHERE name = 'Принтеры'), 
NULL, 
'{"type": "MFP", "print_technology": "Inkjet", "color": "Yes", "print_speed": "15", "resolution": "4800x1200", "duplex": "Yes", "scanner": "Yes", "scanner_resolution": "1200x2400", "connection": "USB, Wi-Fi, Bluetooth", "network": "Yes", "airprint": "Yes", "cloud_print": "Yes", "lcd": "Yes", "paper_size": "A4", "paper_capacity": "200"}', 
true),

('Epson EcoTank L3250', 'МФУ Epson EcoTank L3250, цветная струйная печать, сканер, копир, СНПЧ', 19999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Epson'), 
(SELECT id FROM categories WHERE name = 'Принтеры'), 
NULL, 
'{"type": "MFP", "print_technology": "Inkjet", "color": "Yes", "print_speed": "10", "resolution": "5760x1440", "duplex": "No", "scanner": "Yes", "scanner_resolution": "1200x2400", "connection": "USB, Wi-Fi", "network": "Yes", "airprint": "Yes", "cloud_print": "Yes", "lcd": "Yes", "paper_size": "A4", "paper_capacity": "100", "special_features": "EcoTank (CISS)"}', 
true),

('HP LaserJet Pro M404dn', 'Принтер HP LaserJet Pro M404dn, черно-белая лазерная печать', 24999.99, 8, 
(SELECT id FROM manufacturers WHERE name = 'HP'), 
(SELECT id FROM categories WHERE name = 'Принтеры'), 
NULL, 
'{"type": "Printer", "print_technology": "Laser", "color": "No", "print_speed": "38", "resolution": "1200x1200", "duplex": "Yes", "scanner": "No", "connection": "USB, Ethernet", "network": "Yes", "airprint": "Yes", "cloud_print": "Yes", "lcd": "Yes", "paper_size": "A4", "paper_capacity": "250"}', 
true);

-- Добавляем колонки
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech Z623', 'Акустическая система Logitech Z623, 2.1, 200 Вт', 9999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Колонки'), 
NULL, 
'{"type": "2.1", "power": "200", "frequency_response": "35-20000", "connectivity": "3.5mm, RCA", "subwoofer": "Yes", "subwoofer_size": "7", "control": "Physical", "bluetooth": "No", "remote": "No", "headphone_jack": "Yes", "special_features": "THX Certified"}', 
true),

('Razer Nommo Pro', 'Акустическая система Razer Nommo Pro, 2.1, 420 Вт, Chroma RGB', 29999.99, 5, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Колонки'), 
NULL, 
'{"type": "2.1", "power": "420", "frequency_response": "20-20000", "connectivity": "USB, Bluetooth, Optical", "subwoofer": "Yes", "subwoofer_size": "6", "control": "Remote", "bluetooth": "Yes", "remote": "Yes", "headphone_jack": "Yes", "special_features": "Chroma RGB lighting, Dolby Virtual Surround Sound"}', 
true);

-- Добавляем наушники
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('HyperX Cloud Alpha', 'Наушники HyperX Cloud Alpha игровые, черно-красные', 6999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Наушники'), 
NULL, 
'{"type": "Gaming", "driver_size": "50", "frequency_response": "13-27000", "connection": "3.5mm", "impedance": "65", "weight": "298", "noise_cancellation": "Passive", "wireless": "No", "battery_life": "N/A", "foldable": "No", "special_features": "Dual Chamber Drivers, Detachable cable"}', 
true),

('Razer Kraken X', 'Наушники Razer Kraken X игровые, черные', 4999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Наушники'), 
NULL, 
'{"type": "Gaming", "driver_size": "40", "frequency_response": "12-28000", "connection": "3.5mm", "impedance": "32", "weight": "250", "noise_cancellation": "Passive", "wireless": "No", "battery_life": "N/A", "foldable": "No", "special_features": "7.1 Surround Sound, Ultra-lightweight design"}', 
true);

-- Добавляем коврики для мыши
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('SteelSeries QcK Edge', 'Коврик SteelSeries QcK Edge игровой, тканевый, размер XL', 1999.99, 40, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Коврики для мыши'), 
NULL, 
'{"type": "Gaming", "material": "Cloth", "size": "XL (900x300mm)", "thickness": "2", "rgb": "No", "surface": "Micro-woven", "base": "Non-slip rubber", "special_features": "Reinforced stitched edges"}', 
true),

('Razer Goliathus Chroma', 'Коврик Razer Goliathus Chroma игровой, тканевый, с RGB-подсветкой', 3999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Коврики для мыши'), 
NULL, 
'{"type": "Gaming", "material": "Cloth", "size": "Medium (355x255mm)", "thickness": "3", "rgb": "Yes", "surface": "Micro-textured", "base": "Non-slip rubber", "special_features": "Razer Chroma RGB lighting, Cable management"}', 
true);

-- Добавляем микрофоны
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('HyperX QuadCast', 'Микрофон HyperX QuadCast конденсаторный, для стриминга', 9999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Микрофоны'), 
NULL, 
'{"type": "Condenser", "pattern": "Multiple (Cardioid, Omnidirectional, Bidirectional, Stereo)", "frequency_response": "20-20000", "connection": "USB", "sample_rate": "48", "bit_depth": "16", "mount": "Shock mount included", "gain_control": "Yes", "mute_button": "Yes", "monitoring": "Yes", "special_features": "RGB lighting, Anti-vibration shock mount"}', 
true),

('Razer Seiren X', 'Микрофон Razer Seiren X конденсаторный, для стриминга', 7999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Микрофоны'), 
NULL, 
'{"type": "Condenser", "pattern": "Supercardioid", "frequency_response": "20-20000", "connection": "USB", "sample_rate": "48", "bit_depth": "16", "mount": "Built-in", "gain_control": "Yes", "mute_button": "Yes", "monitoring": "Yes", "special_features": "Built-in shock mount, Compact design"}', 
true);

-- Добавляем сетевое оборудование
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('TP-Link Archer AX50', 'Маршрутизатор TP-Link Archer AX50 Wi-Fi 6, двухдиапазонный', 7999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), -- Используем Dell как временного производителя
(SELECT id FROM categories WHERE name = 'Сетевое оборудование'), 
NULL, 
'{"type": "Router", "wifi_standard": "Wi-Fi 6 (802.11ax)", "bands": "Dual-band", "max_speed": "3000", "ports": "4x Gigabit LAN, 1x Gigabit WAN, 1x USB 3.0", "antennas": "4", "processor": "Dual-core", "security": "WPA3", "special_features": "OFDMA, MU-MIMO, Beamforming"}', 
true),

('Netgear Nighthawk XR1000', 'Маршрутизатор Netgear Nighthawk XR1000 игровой, Wi-Fi 6', 14999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), -- Используем Dell как временного производителя
(SELECT id FROM categories WHERE name = 'Сетевое оборудование'), 
NULL, 
'{"type": "Gaming Router", "wifi_standard": "Wi-Fi 6 (802.11ax)", "bands": "Dual-band", "max_speed": "5400", "ports": "4x Gigabit LAN, 1x Gigabit WAN, 1x USB 3.0", "antennas": "4", "processor": "Triple-core 1.5GHz", "security": "WPA3", "special_features": "DumaOS 3.0, Geo-Filter, QoS, Gaming Dashboard"}', 
true);

-- Добавляем игровые контроллеры
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Xbox Wireless Controller', 'Контроллер Xbox Series X|S беспроводной, черный', 5999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), -- Используем Dell как временного производителя
(SELECT id FROM categories WHERE name = 'Игровые контроллеры'), 
NULL, 
'{"type": "Gamepad", "platform": "Xbox/PC", "connection": "Bluetooth/USB-C", "wireless": "Yes", "battery_type": "AA Batteries", "battery_life": "40", "vibration": "Yes", "buttons": "17", "analog_sticks": "2", "special_features": "Share button, Hybrid D-pad, Textured grip"}', 
true),

('DualSense Wireless Controller', 'Контроллер Sony DualSense для PlayStation 5, белый', 6999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), -- Используем Dell как временного производителя
(SELECT id FROM categories WHERE name = 'Игровые контроллеры'), 
NULL, 
'{"type": "Gamepad", "platform": "PlayStation 5/PC", "connection": "Bluetooth/USB-C", "wireless": "Yes", "battery_type": "Rechargeable", "battery_life": "12", "vibration": "Yes", "buttons": "14", "analog_sticks": "2", "special_features": "Haptic feedback, Adaptive triggers, Built-in microphone"}', 
true); 