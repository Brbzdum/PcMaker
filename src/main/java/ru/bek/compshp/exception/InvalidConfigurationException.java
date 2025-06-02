package ru.bek.compshp.exception;

public class InvalidConfigurationException extends BusinessException {
    public InvalidConfigurationException(String message) {
        super("INVALID_CONFIGURATION", message);
    }

    public InvalidConfigurationException(String component1, String component2, String reason) {
        super("INVALID_CONFIGURATION", 
              "Components %s and %s are not compatible: %s", 
              component1, component2, reason);
    }
} 