package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.CompatibilityRuleRepository;
import ru.compshp.repository.ProductCompatibilityRepository;
import ru.compshp.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponentCompatibilityService {
    private final CompatibilityRuleRepository ruleRepository;
    private final ProductCompatibilityRepository compatibilityRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ComponentCompatibilityService(
            CompatibilityRuleRepository ruleRepository,
            ProductCompatibilityRepository compatibilityRepository,
            ProductRepository productRepository,
            ProductService productService) {
        this.ruleRepository = ruleRepository;
        this.compatibilityRepository = compatibilityRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Проверяет совместимость двух компонентов
     */
    public boolean checkComponentsCompatibility(Product source, Product target) {
        if (source.getComponentType() == null || target.getComponentType() == null) {
            return true; // Если тип компонента не указан, считаем совместимыми
        }

        List<CompatibilityRule> rules = ruleRepository.findBySourceTypeAndTargetType(
                source.getComponentType(), target.getComponentType());

        if (rules.isEmpty()) {
            return true; // Если правил нет, считаем совместимыми
        }

        try {
            Map<String, Object> sourceSpecs = objectMapper.readValue(source.getSpecs(), Map.class);
            Map<String, Object> targetSpecs = objectMapper.readValue(target.getSpecs(), Map.class);

            return rules.stream().allMatch(rule -> {
                try {
                    Map<String, Object> conditions = objectMapper.readValue(rule.getCheckCondition(), Map.class);
                    return evaluateConditions(conditions, sourceSpecs, targetSpecs);
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверяет совместимость компонента с существующей конфигурацией
     */
    public boolean checkConfigurationCompatibility(Product newComponent, List<Product> existingComponents) {
        return existingComponents.stream()
                .allMatch(existing -> checkComponentsCompatibility(newComponent, existing) &&
                        checkComponentsCompatibility(existing, newComponent));
    }

    /**
     * Получает список совместимых компонентов для указанного типа
     */
    public List<Product> getCompatibleComponents(Product source, ComponentType targetType) {
        return productRepository.findByComponentType(targetType).stream()
                .filter(target -> checkComponentsCompatibility(source, target))
                .collect(Collectors.toList());
    }

    /**
     * Создает или обновляет правило совместимости
     */
    @Transactional
    public CompatibilityRule saveCompatibilityRule(CompatibilityRule rule) {
        validateRule(rule);
        return ruleRepository.save(rule);
    }

    /**
     * Проверяет и обновляет совместимость между продуктами
     */
    @Transactional
    public void updateProductCompatibility(Product source, Product target) {
        boolean isCompatible = checkComponentsCompatibility(source, target);
        
        ProductCompatibility compatibility = compatibilityRepository
                .findBySourceProductAndTargetProduct(source, target)
                .orElse(new ProductCompatibility());

        compatibility.setSourceProduct(source);
        compatibility.setTargetProduct(target);
        compatibility.setValid(isCompatible);
        compatibility.setCreatedAt(java.time.LocalDateTime.now());

        compatibilityRepository.save(compatibility);
    }

    /**
     * Оценивает условия совместимости
     */
    private boolean evaluateConditions(Map<String, Object> conditions, 
                                     Map<String, Object> sourceSpecs, 
                                     Map<String, Object> targetSpecs) {
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith("source.")) {
                String sourceKey = key.substring(7);
                if (!evaluateCondition(sourceSpecs.get(sourceKey), value)) {
                    return false;
                }
            } else if (key.startsWith("target.")) {
                String targetKey = key.substring(7);
                if (!evaluateCondition(targetSpecs.get(targetKey), value)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Оценивает отдельное условие совместимости
     */
    private boolean evaluateCondition(Object actual, Object expected) {
        if (actual == null || expected == null) {
            return false;
        }

        if (expected instanceof Map) {
            Map<String, Object> conditions = (Map<String, Object>) expected;
            String operator = (String) conditions.get("operator");
            Object value = conditions.get("value");

            switch (operator) {
                case "equals":
                    return actual.equals(value);
                case "notEquals":
                    return !actual.equals(value);
                case "greaterThan":
                    return compareValues(actual, value) > 0;
                case "lessThan":
                    return compareValues(actual, value) < 0;
                case "greaterThanOrEqual":
                    return compareValues(actual, value) >= 0;
                case "lessThanOrEqual":
                    return compareValues(actual, value) <= 0;
                case "contains":
                    return actual.toString().contains(value.toString());
                case "startsWith":
                    return actual.toString().startsWith(value.toString());
                case "endsWith":
                    return actual.toString().endsWith(value.toString());
                case "in":
                    if (value instanceof List) {
                        return ((List<?>) value).contains(actual);
                    }
                    return false;
                default:
                    return false;
            }
        }

        return actual.equals(expected);
    }

    /**
     * Сравнивает значения для числовых и строковых типов
     */
    @SuppressWarnings("unchecked")
    private int compareValues(Object actual, Object expected) {
        if (actual instanceof Number && expected instanceof Number) {
            return Double.compare(((Number) actual).doubleValue(), ((Number) expected).doubleValue());
        }
        return actual.toString().compareTo(expected.toString());
    }

    /**
     * Валидирует правило совместимости
     */
    private void validateRule(CompatibilityRule rule) {
        try {
            Map<String, Object> conditions = objectMapper.readValue(rule.getCheckCondition(), Map.class);
            for (Map.Entry<String, Object> entry : conditions.entrySet()) {
                if (!entry.getKey().startsWith("source.") && !entry.getKey().startsWith("target.")) {
                    throw new IllegalArgumentException("Invalid condition key: " + entry.getKey());
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON condition format", e);
        }
    }

    /**
     * Получает все правила совместимости для указанного типа компонента
     */
    public List<CompatibilityRule> getRulesForComponentType(ComponentType componentType) {
        return ruleRepository.findBySourceTypeOrTargetType(componentType, componentType);
    }

    /**
     * Проверяет конфликты правил
     */
    public List<String> checkRuleConflicts(CompatibilityRule newRule) {
        List<String> conflicts = new ArrayList<>();
        List<CompatibilityRule> existingRules = ruleRepository
                .findBySourceTypeAndTargetType(newRule.getSourceType(), newRule.getTargetType());

        for (CompatibilityRule existingRule : existingRules) {
            if (existingRule.getId().equals(newRule.getId())) {
                continue;
            }

            try {
                Map<String, Object> newConditions = objectMapper.readValue(newRule.getCheckCondition(), Map.class);
                Map<String, Object> existingConditions = objectMapper.readValue(existingRule.getCheckCondition(), Map.class);

                // Проверяем противоречивые условия
                for (String key : newConditions.keySet()) {
                    if (existingConditions.containsKey(key) && 
                        !newConditions.get(key).equals(existingConditions.get(key))) {
                        conflicts.add("Conflict in condition: " + key);
                    }
                }
            } catch (Exception e) {
                conflicts.add("Error comparing rules: " + e.getMessage());
            }
        }

        return conflicts;
    }

    public Product getProductById(Long id) {
        return productService.getById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }
} 