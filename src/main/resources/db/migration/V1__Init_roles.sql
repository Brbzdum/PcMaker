-- Инициализация ролей пользователей
INSERT INTO roles (name, created_at) VALUES
    ('ROLE_USER', CURRENT_TIMESTAMP),
    ('ROLE_ADMIN', CURRENT_TIMESTAMP),
    ('ROLE_MODERATOR', CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING; 