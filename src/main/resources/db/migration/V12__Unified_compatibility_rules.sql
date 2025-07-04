-- Объединенные правила совместимости компонентов ПК
-- Удаляем все существующие правила и создаем упрощенную систему

-- Очищаем таблицу от всех существующих правил
DELETE FROM compatibility_rules;

-- Основные правила совместимости между компонентами
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description)
VALUES
    -- Процессор и материнская плата
    ('CPU', 'MB', 'EXACT_MATCH', 'socket', 'socket', '=', 'Сокет процессора должен совпадать с сокетом материнской платы'),
    ('MB', 'CPU', 'EXACT_MATCH', 'socket', 'socket', '=', 'Сокет материнской платы должен совпадать с сокетом процессора'),
    
    -- Процессор и оперативная память
    ('CPU', 'RAM', 'EXACT_MATCH', 'ram_type', 'type', '=', 'Процессор должен поддерживать тип используемой оперативной памяти'),
    ('RAM', 'CPU', 'EXACT_MATCH', 'type', 'ram_type', '=', 'Тип оперативной памяти должен поддерживаться процессором'),
    
    -- Материнская плата и оперативная память
    ('MB', 'RAM', 'EXACT_MATCH', 'ram_type', 'type', '=', 'Материнская плата должна поддерживать тип используемой оперативной памяти'),
    ('RAM', 'MB', 'EXACT_MATCH', 'type', 'ram_type', '=', 'Тип оперативной памяти должен поддерживаться материнской платой'),
    
    -- НОВОЕ: Проверка максимальной частоты RAM
    ('MB', 'RAM', 'RANGE_CHECK', 'max_ram_frequency', 'frequency', '>=', 'Материнская плата должна поддерживать частоту оперативной памяти'),
    ('RAM', 'MB', 'RANGE_CHECK', 'frequency', 'max_ram_frequency', '<=', 'Частота оперативной памяти не должна превышать максимальную для материнской платы'),
    ('CPU', 'RAM', 'RANGE_CHECK', 'max_ram_frequency', 'frequency', '>=', 'Процессор должен поддерживать частоту оперативной памяти'),
    ('RAM', 'CPU', 'RANGE_CHECK', 'frequency', 'max_ram_frequency', '<=', 'Частота оперативной памяти не должна превышать максимальную для процессора'),
    
    -- НОВОЕ: Проверка максимального объема RAM
    ('MB', 'RAM', 'RANGE_CHECK', 'max_ram', 'capacity', '>=', 'Материнская плата должна поддерживать объем оперативной памяти'),
    ('RAM', 'MB', 'RANGE_CHECK', 'capacity', 'max_ram', '<=', 'Объем оперативной памяти не должен превышать максимальный для материнской платы'),
    
    -- Материнская плата и корпус
    ('MB', 'CASE', 'EXACT_MATCH', 'form_factor', 'form_factor', '=', 'Форм-фактор материнской платы должен поддерживаться корпусом'),
    ('CASE', 'MB', 'EXACT_MATCH', 'form_factor', 'form_factor', '=', 'Корпус должен поддерживать форм-фактор материнской платы'),
    
    -- Процессор и система охлаждения
    ('CPU', 'COOLER', 'EXACT_MATCH', 'socket', 'socket', '=', 'Система охлаждения должна поддерживать сокет процессора'),
    ('COOLER', 'CPU', 'EXACT_MATCH', 'socket', 'socket', '=', 'Сокет системы охлаждения должен совпадать с сокетом процессора'),
    ('CPU', 'COOLER', 'RANGE_CHECK', 'tdp', 'max_tdp', '<=', 'TDP процессора не должен превышать максимальный TDP системы охлаждения'),
    ('COOLER', 'CPU', 'RANGE_CHECK', 'max_tdp', 'tdp', '>=', 'Максимальный TDP системы охлаждения должен покрывать TDP процессора'),
    
    -- Видеокарта и материнская плата (PCIe обратная совместимость)
    -- PCIe имеет обратную совместимость: 5.0 > 4.0 > 3.0 > 2.0 > 1.0
    -- Материнская плата с PCIe 5.0 поддерживает карты 4.0, 3.0 и т.д.
    ('GPU', 'MB', 'CONTAINS', 'interface', 'pcie_slots', 'CONTAINS', 'Видеокарта должна быть совместима с PCIe слотами материнской платы'),
    ('MB', 'GPU', 'CONTAINS', 'pcie_slots', 'interface', 'CONTAINS', 'Материнская плата должна иметь подходящие PCIe слоты для видеокарты'),
    
    -- Видеокарта и корпус
    ('GPU', 'CASE', 'RANGE_CHECK', 'length', 'max_gpu_length', '<=', 'Длина видеокарты не должна превышать максимальную длину, поддерживаемую корпусом'),
    ('CASE', 'GPU', 'RANGE_CHECK', 'max_gpu_length', 'length', '>=', 'Корпус должен поддерживать длину видеокарты'),
    
    -- НОВОЕ: Видеокарта и высота (для низкопрофильных корпусов)
    ('GPU', 'CASE', 'RANGE_CHECK', 'height', 'max_gpu_height', '<=', 'Высота видеокарты не должна превышать максимальную для корпуса'),
    ('CASE', 'GPU', 'RANGE_CHECK', 'max_gpu_height', 'height', '>=', 'Корпус должен поддерживать высоту видеокарты'),
    
    -- Блок питания - более гибкие правила
    -- Общая мощность должна покрывать сумму потребления всех компонентов + запас 20%
    ('PSU', 'CPU', 'RANGE_CHECK', 'wattage', 'power', '>=', 'Мощность блока питания должна покрывать энергопотребление процессора'),
    ('PSU', 'GPU', 'RANGE_CHECK', 'wattage', 'power', '>=', 'Мощность блока питания должна покрывать энергопотребление видеокарты'),
    
    -- НОВОЕ: Проверка рекомендуемой мощности БП для GPU
    ('PSU', 'GPU', 'RANGE_CHECK', 'wattage', 'recommended_psu', '>=', 'Мощность блока питания должна соответствовать рекомендуемой для видеокарты'),
    ('GPU', 'PSU', 'RANGE_CHECK', 'recommended_psu', 'wattage', '<=', 'Рекомендуемая мощность БП для видеокарты не должна превышать мощность выбранного БП'),
    
    -- Накопители и материнская плата
    ('STORAGE', 'MB', 'CONTAINS', 'interface', 'storage_interfaces', 'CONTAINS', 'Интерфейс накопителя должен поддерживаться материнской платой'),
    ('MB', 'STORAGE', 'CONTAINS', 'storage_interfaces', 'interface', 'CONTAINS', 'Материнская плата должна поддерживать интерфейс накопителя'),
    
    -- НОВОЕ: Проверка количества M.2 слотов
    ('MB', 'STORAGE', 'RANGE_CHECK', 'm2_slots', 'm2_required', '>=', 'Материнская плата должна иметь достаточно M.2 слотов'),
    
    -- Система охлаждения и корпус
    ('COOLER', 'CASE', 'RANGE_CHECK', 'height', 'max_cpu_cooler_height', '<=', 'Высота системы охлаждения не должна превышать максимальную высоту, поддерживаемую корпусом'),
    ('CASE', 'COOLER', 'RANGE_CHECK', 'max_cpu_cooler_height', 'height', '>=', 'Корпус должен поддерживать высоту системы охлаждения'),
    
    -- Блок питания и корпус
    ('PSU', 'CASE', 'EXACT_MATCH', 'form_factor', 'psu_form_factor', '=', 'Форм-фактор блока питания должен поддерживаться корпусом'),
    ('CASE', 'PSU', 'EXACT_MATCH', 'psu_form_factor', 'form_factor', '=', 'Корпус должен поддерживать форм-фактор блока питания'),
    
    -- НОВОЕ: Проверка длины БП (для компактных корпусов)
    ('PSU', 'CASE', 'RANGE_CHECK', 'length', 'max_psu_length', '<=', 'Длина блока питания не должна превышать максимальную для корпуса'),
    ('CASE', 'PSU', 'RANGE_CHECK', 'max_psu_length', 'length', '>=', 'Корпус должен поддерживать длину блока питания'),
    
    -- НОВОЕ: Система охлаждения и высокопроизводительная память (для башенных кулеров)
    ('COOLER', 'RAM', 'RANGE_CHECK', 'ram_clearance', 'height', '>=', 'Система охлаждения должна обеспечивать зазор для высокой оперативной памяти'),
    ('RAM', 'COOLER', 'RANGE_CHECK', 'height', 'ram_clearance', '<=', 'Высота оперативной памяти не должна мешать системе охлаждения'),
    
    -- НОВОЕ: Проверка количества слотов расширения
    ('MB', 'GPU', 'RANGE_CHECK', 'pcie_x16_slots', 'slots_required', '>=', 'Материнская плата должна иметь достаточно PCIe x16 слотов'),
    
    -- НОВОЕ: Проверка питания PCIe для мощных видеокарт
    ('MB', 'GPU', 'RANGE_CHECK', 'pcie_power_connectors', 'power_connectors_required', '>=', 'Материнская плата должна поддерживать дополнительное питание PCIe'),
    
    -- НОВОЕ: Проверка совместимости чипсета с процессором (для новых поколений)
    ('CPU', 'MB', 'CONTAINS', 'supported_chipsets', 'chipset', 'CONTAINS', 'Процессор должен поддерживаться чипсетом материнской платы'),
    ('MB', 'CPU', 'CONTAINS', 'chipset', 'supported_chipsets', 'CONTAINS', 'Чипсет материнской платы должен поддерживать процессор');

-- Добавляем комментарий к таблице
COMMENT ON TABLE compatibility_rules IS 'Расширенные правила совместимости компонентов ПК с проверкой частот, объемов, количества слотов и дополнительных параметров'; 