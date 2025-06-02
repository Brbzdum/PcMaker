package ru.bek.compshp.model.enums;

/**
 * Перечисление статусов заказа
 */
public enum OrderStatus {
    PENDING("В ожидании"),
    PROCESSING("В обработке"),
    SHIPPED("Отправлен"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен"),
    COMPLETED("Завершен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 