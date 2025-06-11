-- Добавляем расширенные правила совместимости для компонентов ПК

-- Правила совместимости для процессоров (CPU)
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, value_modifier, description)
VALUES
    -- Правила CPU-RAM
    ('CPU', 'RAM', 'EXACT_MATCH', 'ram_type', 'type', '=', NULL, 'Процессор должен поддерживать тип используемой RAM'),
    ('CPU', 'RAM', 'RANGE_CHECK', 'max_memory_channels', 'modules', '>=', NULL, 'Процессор должен поддерживать количество модулей памяти'),
    ('CPU', 'COOLER', 'GREATER_THAN', 'tdp', 'max_tdp', '<=', NULL, 'TDP процессора не должен превышать максимальный TDP, поддерживаемый системой охлаждения'),
    
    -- Дополнительные правила для материнских плат
    ('MB', 'CASE', 'EXACT_MATCH', 'form_factor', 'form_factor', '=', NULL, 'Форм-фактор материнской платы должен соответствовать поддерживаемому форм-фактору корпуса'),
    ('MB', 'RAM', 'EXACT_MATCH', 'ram_slots', 'modules', '>=', NULL, 'Количество слотов для RAM должно быть не меньше количества модулей памяти'),
    
    -- Проверка общей мощности для блока питания
    ('PSU', 'CPU', 'GREATER_THAN', 'wattage', 'tdp', '>=', NULL, 'Мощность блока питания должна быть достаточной для питания процессора'),
    ('PSU', 'RAM', 'GREATER_THAN', 'wattage', 'power_consumption', '>=', NULL, 'Мощность блока питания должна быть достаточной для питания памяти'),
    ('PSU', 'STORAGE', 'GREATER_THAN', 'wattage', 'power_consumption', '>=', NULL, 'Мощность блока питания должна быть достаточной для питания накопителей'),
    
    -- Проверка совместимости для накопителей
    ('STORAGE', 'MB', 'EXACT_MATCH', 'interface', 'storage_interface', '=', NULL, 'Интерфейс накопителя должен поддерживаться материнской платой'),
    ('STORAGE', 'CASE', 'EXACT_MATCH', 'form_factor', 'drive_bays', 'CONTAINS', NULL, 'Корпус должен иметь отсеки для установки накопителя данного форм-фактора'),
    
    -- Дополнительные правила для кулеров
    ('COOLER', 'CASE', 'RANGE_CHECK', 'height', 'max_cpu_cooler_height', '<=', NULL, 'Высота кулера не должна превышать максимальную высоту, поддерживаемую корпусом'),
    ('COOLER', 'MB', 'EXACT_MATCH', 'socket', 'socket', '=', NULL, 'Сокет кулера должен совпадать с сокетом материнской платы'),
    
    -- Правила для радиаторов жидкостного охлаждения
    ('COOLER', 'CASE', 'SUBSET_CHECK', 'radiator_size', 'radiator_support', 'CONTAINS', NULL, 'Корпус должен поддерживать размер радиатора системы жидкостного охлаждения'),
    
    -- Правила для общей совместимости системы
    ('CPU', 'GPU', 'RANGE_CHECK', 'max_pcie_lanes', 'pcie_lanes', '>=', NULL, 'Процессор должен поддерживать количество линий PCIe, требуемых для видеокарты'),
    ('RAM', 'CPU', 'RANGE_CHECK', 'frequency', 'max_memory_frequency', '<=', NULL, 'Частота оперативной памяти не должна превышать максимальную частоту, поддерживаемую процессором');

-- Добавляем правила для тепловых и энергетических параметров
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, value_modifier, description)
VALUES
    -- Тепловые характеристики
    ('CASE', 'CPU', 'RANGE_CHECK', 'airflow_rating', 'thermal_output', '>=', 'CPU.tdp + GPU.tdp', 'Корпус должен обеспечивать достаточное охлаждение для всех компонентов'),
    
    -- Проверка общей мощности системы
    ('PSU', 'CPU', 'GREATER_THAN', 'wattage', 'total_power', '>=', 'CPU.tdp + GPU.power + (RAM.power_consumption * RAM.modules) + STORAGE.power_consumption', 'Мощность блока питания должна быть достаточной для всей системы с запасом 20%'),
    
    -- Проверка для M.2 SSD и материнских плат
    ('STORAGE', 'MB', 'EXACT_MATCH', 'm2_key', 'm2_supported_keys', 'CONTAINS', NULL, 'Тип ключа M.2 SSD должен поддерживаться материнской платой'),
    
    -- Правила для высокопроизводительных компонентов
    ('CPU', 'MB', 'EXACT_MATCH', 'overclocking_support', 'overclocking_support', '=', NULL, 'Для разгона процессора требуется материнская плата с поддержкой разгона'),
    ('RAM', 'MB', 'EXACT_MATCH', 'xmp_support', 'xmp_support', '=', NULL, 'Для работы XMP-профилей памяти требуется материнская плата с поддержкой XMP'); 