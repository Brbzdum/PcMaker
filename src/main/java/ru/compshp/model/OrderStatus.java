package ru.compshp.model;

public enum OrderStatus {
    PENDING("Ожидает обработки"),
    PROCESSING("В обработке"),
    SHIPPED("Отправлен"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // TODO: Добавить метод для проверки возможности изменения статуса
    // TODO: Добавить метод для получения следующего возможного статуса
    // TODO: Добавить метод для получения всех статусов с их отображаемыми именами
    // TODO: Добавить метод для проверки возможности отмены заказа
} 