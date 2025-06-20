-- Добавляем мониторы (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('LG 27GP950-B', 'Монитор LG 27GP950-B, 27", 4K UHD, 144 Гц, IPS, HDR600, для игр', 54999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'LG'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
'MONITOR', 
'{"size": "27", "resolution": "3840x2160", "refresh_rate": "144", "panel": "IPS", "response_time": "1", "hdr": "HDR600", "ports": "HDMI 2.1, DisplayPort 1.4, USB-C", "performance": "8640", "vesa": "100x100"}', 
true),

('BenQ ZOWIE XL2546K', 'Монитор BenQ ZOWIE XL2546K, 24.5", Full HD, 240 Гц, TN, для киберспорта', 42999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'BenQ'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
'MONITOR', 
'{"size": "24.5", "resolution": "1920x1080", "refresh_rate": "240", "panel": "TN", "response_time": "1", "hdr": "No", "ports": "HDMI 2.0, DisplayPort 1.2, DVI-DL", "performance": "4608", "vesa": "100x100"}', 
true),

('Dell S2721DGF', 'Монитор Dell S2721DGF, 27", QHD, 165 Гц, IPS, FreeSync Premium Pro', 29999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Dell'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
'MONITOR', 
'{"size": "27", "resolution": "2560x1440", "refresh_rate": "165", "panel": "IPS", "response_time": "1", "hdr": "HDR400", "ports": "HDMI 2.0, DisplayPort 1.4", "performance": "4224", "vesa": "100x100"}', 
true),

('Samsung Odyssey G5 C27G55T', 'Монитор Samsung Odyssey G5 C27G55T, 27", QHD, 144 Гц, VA, изогнутый', 24999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'Samsung'), 
(SELECT id FROM categories WHERE name = 'Мониторы'), 
'MONITOR', 
'{"size": "27", "resolution": "2560x1440", "refresh_rate": "144", "panel": "VA", "response_time": "1", "hdr": "HDR10", "ports": "HDMI 2.0, DisplayPort 1.2", "performance": "3686", "vesa": "75x75"}', 
true);

-- Добавляем клавиатуры (3 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech G915 TKL', 'Клавиатура Logitech G915 TKL, механическая, беспроводная, RGB, низкопрофильная', 19999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
'KEYBOARD', 
'{"type": "Mechanical", "connection": "Wireless", "switches": "GL Tactile", "rgb": "Yes", "layout": "TKL", "battery": "40 hours", "performance": "1000"}', 
true),

('Razer BlackWidow V3 Pro', 'Клавиатура Razer BlackWidow V3 Pro, механическая, беспроводная, RGB, полноразмерная', 22999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
'KEYBOARD', 
'{"type": "Mechanical", "connection": "Wireless", "switches": "Razer Green", "rgb": "Yes", "layout": "Full Size", "battery": "200 hours", "performance": "1000"}', 
true),

('SteelSeries Apex Pro', 'Клавиатура SteelSeries Apex Pro, механическая, проводная, RGB, регулируемые переключатели', 17999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Клавиатуры'), 
'KEYBOARD', 
'{"type": "Mechanical", "connection": "Wired", "switches": "OmniPoint Adjustable", "rgb": "Yes", "layout": "Full Size", "battery": "N/A", "performance": "1000"}', 
true);

-- Добавляем мыши (4 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech G Pro X Superlight', 'Мышь Logitech G Pro X Superlight, беспроводная, игровая, 25600 DPI, 63г', 12999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
'MOUSE', 
'{"type": "Gaming", "connection": "Wireless", "dpi": "25600", "buttons": "5", "weight": "63", "battery": "70 hours", "performance": "25600"}', 
true),

('Razer DeathAdder V3 Pro', 'Мышь Razer DeathAdder V3 Pro, беспроводная, игровая, 30000 DPI, эргономичная', 14999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
'MOUSE', 
'{"type": "Gaming", "connection": "Wireless", "dpi": "30000", "buttons": "8", "weight": "88", "battery": "90 hours", "performance": "30000"}', 
true),

('SteelSeries Rival 650 Wireless', 'Мышь SteelSeries Rival 650 Wireless, беспроводная, игровая, 12000 DPI, настраиваемый вес', 11999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
'MOUSE', 
'{"type": "Gaming", "connection": "Wireless", "dpi": "12000", "buttons": "7", "weight": "121", "battery": "24 hours", "performance": "12000"}', 
true),

('Logitech MX Master 3S', 'Мышь Logitech MX Master 3S, беспроводная, офисная, 8000 DPI, тихие клики', 8999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Мыши'), 
'MOUSE', 
'{"type": "Office", "connection": "Wireless", "dpi": "8000", "buttons": "7", "weight": "141", "battery": "70 days", "performance": "8000"}', 
true);

