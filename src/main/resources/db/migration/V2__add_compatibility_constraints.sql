-- Добавление констрейнтов для проверки спецификаций компонентов
ALTER TABLE products ADD CONSTRAINT check_cpu_specs 
CHECK (
    (component_type != 'CPU') OR 
    (specs ? 'socket' AND specs ? 'tdp' AND specs ? 'ram_type' AND
     specs ? 'cores' AND specs ? 'threads' AND specs ? 'base_clock' AND
     specs ? 'boost_clock' AND specs ? 'l3_cache')
);

ALTER TABLE products ADD CONSTRAINT check_motherboard_specs 
CHECK (
    (component_type != 'MOTHERBOARD') OR 
    (specs ? 'socket' AND specs ? 'formFactor' AND specs ? 'ram_slots' AND 
     specs ? 'm2Slots' AND specs ? 'pcie_slots' AND specs ? 'sata_ports' AND
     specs ? 'max_ram' AND specs ? 'ram_type' AND specs ? 'chipset')
);

ALTER TABLE products ADD CONSTRAINT check_gpu_specs 
CHECK (
    (component_type != 'GPU') OR 
    (specs ? 'powerConsumption' AND specs ? 'length' AND specs ? 'pcie_version' AND
     specs ? 'memory_size' AND specs ? 'memory_type' AND specs ? 'boost_clock' AND
     specs ? 'base_clock' AND specs ? 'tdp')
);

ALTER TABLE products ADD CONSTRAINT check_psu_specs 
CHECK (
    (component_type != 'PSU') OR 
    (specs ? 'wattage' AND specs ? 'efficiency' AND specs ? 'modular' AND
     specs ? 'pcie_connectors' AND specs ? 'sata_connectors' AND
     specs ? 'atx_connector' AND specs ? 'eps_connector')
);

ALTER TABLE products ADD CONSTRAINT check_case_specs 
CHECK (
    (component_type != 'CASE') OR 
    (specs ? 'formFactor' AND specs ? 'maxGpuLength' AND specs ? 'maxCpuCoolerHeight' AND
     specs ? 'maxPsuLength' AND specs ? 'expansionSlots' AND specs ? 'fanMounts' AND
     specs ? 'includedFans' AND specs ? 'usbPorts')
);

ALTER TABLE products ADD CONSTRAINT check_ram_specs 
CHECK (
    (component_type != 'RAM') OR 
    (specs ? 'type' AND specs ? 'speed' AND specs ? 'capacity' AND
     specs ? 'modules' AND specs ? 'timing' AND specs ? 'voltage' AND
     specs ? 'height')
);

ALTER TABLE products ADD CONSTRAINT check_storage_specs 
CHECK (
    (component_type != 'STORAGE') OR 
    (specs ? 'type' AND specs ? 'capacity' AND specs ? 'isM2' AND
     specs ? 'interface' AND specs ? 'read_speed' AND specs ? 'write_speed' AND
     specs ? 'form_factor')
);

-- Добавление констрейнтов для проверки значений
ALTER TABLE products ADD CONSTRAINT check_positive_values
CHECK (
    (component_type != 'CPU' OR (specs->>'tdp')::integer > 0) AND
    (component_type != 'GPU' OR (specs->>'powerConsumption')::integer > 0) AND
    (component_type != 'PSU' OR (specs->>'wattage')::integer > 0) AND
    (component_type != 'RAM' OR (specs->>'capacity')::integer > 0) AND
    (component_type != 'STORAGE' OR (specs->>'capacity')::integer > 0)
);

-- Создание таблицы для кеша совместимости
CREATE TABLE IF NOT EXISTS compatibility_cache (
    config_hash VARCHAR(64) PRIMARY KEY,
    is_compatible BOOLEAN NOT NULL,
    errors JSONB,
    warnings JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    total_power_consumption INTEGER,
    estimated_price DECIMAL(10,2),
    performance_score INTEGER
);

-- Создание индексов для оптимизации поиска
CREATE INDEX idx_compatibility_cache_created_at ON compatibility_cache(created_at);
CREATE INDEX idx_compatibility_cache_is_compatible ON compatibility_cache(is_compatible);
CREATE INDEX idx_compatibility_cache_performance ON compatibility_cache(performance_score);

-- Добавление триггера для автоматической очистки устаревших записей кеша
CREATE OR REPLACE FUNCTION clean_old_compatibility_cache()
RETURNS trigger AS $$
BEGIN
    DELETE FROM compatibility_cache 
    WHERE created_at < NOW() - INTERVAL '7 days';
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_clean_compatibility_cache
AFTER INSERT ON compatibility_cache
EXECUTE FUNCTION clean_old_compatibility_cache();

-- Создание таблицы для хранения правил совместимости
CREATE TABLE IF NOT EXISTS compatibility_rules (
    id SERIAL PRIMARY KEY,
    source_type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    rule_condition JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_rule UNIQUE (source_type, target_type)
);

-- Создание индекса для быстрого поиска правил
CREATE INDEX idx_compatibility_rules_types 
ON compatibility_rules(source_type, target_type);

-- Добавление триггера для обновления updated_at
CREATE OR REPLACE FUNCTION update_compatibility_rules_timestamp()
RETURNS trigger AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_compatibility_rules_timestamp
BEFORE UPDATE ON compatibility_rules
FOR EACH ROW
EXECUTE FUNCTION update_compatibility_rules_timestamp(); 