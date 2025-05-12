package ru.compshp.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "User with id %d not found", userId);
    }
} 