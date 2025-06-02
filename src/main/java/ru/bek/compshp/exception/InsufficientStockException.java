package ru.bek.compshp.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends BusinessException {
    private final int available;
    private final int requested;

    public InsufficientStockException(Long productId, int available, int requested) {
        super("INSUFFICIENT_STOCK", 
            "Not enough stock for product %d. Available: %d, Requested: %d", 
            productId, available, requested);
        this.available = available;
        this.requested = requested;
    }
} 