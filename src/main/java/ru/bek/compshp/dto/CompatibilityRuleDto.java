package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.model.enums.ComponentType;

/**
 * DTO для передачи данных о правиле совместимости
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityRuleDto {
    
    private Long id;
    private String sourceType;
    private String targetType;
    private String ruleType;
    private String sourceProperty;
    private String targetProperty;
    private String comparisonOperator;
    private String valueModifier;
    private String description;
    private Boolean isActive;
} 