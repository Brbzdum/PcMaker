-- Создание таблицы для правил совместимости компонентов
CREATE TABLE compatibility_rules (
    id BIGSERIAL PRIMARY KEY,
    source_type component_type NOT NULL,
    target_type component_type NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- EXACT_MATCH, RANGE_CHECK, COMPATIBILITY_LIST, GREATER_THAN, LESS_THAN, SUBSET_CHECK
    source_property VARCHAR(100) NOT NULL,
    target_property VARCHAR(100) NOT NULL,
    comparison_operator VARCHAR(20) NOT NULL, -- =, >, <, >=, <=, IN, CONTAINS
    value_modifier VARCHAR(255),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание индексов для оптимизации запросов
CREATE INDEX idx_rules_source_type ON compatibility_rules(source_type);
CREATE INDEX idx_rules_target_type ON compatibility_rules(target_type);
CREATE INDEX idx_rules_active ON compatibility_rules(is_active);

-- Триггер для обновления поля updated_at
CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON compatibility_rules
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Добавление базовых правил совместимости
INSERT INTO compatibility_rules 
(source_type, target_type, rule_type, source_property, target_property, comparison_operator, description)
VALUES
('CPU', 'MB', 'EXACT_MATCH', 'socket', 'socket', '=', 'Совместимость сокета процессора и материнской платы'),
('RAM', 'MB', 'EXACT_MATCH', 'type', 'ram_type', '=', 'Совместимость типа оперативной памяти и материнской платы'),
('GPU', 'PSU', 'GREATER_THAN', 'power', 'wattage', '<=', 'Совместимость мощности видеокарты и блока питания'),
('CPU', 'PSU', 'GREATER_THAN', 'power', 'wattage', '<=', 'Совместимость мощности процессора и блока питания'); 