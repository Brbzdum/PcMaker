package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.dto.CompatibilityRuleDto;
import ru.bek.compshp.model.CompatibilityRule;
import ru.bek.compshp.model.enums.ComponentType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между CompatibilityRule и CompatibilityRuleDto
 */
@Component
public class CompatibilityRuleMapper {

    /**
     * Преобразует сущность CompatibilityRule в DTO
     * @param rule сущность
     * @return DTO
     */
    public CompatibilityRuleDto toDto(CompatibilityRule rule) {
        if (rule == null) return null;
        
        return CompatibilityRuleDto.builder()
                .id(rule.getId())
                .sourceType(rule.getSourceType() != null ? rule.getSourceType().name() : null)
                .targetType(rule.getTargetType() != null ? rule.getTargetType().name() : null)
                .ruleType(rule.getRuleType() != null ? rule.getRuleType().name() : null)
                .sourceProperty(rule.getSourceProperty())
                .targetProperty(rule.getTargetProperty())
                .comparisonOperator(rule.getComparisonOperator())
                .valueModifier(rule.getValueModifier())
                .description(rule.getDescription())
                .isActive(rule.getIsActive())
                .build();
    }
    
    /**
     * Преобразует список сущностей CompatibilityRule в список DTO
     * @param rules список сущностей
     * @return список DTO
     */
    public List<CompatibilityRuleDto> toDtoList(List<CompatibilityRule> rules) {
        if (rules == null) return null;
        return rules.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Преобразует DTO в сущность CompatibilityRule
     * @param dto DTO
     * @return сущность
     */
    public CompatibilityRule toEntity(CompatibilityRuleDto dto) {
        if (dto == null) return null;
        
        CompatibilityRule rule = new CompatibilityRule();
        
        rule.setId(dto.getId());
        
        if (dto.getSourceType() != null) {
            try {
                rule.setSourceType(ComponentType.valueOf(dto.getSourceType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        if (dto.getTargetType() != null) {
            try {
                rule.setTargetType(ComponentType.valueOf(dto.getTargetType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        if (dto.getRuleType() != null) {
            try {
                rule.setRuleType(CompatibilityRule.RuleType.valueOf(dto.getRuleType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип правила
            }
        }
        
        rule.setSourceProperty(dto.getSourceProperty());
        rule.setTargetProperty(dto.getTargetProperty());
        rule.setComparisonOperator(dto.getComparisonOperator());
        rule.setValueModifier(dto.getValueModifier());
        rule.setDescription(dto.getDescription());
        rule.setIsActive(dto.getIsActive());
        
        return rule;
    }
    
    /**
     * Обновляет существующую сущность CompatibilityRule данными из DTO
     * @param rule существующая сущность
     * @param dto DTO с новыми данными
     * @return обновленная сущность
     */
    public CompatibilityRule updateEntityFromDto(CompatibilityRule rule, CompatibilityRuleDto dto) {
        if (rule == null || dto == null) return rule;
        
        if (dto.getSourceType() != null) {
            try {
                rule.setSourceType(ComponentType.valueOf(dto.getSourceType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        if (dto.getTargetType() != null) {
            try {
                rule.setTargetType(ComponentType.valueOf(dto.getTargetType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип компонента
            }
        }
        
        if (dto.getRuleType() != null) {
            try {
                rule.setRuleType(CompatibilityRule.RuleType.valueOf(dto.getRuleType()));
            } catch (IllegalArgumentException e) {
                // Игнорируем неправильный тип правила
            }
        }
        
        if (dto.getSourceProperty() != null) rule.setSourceProperty(dto.getSourceProperty());
        if (dto.getTargetProperty() != null) rule.setTargetProperty(dto.getTargetProperty());
        if (dto.getComparisonOperator() != null) rule.setComparisonOperator(dto.getComparisonOperator());
        if (dto.getValueModifier() != null) rule.setValueModifier(dto.getValueModifier());
        if (dto.getDescription() != null) rule.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) rule.setIsActive(dto.getIsActive());
        
        return rule;
    }
} 