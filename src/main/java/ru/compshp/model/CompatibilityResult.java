package ru.compshp.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения результатов проверки совместимости компонентов
 */
@Data
public class CompatibilityResult {
    private boolean compatible;
    private List<String> errors;
    private List<String> warnings;

    public CompatibilityResult() {
        this.compatible = true;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public void addError(String error) {
        this.errors.add(error);
        this.compatible = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
} 