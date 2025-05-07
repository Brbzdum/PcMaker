package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.CompatibilityRuleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompatibilityRuleService {
    private final CompatibilityRuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

    public CompatibilityRuleService(CompatibilityRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<CompatibilityRule> getBySourceType(ComponentType sourceType) {
        return ruleRepository.findBySourceType(sourceType);
    }

    public List<CompatibilityRule> getByTargetType(ComponentType targetType) {
        return ruleRepository.findByTargetType(targetType);
    }

    public Optional<CompatibilityRule> getById(Long id) {
        return ruleRepository.findById(id);
    }

    @Transactional
    public CompatibilityRule save(CompatibilityRule rule) {
        return ruleRepository.save(rule);
    }

    @Transactional
    public void delete(Long id) {
        ruleRepository.deleteById(id);
    }

    public boolean checkCompatibility(ComponentType source, ComponentType target, String specsSource, String specsTarget) {
        List<CompatibilityRule> rules = ruleRepository.findBySourceTypeAndTargetType(source, target);
        
        if (rules.isEmpty()) {
            return true; // Если правил нет, считаем компоненты совместимыми
        }

        try {
            Map<String, Object> sourceSpecs = objectMapper.readValue(specsSource, Map.class);
            Map<String, Object> targetSpecs = objectMapper.readValue(specsTarget, Map.class);

            return rules.stream().allMatch(rule -> {
                try {
                    Map<String, Object> conditions = objectMapper.readValue(rule.getConditions(), Map.class);
                    return evaluateConditions(conditions, sourceSpecs, targetSpecs);
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateConditions(Map<String, Object> conditions, Map<String, Object> sourceSpecs, Map<String, Object> targetSpecs) {
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
                default:
                    return false;
            }
        }

        return actual.equals(expected);
    }

    @SuppressWarnings("unchecked")
    private int compareValues(Object actual, Object expected) {
        if (actual instanceof Number && expected instanceof Number) {
            return Double.compare(((Number) actual).doubleValue(), ((Number) expected).doubleValue());
        }
        return actual.toString().compareTo(expected.toString());
    }

    public List<CompatibilityRule> getRulesForComponent(ComponentType componentType) {
        return ruleRepository.findBySourceTypeOrTargetType(componentType, componentType);
    }

    @Transactional
    public CompatibilityRule createRule(ComponentType sourceType, ComponentType targetType, String conditions) {
        CompatibilityRule rule = new CompatibilityRule();
        rule.setSourceType(sourceType);
        rule.setTargetType(targetType);
        rule.setConditions(conditions);
        return ruleRepository.save(rule);
    }

    @Transactional
    public CompatibilityRule updateRule(Long id, String conditions) {
        return ruleRepository.findById(id)
                .map(rule -> {
                    rule.setConditions(conditions);
                    return ruleRepository.save(rule);
                })
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    public List<CompatibilityRule> getAllRules() {
        return ruleRepository.findAll();
    }
} 