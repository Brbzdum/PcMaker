package ru.compshp.model;

public enum ProductCategory {
    PC_COMPONENT("Комплектующие ПК"),
    LAPTOP("Ноутбуки"),
    MONITOR("Мониторы"),
    PERIPHERAL("Периферия"),
    STORAGE("Накопители"),
    ACCESSORY("Аксессуары");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // TODO: Добавить метод для получения всех категорий с их отображаемыми именами
    // TODO: Добавить метод для проверки, является ли категория комплектующим ПК
    // TODO: Добавить метод для получения подкатегорий (если потребуется)
} 