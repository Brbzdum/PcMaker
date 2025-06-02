package ru.bek.compshp.exception;

public class ConfigurationExportException extends BusinessException {
    public ConfigurationExportException(Long configId, Throwable cause) {
        super("CONFIGURATION_EXPORT_ERROR", 
            String.format("Failed to export configuration with ID %d: %s", configId, cause.getMessage()));
    }
} 