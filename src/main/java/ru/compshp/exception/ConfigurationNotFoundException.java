package ru.compshp.exception;

public class ConfigurationNotFoundException extends BusinessException {
    public ConfigurationNotFoundException(Long configId) {
        super("CONFIGURATION_NOT_FOUND", "PC configuration with id %d not found", configId);
    }
} 