package ru.bek.compshp.exception;

public class CartNotFoundException extends BusinessException {
    public CartNotFoundException(Long userId) {
        super("CART_NOT_FOUND", "Cart for user %d not found", userId);
    }
} 