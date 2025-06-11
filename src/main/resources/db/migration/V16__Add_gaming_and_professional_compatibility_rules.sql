-- Добавляем специфические правила совместимости для геймерских и профессиональных конфигураций ПК

-- Правила для высокопроизводительных геймерских конфигураций
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description, value_modifier)
VALUES
    -- Геймерские конфигурации требуют высокопроизводительные компоненты
    ('CPU', 'GPU', 'COMPATIBILITY_LIST', 'performance', 'performance', 'BALANCED', 'Для геймерской системы процессор и видеокарта должны быть сбалансированы по производительности', 'CPU.performance * 0.8 <= GPU.performance AND CPU.performance * 1.2 >= GPU.performance'),
    ('RAM', 'GPU', 'RANGE_CHECK', 'capacity', 'memory', '>=', 'Объем оперативной памяти должен быть не меньше объема памяти видеокарты для геймерских конфигураций', 'RAM.capacity >= GPU.memory * 2'),
    ('COOLER', 'CPU', 'GREATER_THAN', 'max_tdp', 'tdp', '>=', 'Для геймерских процессоров с высоким TDP требуется соответствующее охлаждение', 'COOLER.max_tdp >= CPU.tdp * 1.3'),
    
    -- Правила для оверклокинга
    ('CPU', 'COOLER', 'RANGE_CHECK', 'overclocking_support', 'max_tdp', 'CONDITION', 'Если процессор поддерживает разгон, система охлаждения должна иметь запас по TDP', 'IF CPU.overclocking_support = "Yes" THEN COOLER.max_tdp >= CPU.tdp * 1.5'),
    ('MB', 'PSU', 'RANGE_CHECK', 'overclocking_support', 'wattage', 'CONDITION', 'Если материнская плата поддерживает разгон, блок питания должен иметь дополнительный запас мощности', 'IF MB.overclocking_support = "Yes" THEN PSU.wattage >= CASE.total_power * 1.3'),
    
    -- Геймерские требования к накопителям
    ('STORAGE', 'CASE', 'RANGE_CHECK', 'read_speed', 'performance_level', '>=', 'Для геймерской системы высокого уровня требуется быстрый накопитель', 'IF CASE.performance_level = "High" THEN STORAGE.read_speed >= 3000');

-- Правила для профессиональных рабочих станций
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description, value_modifier)
VALUES
    -- Профессиональные конфигурации требуют еще больше ресурсов
    ('CPU', 'RAM', 'RANGE_CHECK', 'cores', 'capacity', '>=', 'Для профессиональных рабочих станций требуется достаточный объем памяти', 'RAM.capacity >= CPU.cores * 4'),
    ('PSU', 'CASE', 'GREATER_THAN', 'wattage', 'total_power', '>=', 'Блок питания для профессиональной системы должен иметь запас мощности', 'PSU.wattage >= CASE.total_power * 1.5'),
    ('STORAGE', 'CASE', 'RANGE_CHECK', 'type', 'purpose', 'CONDITION', 'Для профессиональных задач рекомендуется использовать NVMe SSD', 'IF CASE.purpose = "Professional" THEN STORAGE.interface CONTAINS "NVMe"'),
    
    -- Требования к надежности для рабочих станций
    ('PSU', 'CASE', 'EXACT_MATCH', 'efficiency', 'reliability_level', '>=', 'Для надежных рабочих станций требуется высокоэффективный блок питания', 'IF CASE.reliability_level = "High" THEN PSU.efficiency IN ("80 PLUS Gold", "80 PLUS Platinum", "80 PLUS Titanium")'),
    ('RAM', 'CASE', 'EXACT_MATCH', 'ecc_support', 'purpose', 'CONDITION', 'Для профессиональных рабочих станций рекомендуется ECC память', 'IF CASE.purpose = "Professional_Critical" THEN RAM.ecc_support = "Yes"'),
    
    -- Требования к системе охлаждения для длительных нагрузок
    ('CASE', 'CPU', 'RANGE_CHECK', 'airflow_rating', 'workload_duration', '>=', 'Для систем с длительными рабочими нагрузками требуется хорошая вентиляция', 'IF CPU.workload_duration = "Extended" THEN CASE.airflow_rating >= 85'),
    ('COOLER', 'CASE', 'RANGE_CHECK', 'noise_level', 'environment', '<=', 'Для тихих рабочих сред требуется тихая система охлаждения', 'IF CASE.environment = "Quiet" THEN COOLER.noise_level <= 25');