-- Добавляем гарнитуры (3 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('SteelSeries Arctis Pro Wireless', 'Гарнитура SteelSeries Arctis Pro Wireless, беспроводная, Hi-Res Audio, 2.4G + Bluetooth', 29999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
'HEADSET', 
'{"type": "Gaming", "connection": "Wireless", "frequency": "10-40000", "impedance": "32", "microphone": "Retractable", "battery": "20 hours", "performance": "40000"}', 
true),

('HyperX Cloud Alpha Wireless', 'Гарнитура HyperX Cloud Alpha Wireless, беспроводная, игровая, 300 часов работы', 19999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
'HEADSET', 
'{"type": "Gaming", "connection": "Wireless", "frequency": "15-21000", "impedance": "62", "microphone": "Detachable", "battery": "300 hours", "performance": "21000"}', 
true),

('Razer Kraken V3 Pro', 'Гарнитура Razer Kraken V3 Pro, беспроводная, игровая, THX Spatial Audio, RGB', 24999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Гарнитуры'), 
'HEADSET', 
'{"type": "Gaming", "connection": "Wireless", "frequency": "12-28000", "impedance": "32", "microphone": "Retractable", "battery": "50 hours", "performance": "28000"}', 
true);

-- Добавляем колонки (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech Z623', 'Колонки Logitech Z623, 2.1, 200 Вт RMS, THX сертифицированы', 9999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Колонки'), 
'SPEAKERS', 
'{"type": "2.1", "power": "200", "frequency": "35-20000", "thx": "Yes", "inputs": "3.5mm, RCA", "performance": "200"}', 
true),

('Razer Nommo Pro', 'Колонки Razer Nommo Pro, 2.1, THX сертифицированы, RGB подсветка', 49999.99, 8, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Колонки'), 
'SPEAKERS', 
'{"type": "2.1", "power": "400", "frequency": "35-20000", "thx": "Yes", "inputs": "USB, Optical, Bluetooth", "performance": "400"}', 
true);

-- Добавляем веб-камеры (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Logitech Brio 4K', 'Веб-камера Logitech Brio 4K, Ultra HD, автофокус, HDR, Windows Hello', 19999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Web-камеры'), 
'WEBCAM', 
'{"resolution": "4K", "fps": "30", "autofocus": "Yes", "hdr": "Yes", "microphone": "Dual stereo", "performance": "2160"}', 
true),

('Razer Kiyo Pro', 'Веб-камера Razer Kiyo Pro, Full HD, адаптивный датчик освещения, автофокус', 14999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Web-камеры'), 
'WEBCAM', 
'{"resolution": "1080p", "fps": "60", "autofocus": "Yes", "hdr": "No", "microphone": "Built-in", "performance": "1080"}', 
true);

-- Добавляем принтеры (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Canon PIXMA G6040', 'Принтер Canon PIXMA G6040, струйный, цветной, Wi-Fi, система непрерывной подачи чернил', 24999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'Canon'), 
(SELECT id FROM categories WHERE name = 'Принтеры'), 
'PRINTER', 
'{"type": "Inkjet", "color": "Yes", "wifi": "Yes", "duplex": "Yes", "speed_bw": "13", "speed_color": "6.8", "performance": "13"}', 
true),

('HP LaserJet Pro M404dn', 'Принтер HP LaserJet Pro M404dn, лазерный, монохромный, дуплекс, сетевой', 19999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'HP'), 
(SELECT id FROM categories WHERE name = 'Принтеры'), 
'PRINTER', 
'{"type": "Laser", "color": "No", "wifi": "No", "duplex": "Yes", "speed_bw": "38", "speed_color": "0", "performance": "38"}', 
true);

-- Добавляем сканеры (1 продукт)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Epson Perfection V600 Photo', 'Сканер Epson Perfection V600 Photo, планшетный, 6400x9600 dpi, для фото и документов', 14999.99, 8, 
(SELECT id FROM manufacturers WHERE name = 'Epson'), 
(SELECT id FROM categories WHERE name = 'Сканеры'), 
'SCANNER', 
'{"type": "Flatbed", "resolution": "6400x9600", "color_depth": "48", "scan_speed": "12", "usb": "Yes", "performance": "6400"}', 
true);

