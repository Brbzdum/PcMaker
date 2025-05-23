package ru.compshp.exception;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(Long productId, int requested, int available) {
        super("INSUFFICIENT_STOCK", 
              "Insufficient stock for product %d. Requested: %d, Available: %d", 
              productId, requested, available);
    }
} 