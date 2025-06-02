package ru.bek.compshp.exception;

public class ConfigurationImportException extends BusinessException {
    public ConfigurationImportException(Throwable cause) {
        super("CONFIGURATION_IMPORT_ERROR", 
            String.format("Failed to import configuration: %s", cause.getMessage()));
    }
} 