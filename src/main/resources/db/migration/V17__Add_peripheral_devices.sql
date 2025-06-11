-- Добавляем категории периферийных устройств
INSERT INTO categories (name, description, is_pc_component, slug) VALUES
('Мониторы', 'Дисплеи для отображения информации', false, 'monitors'),
('Клавиатуры', 'Устройства ввода для ПК', false, 'keyboards'),
('Мыши', 'Манипуляторы для управления курсором', false, 'mice'),
('Гарнитуры', 'Наушники с микрофоном для компьютера', false, 'headsets'),
('Колонки', 'Акустические системы для ПК', false, 'speakers'),
('Web-камеры', 'Камеры для видеосвязи', false, 'webcams'),
('Принтеры', 'Устройства для печати документов', false, 'printers'),
('Сканеры', 'Устройства для оцифровки изображений', false, 'scanners'),
('Игровые контроллеры', 'Джойстики и геймпады для игр', false, 'gamepads'),
('Сетевое оборудование', 'Маршрутизаторы, коммутаторы и сетевые адаптеры', false, 'network');

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