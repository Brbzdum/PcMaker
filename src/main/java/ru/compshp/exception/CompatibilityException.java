package ru.compshp.exception;

public class CompatibilityException extends BusinessException {
    public CompatibilityException(String message) {
        super("COMPATIBILITY_ERROR", message);
    }

    public CompatibilityException(String component1, String component2, String reason) {
        super("COMPATIBILITY_ERROR", 
              "Components %s and %s are not compatible: %s", 
              component1, component2, reason);
    }
} 