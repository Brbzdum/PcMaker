package ru.bek.compshp.exception;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(Long orderId) {
        super("ORDER_NOT_FOUND", "Order with id %d not found", orderId);
    }
} 