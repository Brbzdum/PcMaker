-- Инициализация схемы базы данных

-- Создание типов данных
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE product_category AS ENUM (
    'PC_COMPONENT', 'LAPTOP', 'MONITOR',
    'PERIPHERAL', 'STORAGE', 'ACCESSORY'
);

-- Создание таблиц
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    activation_code VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(1000) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_pc_component BOOLEAN NOT NULL DEFAULT FALSE,
    slug VARCHAR(255) UNIQUE,
    is_peripheral BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE manufacturers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rating DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stock INTEGER NOT NULL CHECK (stock >= 0),
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    component_type VARCHAR(50),
    specs JSONB NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
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
    is_compatible BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_performance DOUBLE PRECISION DEFAULT 0.0,
    is_public BOOLEAN,
    category VARCHAR(50)
);

CREATE TABLE config_components (
    config_id BIGINT NOT NULL REFERENCES pc_configurations(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id BIGSERIAL NOT NULL,
    PRIMARY KEY (config_id, product_id)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_price DECIMAL(15,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    delivery_address JSONB,
    pc_configuration_id BIGINT REFERENCES pc_configurations(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    phone VARCHAR(255),
    full_name VARCHAR(255),
    payment_method VARCHAR(255),
    comment VARCHAR(255)
);

CREATE TABLE order_items (
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id, product_id)
);

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    report_count INTEGER DEFAULT 0,
    is_moderated BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, product_id)
);

CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255),
    comment TEXT
);

-- Создание таблицы для правил совместимости компонентов
CREATE TABLE compatibility_rules (
    id BIGSERIAL PRIMARY KEY,
    source_type VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- EXACT_MATCH, RANGE_CHECK, COMPATIBILITY_LIST, GREATER_THAN, LESS_THAN, SUBSET_CHECK
    source_property VARCHAR(100) NOT NULL,
    target_property VARCHAR(100) NOT NULL,
    comparison_operator VARCHAR(20) NOT NULL, -- =, >, <, >=, <=, IN, CONTAINS, BALANCED, CONDITION
    value_modifier VARCHAR(255),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
-- Индексы для таблицы правил совместимости
CREATE INDEX idx_rules_source_type ON compatibility_rules(source_type);
CREATE INDEX idx_rules_target_type ON compatibility_rules(target_type);
CREATE INDEX idx_rules_active ON compatibility_rules(is_active);

-- Создание функций и триггеров
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
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

-- Триггеры для обновления временных меток
CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

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

CREATE TRIGGER trg_update_timestamp
    BEFORE UPDATE ON compatibility_rules
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Триггеры для управления запасами
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