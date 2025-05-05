package ru.compshp.model;

public enum UserRole {
    USER("Пользователь"),
    ADMIN("Администратор"),
    MANAGER("Менеджер");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // TODO: Добавить метод для проверки прав доступа
    // TODO: Добавить метод для получения всех ролей с их отображаемыми именами
    // TODO: Добавить метод для проверки иерархии ролей
} 