-- Добавляем игровые контроллеры (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('Razer Wolverine V2 Pro', 'Геймпад Razer Wolverine V2 Pro, беспроводной, для Xbox и ПК, настраиваемые кнопки', 17999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Игровые контроллеры'), 
'GAMEPAD', 
'{"type": "Wireless", "platform": "Xbox/PC", "buttons": "Customizable", "battery": "28 hours", "rgb": "Yes", "performance": "1000"}', 
true),

('Logitech F710', 'Геймпад Logitech F710, беспроводной, для ПК, вибрация, двойные аналоговые стики', 4999.99, 20, 
(SELECT id FROM manufacturers WHERE name = 'Logitech'), 
(SELECT id FROM categories WHERE name = 'Игровые контроллеры'), 
'GAMEPAD', 
'{"type": "Wireless", "platform": "PC", "buttons": "Standard", "battery": "N/A", "rgb": "No", "performance": "100"}', 
true);

-- Добавляем сетевое оборудование (1 продукт)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('ASUS AX6000', 'Маршрутизатор ASUS AX6000, Wi-Fi 6, двухдиапазонный, 8 антенн, игровые функции', 24999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'ASUS'), 
(SELECT id FROM categories WHERE name = 'Сетевое оборудование'), 
'NETWORK', 
'{"type": "Router", "wifi": "Wi-Fi 6", "speed": "6000", "antennas": "8", "ports": "4x Gigabit LAN", "performance": "6000"}', 
true);

-- Добавляем наушники (2 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('SteelSeries Arctis 7P+', 'Наушники SteelSeries Arctis 7P+, беспроводные, игровые, 30 часов работы', 16999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Наушники'), 
'HEADPHONES', 
'{"type": "Gaming", "connection": "Wireless", "frequency": "20-20000", "impedance": "32", "battery": "30 hours", "performance": "20000"}', 
true),

('HyperX Cloud Flight S', 'Наушники HyperX Cloud Flight S, беспроводные, игровые, Qi беспроводная зарядка', 14999.99, 18, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Наушники'), 
'HEADPHONES', 
'{"type": "Gaming", "connection": "Wireless", "frequency": "15-23000", "impedance": "32", "battery": "30 hours", "performance": "23000"}', 
true);

-- Добавляем коврики для мыши (3 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('SteelSeries QcK Heavy', 'Коврик для мыши SteelSeries QcK Heavy, тканевый, 450x400x6 мм, толстый', 2999.99, 30, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Коврики для мыши'), 
'MOUSEPAD', 
'{"material": "Cloth", "size": "450x400", "thickness": "6", "surface": "Smooth", "base": "Rubber", "performance": "400"}', 
true),

('Razer Goliathus Extended Chroma', 'Коврик для мыши Razer Goliathus Extended Chroma, RGB, 920x294x3 мм, расширенный', 5999.99, 25, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Коврики для мыши'), 
'MOUSEPAD', 
'{"material": "Cloth", "size": "920x294", "thickness": "3", "surface": "Speed", "base": "Rubber", "performance": "920"}', 
true),

('Corsair MM300 Extended', 'Коврик для мыши Corsair MM300 Extended, тканевый, 930x300x3 мм, расширенный размер', 3999.99, 35, 
(SELECT id FROM manufacturers WHERE name = 'Corsair'), 
(SELECT id FROM categories WHERE name = 'Коврики для мыши'), 
'MOUSEPAD', 
'{"material": "Cloth", "size": "930x300", "thickness": "3", "surface": "Speed", "base": "Rubber", "performance": "930"}', 
true);

-- Добавляем микрофоны (3 продукта)
INSERT INTO products (title, description, price, stock, manufacturer_id, category_id, component_type, specs, is_active) 
VALUES 
('HyperX QuadCast S', 'Микрофон HyperX QuadCast S, USB, RGB подсветка, антивибрационное крепление', 14999.99, 15, 
(SELECT id FROM manufacturers WHERE name = 'HyperX'), 
(SELECT id FROM categories WHERE name = 'Микрофоны'), 
'MICROPHONE', 
'{"type": "USB", "pattern": "Cardioid", "frequency": "20-20000", "rgb": "Yes", "shock_mount": "Yes", "performance": "20000"}', 
true),

('Razer Seiren V2 Pro', 'Микрофон Razer Seiren V2 Pro, USB, профессиональный, фокусирующий капсюль', 19999.99, 12, 
(SELECT id FROM manufacturers WHERE name = 'Razer'), 
(SELECT id FROM categories WHERE name = 'Микрофоны'), 
'MICROPHONE', 
'{"type": "USB", "pattern": "Supercardioid", "frequency": "20-20000", "rgb": "No", "shock_mount": "Yes", "performance": "20000"}', 
true),

('SteelSeries Alias', 'Микрофон SteelSeries Alias, XLR/USB, студийное качество, шумоподавление', 24999.99, 10, 
(SELECT id FROM manufacturers WHERE name = 'SteelSeries'), 
(SELECT id FROM categories WHERE name = 'Микрофоны'), 
'MICROPHONE', 
'{"type": "XLR/USB", "pattern": "Cardioid", "frequency": "20-20000", "rgb": "No", "shock_mount": "Yes", "performance": "20000"}', 
true); 