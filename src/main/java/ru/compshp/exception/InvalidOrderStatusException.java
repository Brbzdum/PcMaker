package ru.compshp.exception;

import ru.compshp.model.OrderStatus;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException(OrderStatus currentStatus, OrderStatus newStatus) {
        super("INVALID_ORDER_STATUS", 
              "Cannot change order status from %s to %s", 
              currentStatus, newStatus);
    }
} 