-- Создание базы данных и подключение
-- CREATE DATABASE computer_store;


CREATE TYPE user_role AS ENUM ('USER', 'ADMIN', 'MANAGER');
CREATE TYPE component_type AS ENUM (
    'CPU', 'GPU', 'MB', 'RAM', 'PSU', 
    'CASE', 'COOLER', 'STORAGE', 'SSD', 'HDD'
);
CREATE TYPE order_status AS ENUM (
    'PENDING', 'PROCESSING', 
    'SHIPPED', 'DELIVERED', 'CANCELLED'
);

-- Таблица категорий
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Основные таблицы
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    activation_code VARCHAR(255),
    password VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE manufacturers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    website VARCHAR(255)
);

-- Таблица продуктов
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    purchase_price NUMERIC(15,2) NOT NULL,
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    stock INT NOT NULL CHECK (stock >= 0),
    manufacturer_id BIGINT NOT NULL REFERENCES manufacturers(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    component_type component_type,
    specs JSONB NOT NULL,
    CHECK (
        (SELECT name FROM categories WHERE id = category_id) = 'PC_COMPONENT' AND component_type IS NOT NULL OR
        (SELECT name FROM categories WHERE id = category_id) != 'PC_COMPONENT' AND component_type IS NULL
    )
);
-- Пересоздаем таблицу правил совместимости с корректными внешними ключами
CREATE TABLE compatibility_rules (
    id BIGSERIAL PRIMARY KEY,
    source_type component_type NOT NULL,
    target_type component_type NOT NULL,
    check_condition JSONB NOT NULL,
    description TEXT,
    CHECK (source_type != target_type)
);
-- Создаём таблицу конкретной совместимости между продуктами
CREATE TABLE product_compatibility (
    id BIGSERIAL PRIMARY KEY,
    product_id_source BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    product_id_target BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    rule_id BIGINT REFERENCES compatibility_rules(id),
    valid BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);
-- Система заказов
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    total_price NUMERIC(15,2) NOT NULL,
    status order_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    delivery_address JSONB
);

CREATE TABLE order_items (
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    price NUMERIC(15,2) NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

-- Таблица продаж
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    sale_date TIMESTAMP DEFAULT NOW(),
    total_profit NUMERIC(15,2) NOT NULL -- Убрали generated column
);

-- Корзины и конфигураторы
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (cart_id, product_id)
);

CREATE TABLE pc_configurations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    last_modified TIMESTAMP DEFAULT NOW()
);

CREATE TABLE config_components (
    config_id BIGINT NOT NULL REFERENCES pc_configurations(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (config_id, product_id)
);

-- Таблица отзывов
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id, product_id) -- Один отзыв на пользователя и продукт
);

-- Таблица истории статусов заказа
CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status order_status NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    comment TEXT
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_timestamp ON order_status_history(timestamp);

-- Индексы для быстрого поиска
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Триггер для автоматического обновления времени редактирования
CREATE OR REPLACE FUNCTION update_review_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_review_update
BEFORE UPDATE ON reviews
FOR EACH ROW EXECUTE FUNCTION update_review_timestamp();

-- Добавляем расчет среднего рейтинга в продукты
ALTER TABLE products ADD COLUMN average_rating NUMERIC(3,2);
ALTER TABLE products ADD COLUMN ratings_count INT DEFAULT 0;

-- Функция для обновления рейтингов
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

-- Триггеры для автоматического обновления рейтингов
CREATE TRIGGER trg_review_ratings
AFTER INSERT OR UPDATE OR DELETE ON reviews
FOR EACH ROW EXECUTE FUNCTION update_product_ratings();
-- Индексы и оптимизации
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_components_type ON products(component_type);
CREATE INDEX idx_specs ON products USING GIN(specs);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_sales_date ON sales(sale_date);

-- Триггеры
CREATE OR REPLACE FUNCTION update_order_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_order_update
BEFORE UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION update_order_timestamp();

CREATE TRIGGER trg_order_delivered
AFTER UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION create_sale_on_delivery();

-- Триггер для расчета прибыли при создании продажи
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

CREATE TRIGGER trg_sale_profit
BEFORE INSERT ON sales
FOR EACH ROW EXECUTE FUNCTION calculate_sale_profit();

-- Модифицированный триггер для создания продажи
CREATE OR REPLACE FUNCTION create_sale_on_delivery()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'DELIVERED' AND OLD.status <> 'DELIVERED' THEN
        INSERT INTO sales (order_id) VALUES (NEW.id); -- Триггер автоматически рассчитает profit
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--ДЛЯ КОЛИЧЕСТВА ТОВАРОВ 
CREATE OR REPLACE FUNCTION reduce_stock_on_order()
RETURNS TRIGGER AS $$
BEGIN
    -- Проверка, хватает ли товара
    IF (SELECT stock FROM products WHERE id = NEW.product_id) < NEW.quantity THEN
        RAISE EXCEPTION 'Недостаточно товара на складе для продукта id=%', NEW.product_id;
    END IF;

    -- Уменьшаем stock
    UPDATE products
    SET stock = stock - NEW.quantity
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reduce_stock
AFTER INSERT ON order_items
FOR EACH ROW
EXECUTE FUNCTION reduce_stock_on_order();

--Восстановление stock при удалении позиции из заказа 
CREATE OR REPLACE FUNCTION restore_stock_on_delete()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock = stock + OLD.quantity
    WHERE id = OLD.product_id;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_restore_stock
AFTER DELETE ON order_items
FOR EACH ROW
EXECUTE FUNCTION restore_stock_on_delete();
-- Добавляем колонку для связи заказа с конфигурацией сборки
ALTER TABLE orders
ADD COLUMN pc_configuration_id BIGINT REFERENCES pc_configurations(id);

-- Функция для корректировки stock при UPDATE строки в order_items
CREATE OR REPLACE FUNCTION adjust_stock_on_update()
RETURNS TRIGGER AS $$
DECLARE
    delta INT;
BEGIN
    -- Разница между новым и старым количеством
    delta = NEW.quantity - OLD.quantity;

    -- Если изменений нет, выходим
    IF delta = 0 THEN
        RETURN NEW;
    END IF;

    -- Если количество увеличивается, проверяем наличие нужного остатка
    IF delta > 0 THEN
        IF (SELECT stock FROM products WHERE id = NEW.product_id) < delta THEN
            RAISE EXCEPTION 'Недостаточно товара для обновления позиции заказа (product_id=%)', NEW.product_id;
        END IF;
    END IF;

    -- Корректируем stock: уменьшаем при увеличении заказа, возвращаем при уменьшении
    UPDATE products
    SET stock = stock - delta
    WHERE id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер, который вызывает функцию adjust_stock_on_update после обновления строки в order_items
CREATE TRIGGER trg_adjust_stock
AFTER UPDATE ON order_items
FOR EACH ROW
EXECUTE FUNCTION adjust_stock_on_update();



