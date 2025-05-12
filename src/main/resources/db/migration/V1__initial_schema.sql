-- Удаление существующих таблиц и типов
DROP TABLE IF EXISTS config_components CASCADE;
DROP TABLE IF EXISTS pc_configurations CASCADE;
DROP TABLE IF EXISTS order_status_history CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS manufacturers CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS compatibility_rules CASCADE;
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS discounts CASCADE;
DROP TABLE IF EXISTS promotions CASCADE;
DROP TABLE IF EXISTS promotion_products CASCADE;
DROP TABLE IF EXISTS recommended_configs CASCADE;
DROP TABLE IF EXISTS recommended_config_components CASCADE;

DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS component_type CASCADE;
DROP TYPE IF EXISTS product_category CASCADE;
DROP TYPE IF EXISTS order_status CASCADE;
DROP TYPE IF EXISTS discount_type CASCADE;
DROP TYPE IF EXISTS promotion_type CASCADE;

-- Создание типов данных
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN', 'MANAGER');
CREATE TYPE component_type AS ENUM (
    'CPU', 'GPU', 'MB', 'RAM', 'PSU', 
    'CASE', 'COOLER', 'STORAGE', 'SSD', 'HDD'
);
CREATE TYPE product_category AS ENUM (
    'PC_COMPONENT', 'LAPTOP', 'MONITOR',
    'PERIPHERAL', 'STORAGE', 'ACCESSORY'
);
CREATE TYPE order_status AS ENUM (
    'PENDING', 'PROCESSING', 
    'SHIPPED', 'DELIVERED', 'CANCELLED'
);
CREATE TYPE discount_type AS ENUM (
    'PERCENTAGE', 'FIXED_AMOUNT', 'BUNDLE'
);
CREATE TYPE promotion_type AS ENUM (
    'SEASONAL', 'FLASH_SALE', 'BUNDLE', 'LOYALTY', 'FIRST_PURCHASE'
);

-- Создание таблиц
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(1000) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN DEFAULT FALSE,
    activation_code VARCHAR(255),
    verification_code VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE manufacturers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    website VARCHAR(255),
    logo VARCHAR(255),
    rating DECIMAL(3,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    purchase_price DECIMAL(15,2) NOT NULL,
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    component_type component_type,
    specs JSONB NOT NULL,
    average_rating DECIMAL(3,2),
    ratings_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    CHECK (
        (component_type IS NOT NULL AND category_id IN (SELECT id FROM categories WHERE name = 'PC_COMPONENT')) OR
        (component_type IS NULL AND category_id NOT IN (SELECT id FROM categories WHERE name = 'PC_COMPONENT'))
    )
);

CREATE TABLE compatibility_rules (
    id BIGSERIAL PRIMARY KEY,
    source_type component_type NOT NULL,
    target_type component_type NOT NULL,
    check_condition JSONB NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (source_type != target_type)
);

CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id, product_id)
);

CREATE TABLE pc_configurations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    total_price DECIMAL(15,2),
    power_requirement INTEGER,
    is_compatible BOOLEAN,
    compatibility_notes JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE config_components (
    config_id BIGINT NOT NULL REFERENCES pc_configurations(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (config_id, product_id)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_price DECIMAL(15,2) NOT NULL,
    status order_status NOT NULL DEFAULT 'PENDING',
    delivery_address JSONB,
    delivery_method VARCHAR(50),
    tracking_number VARCHAR(100),
    return_status VARCHAR(50),
    return_reason TEXT,
    pc_configuration_id BIGINT REFERENCES pc_configurations(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id, product_id)
);

CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status order_status NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment TEXT
);

CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_profit DECIMAL(15,2) NOT NULL
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    is_moderated BOOLEAN DEFAULT FALSE,
    is_approved BOOLEAN,
    report_count INTEGER DEFAULT 0,
    UNIQUE (user_id, product_id)
);

-- Добавление новых таблиц для скидок и акций
CREATE TABLE discounts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    discount_type discount_type NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    min_purchase_amount DECIMAL(15,2),
    max_discount_amount DECIMAL(15,2),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date > start_date),
    CHECK (value > 0)
);

CREATE TABLE promotions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    promotion_type promotion_type NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    conditions JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_date > start_date)
);

CREATE TABLE promotion_products (
    promotion_id BIGINT NOT NULL REFERENCES promotions(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    min_quantity INTEGER DEFAULT 1,
    max_quantity INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (promotion_id, product_id),
    CHECK (
        (discount_percentage IS NOT NULL AND discount_amount IS NULL) OR
        (discount_percentage IS NULL AND discount_amount IS NOT NULL)
    ),
    CHECK (discount_percentage > 0 AND discount_percentage <= 100),
    CHECK (discount_amount > 0),
    CHECK (min_quantity > 0),
    CHECK (max_quantity IS NULL OR max_quantity >= min_quantity)
);

-- Добавление таблиц для рекомендованных конфигураций
CREATE TABLE recommended_configs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    target_usage VARCHAR(50) NOT NULL,
    price_range_min DECIMAL(15,2),
    price_range_max DECIMAL(15,2),
    performance_score INTEGER,
    is_featured BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (price_range_max > price_range_min)
);

