-- Скрипт для удаления товаров с типом компонента PERIPHERAL
-- Запустите этот скрипт напрямую в вашей базе данных PostgreSQL

-- Находим и удаляем все записи о компонентах с типом PERIPHERAL в компонентах конфигураций
DELETE FROM config_components 
WHERE product_id IN (SELECT id FROM products WHERE component_type = 'PERIPHERAL');

-- Удаляем все товары с типом компонента PERIPHERAL
DELETE FROM products 
WHERE component_type = 'PERIPHERAL';

-- Обновляем любые другие записи, которые могут ссылаться на тип PERIPHERAL
UPDATE products 
SET component_type = NULL 
WHERE component_type = 'PERIPHERAL'; 