-- Правила для специализированных задач
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description, value_modifier)
VALUES
    -- Правила для задач машинного обучения
    ('GPU', 'CPU', 'RANGE_CHECK', 'memory', 'workload_type', 'CONDITION', 'Для задач машинного обучения требуется видеокарта с большим объемом памяти', 'IF CPU.workload_type = "ML" THEN GPU.memory >= 12'),
    ('RAM', 'CPU', 'RANGE_CHECK', 'capacity', 'workload_type', 'CONDITION', 'Для задач машинного обучения требуется большой объем оперативной памяти', 'IF CPU.workload_type = "ML" THEN RAM.capacity >= 32'),
    
    -- Правила для задач рендеринга
    ('CPU', 'GPU', 'RANGE_CHECK', 'cores', 'workload_type', 'CONDITION', 'Для задач рендеринга требуется процессор с большим количеством ядер', 'IF GPU.workload_type = "Rendering" THEN CPU.cores >= 8'),
    ('STORAGE', 'CPU', 'RANGE_CHECK', 'capacity', 'workload_type', 'CONDITION', 'Для задач рендеринга требуется достаточно места на накопителе', 'IF CPU.workload_type = "Rendering" THEN STORAGE.capacity >= 1000'),
    
    -- Правила для серверных задач
    ('MB', 'CPU', 'RANGE_CHECK', 'max_memory', 'workload_type', 'CONDITION', 'Для серверных задач требуется поддержка большого объема памяти', 'IF CPU.workload_type = "Server" THEN MB.max_memory >= 128'),
    ('PSU', 'MB', 'EXACT_MATCH', 'efficiency', 'workload_type', 'CONDITION', 'Для серверных систем рекомендуется высокоэффективный блок питания', 'IF MB.workload_type = "Server" THEN PSU.efficiency IN ("80 PLUS Platinum", "80 PLUS Titanium")');

-- Правила для компактных систем
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description)
VALUES
    -- Правила для Mini-ITX сборок
    ('CASE', 'MB', 'EXACT_MATCH', 'form_factor', 'size_factor', '=', 'Для компактных систем требуется корпус соответствующего форм-фактора'),
    ('MB', 'CASE', 'EXACT_MATCH', 'form_factor', 'size_factor', '=', 'Для компактных систем требуется материнская плата соответствующего форм-фактора'),
    ('COOLER', 'CASE', 'RANGE_CHECK', 'height', 'max_cpu_cooler_height', '<=', 'Высота кулера должна соответствовать ограничениям компактного корпуса'),
    ('PSU', 'CASE', 'EXACT_MATCH', 'form_factor', 'psu_form_factor', '=', 'Форм-фактор блока питания должен соответствовать поддерживаемому корпусом');

-- Правила совместимости для многопроцессорных систем
INSERT INTO compatibility_rules
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description)
VALUES
    ('MB', 'CPU', 'EXACT_MATCH', 'multi_cpu_support', 'cpu_count', '>=', 'Для многопроцессорных систем требуется соответствующая материнская плата'),
    ('CASE', 'CPU', 'RANGE_CHECK', 'cooling_capacity', 'cpu_count', '>=', 'Для многопроцессорных систем требуется корпус с хорошей вентиляцией'),
    ('PSU', 'CPU', 'GREATER_THAN', 'wattage', 'total_power', '>=', 'Мощность блока питания должна соответствовать энергопотреблению многопроцессорной системы'); 