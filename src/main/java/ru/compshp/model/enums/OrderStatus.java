package ru.compshp.model.enums;

public enum OrderStatus {
    PENDING("Ожидает подтверждения"),
    CONFIRMED("Подтвержден"),
    PROCESSING("В обработке"),
    SHIPPED("Отправлен"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен"),
    REFUNDED("Возвращен");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 