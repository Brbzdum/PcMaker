package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.CompatibilityRuleRepository;
import ru.compshp.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для проверки совместимости компонентов
 * Основные функции:
 * - Проверка совместимости компонентов
 * - Кэширование результатов проверок
 * - Расчет энергопотребления
 * - Управление правилами совместимости
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CompatibilityService {
    private final CompatibilityRuleRepository ruleRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public boolean checkCompatibility(Product product, List<Product> existingComponents) {
        return existingComponents.stream()
                .allMatch(existing -> isCompatible(product, existing));
    }

    public Map<String, String> getCompatibilityIssues(PCConfiguration config) {
        Map<String, String> issues = new HashMap<>();
        List<Product> components = config.getComponents().stream()
                .map(ConfigComponent::getProduct)
                .collect(Collectors.toList());

        // Проверка наличия обязательных компонентов
        Set<ComponentType> requiredTypes = Set.of(ComponentType.CPU, ComponentType.MB, ComponentType.RAM, ComponentType.PSU);
        Set<ComponentType> presentTypes = components.stream()
                .map(Product::getComponentType)
                .collect(Collectors.toSet());

        requiredTypes.stream()
                .filter(type -> !presentTypes.contains(type))
                .forEach(type -> issues.put(type.name(), "Missing required component"));

        // Проверка совместимости между компонентами
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (!isCompatible(components.get(i), components.get(j))) {
                    issues.put(
                        components.get(i).getComponentType() + " - " + components.get(j).getComponentType(),
                        "Incompatible components"
                    );
                }
            }
        }

        return issues;
    }

    public List<Product> getCompatibleComponents(Product baseComponent, ComponentType targetType) {
        List<CompatibilityRule> rules = ruleRepository.findBySourceTypeAndTargetType(
                baseComponent.getComponentType(), targetType);

        return productRepository.findByComponentType(targetType).stream()
                .filter(product -> isCompatibleWithRules(product, rules))
                .collect(Collectors.toList());
    }

    public boolean isPowerSupplySufficient(PCConfiguration config) {
        int totalPower = config.getComponents().stream()
                .mapToInt(this::getComponentPowerConsumption)
                .sum();

        return config.getComponents().stream()
                .filter(c -> c.getProduct().getComponentType() == ComponentType.PSU)
                .mapToInt(this::getComponentPowerOutput)
                .anyMatch(power -> power >= totalPower);
    }

    private boolean isCompatible(Product product1, Product product2) {
        List<CompatibilityRule> rules = ruleRepository.findBySourceTypeAndTargetType(
                product1.getComponentType(), product2.getComponentType());

        return rules.stream()
                .allMatch(rule -> checkRuleCompatibility(product1, product2, rule));
    }

    private boolean isCompatibleWithRules(Product product, List<CompatibilityRule> rules) {
        return rules.stream()
                .allMatch(rule -> checkRuleCompatibility(null, product, rule));
    }

    private boolean checkRuleCompatibility(Product source, Product target, CompatibilityRule rule) {
        try {
            Map<String, Object> sourceSpecs = source != null ? 
                    objectMapper.readValue(source.getSpecs(), Map.class) : 
                    new HashMap<>();
            Map<String, Object> targetSpecs = objectMapper.readValue(target.getSpecs(), Map.class);
            Map<String, Object> ruleConditions = objectMapper.readValue(rule.getCheckCondition(), Map.class);

            return ruleConditions.entrySet().stream()
                    .allMatch(entry -> {
                        Object sourceValue = sourceSpecs.get(entry.getKey());
                        Object targetValue = targetSpecs.get(entry.getKey());
                        return sourceValue != null && targetValue != null && 
                               sourceValue.equals(entry.getValue()) && 
                               targetValue.equals(entry.getValue());
                    });
        } catch (Exception e) {
            return false;
        }
    }

    private int getComponentPowerConsumption(ConfigComponent component) {
        try {
            Map<String, Object> specs = objectMapper.readValue(component.getProduct().getSpecs(), Map.class);
            return (int) specs.getOrDefault("power_consumption", 0) * component.getQuantity();
        } catch (Exception e) {
            return 0;
        }
    }

    private int getComponentPowerOutput(ConfigComponent component) {
        try {
            Map<String, Object> specs = objectMapper.readValue(component.getProduct().getSpecs(), Map.class);
            return (int) specs.getOrDefault("power_output", 0);
        } catch (Exception e) {
            return 0;
        }
    }
} 