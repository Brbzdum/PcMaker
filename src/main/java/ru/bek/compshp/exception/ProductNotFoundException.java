package ru.bek.compshp.exception;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long productId) {
        super("PRODUCT_NOT_FOUND", "Product with id %d not found", productId);
    }
} 