CREATE TABLE recommended_config_components (
    config_id BIGINT NOT NULL REFERENCES recommended_configs(id) ON DELETE CASCADE,
    component_type component_type NOT NULL,
    specs_requirements JSONB NOT NULL,
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (config_id, component_type)
);

-- Оптимизированные индексы
CREATE INDEX idx_products_component_type ON products(component_type);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_manufacturer ON products(manufacturer_id);
CREATE INDEX idx_products_specs ON products USING GIN(specs);
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_pc_configurations_user ON pc_configurations(user_id);
CREATE INDEX idx_config_components_config ON config_components(config_id);
CREATE INDEX idx_compatibility_rules_types ON compatibility_rules(source_type, target_type);
CREATE INDEX idx_sales_date ON sales(sale_date);

-- Добавление индексов для новых таблиц
CREATE INDEX idx_discounts_dates ON discounts(start_date, end_date);
CREATE INDEX idx_discounts_active ON discounts(is_active);
CREATE INDEX idx_promotions_dates ON promotions(start_date, end_date);
CREATE INDEX idx_promotions_active ON promotions(is_active);
CREATE INDEX idx_promotions_type ON promotions(promotion_type);
CREATE INDEX idx_promotion_products_product ON promotion_products(product_id);
CREATE INDEX idx_recommended_configs_usage ON recommended_configs(target_usage);
CREATE INDEX idx_recommended_configs_price ON recommended_configs(price_range_min, price_range_max);
CREATE INDEX idx_recommended_configs_featured ON recommended_configs(is_featured);

-- Создание функций и триггеров
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_product_ratings()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        UPDATE products
        SET 
            average_rating = (
                SELECT AVG(rating) 
                FROM reviews 
                WHERE product_id = OLD.product_id
            ),
            ratings_count = (
                SELECT COUNT(*) 
                FROM reviews 
                WHERE product_id = OLD.product_id
            )
        WHERE id = OLD.product_id;
    ELSE
        UPDATE products
        SET 
            average_rating = (
                SELECT AVG(rating) 
                FROM reviews 
                WHERE product_id = NEW.product_id
            ),
            ratings_count = (
                SELECT COUNT(*) 
                FROM reviews 
                WHERE product_id = NEW.product_id
            )
        WHERE id = NEW.product_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_sale_profit()
RETURNS TRIGGER AS $$
BEGIN
    NEW.total_profit = (
        SELECT SUM((oi.price - p.purchase_price) * oi.quantity)
        FROM order_items oi
        JOIN products p ON oi.product_id = p.id
        WHERE oi.order_id = NEW.order_id
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION reduce_stock_on_order()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT stock FROM products WHERE id = NEW.product_id) < NEW.quantity THEN
        RAISE EXCEPTION 'Недостаточно товара на складе для продукта id=%', NEW.product_id;
    END IF;

    UPDATE products
    SET stock = stock - NEW.quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION restore_stock_on_delete()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock = stock + OLD.quantity
    WHERE id = OLD.product_id;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION adjust_stock_on_update()
RETURNS TRIGGER AS $$
DECLARE
    delta INT;
BEGIN
    delta = NEW.quantity - OLD.quantity;

    IF delta = 0 THEN
        RETURN NEW;
    END IF;

    IF delta > 0 THEN
        IF (SELECT stock FROM products WHERE id = NEW.product_id) < delta THEN
            RAISE EXCEPTION 'Недостаточно товара для обновления позиции заказа (product_id=%)', NEW.product_id;
        END IF;
    END IF;

    UPDATE products
    SET stock = stock - delta
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггеров
CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON pc_configurations
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON config_components
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_review_ratings
    AFTER INSERT OR UPDATE OR DELETE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_product_ratings();

CREATE TRIGGER trg_sale_profit
    BEFORE INSERT ON sales
    FOR EACH ROW
    EXECUTE FUNCTION calculate_sale_profit();

CREATE TRIGGER trg_reduce_stock
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION reduce_stock_on_order();

CREATE TRIGGER trg_restore_stock
    AFTER DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION restore_stock_on_delete();

CREATE TRIGGER trg_adjust_stock
    AFTER UPDATE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION adjust_stock_on_update();

-- Добавление триггеров для новых таблиц
CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON discounts
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON promotions
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON promotion_products
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON recommended_configs
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON recommended_config_components
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

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
    (component_type != 'MB') OR 
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