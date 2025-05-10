package ru.compshp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponentCompatibilityService {
    private final CompatibilityRuleRepository ruleRepository;
    private final ProductCompatibilityRepository compatibilityRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CompatibilityCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

    // Константы для валидации спецификаций
    private static final Map<ComponentType, Set<String>> REQUIRED_SPECS = Map.of(
        ComponentType.CPU, Set.of("socket", "tdp", "ram_type"),
        ComponentType.MOTHERBOARD, Set.of("socket", "formFactor", "ram_slots", "m2Slots"),
        ComponentType.GPU, Set.of("powerConsumption", "length", "pcie_version"),
        ComponentType.PSU, Set.of("wattage", "efficiency"),
        ComponentType.CASE, Set.of("formFactor", "maxGpuLength", "maxCpuCoolerHeight"),
        ComponentType.RAM, Set.of("type", "speed", "capacity"),
        ComponentType.STORAGE, Set.of("type", "capacity", "isM2")
    );

    public ComponentCompatibilityService(
            CompatibilityRuleRepository ruleRepository,
            ProductCompatibilityRepository compatibilityRepository,
            ProductRepository productRepository,
            ProductService productService,
            CompatibilityCacheRepository cacheRepository) {
        this.ruleRepository = ruleRepository;
        this.compatibilityRepository = compatibilityRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.cacheRepository = cacheRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Проверяет совместимость двух компонентов
     * Включает проверку технических характеристик и физической совместимости
     */
    public CompatibilityResult checkComponentsCompatibility(Product source, Product target) {
        CompatibilityResult result = new CompatibilityResult();
        
        // Проверяем валидность спецификаций
        if (!validateSpecs(source) || !validateSpecs(target)) {
            result.setCompatible(false);
            result.addError("Invalid component specifications");
            return result;
        }

        // Проверяем техническую совместимость
        if (!checkTechnicalCompatibility(source, target)) {
            result.setCompatible(false);
            result.addError("Technical compatibility check failed");
            return result;
        }

        // Проверяем физическую совместимость
        if (!checkPhysicalCompatibility(source, target)) {
            result.setCompatible(false);
            result.addError("Physical compatibility check failed");
            return result;
        }

        // Проверяем пограничные случаи
        checkBorderlineCases(source, target, result);

        return result;
    }

    /**
     * Проверяет валидность спецификаций компонента
     */
    private boolean validateSpecs(Product component) {
        if (component.getComponentType() == null || component.getSpecs() == null) {
            return false;
        }

        Set<String> requiredSpecs = REQUIRED_SPECS.get(component.getComponentType());
        if (requiredSpecs == null) {
            return true;
        }

        try {
            Map<String, Object> specs = objectMapper.readValue(component.getSpecs(), Map.class);
            return requiredSpecs.stream().allMatch(specs::containsKey);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Проверяет техническую совместимость компонентов
     */
    private boolean checkTechnicalCompatibility(Product source, Product target) {
        if (source.getComponentType() == null || target.getComponentType() == null) {
            return true;
        }

        List<CompatibilityRule> rules = ruleRepository.findBySourceTypeAndTargetType(
                source.getComponentType(), target.getComponentType());

        if (rules.isEmpty()) {
            return true;
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
     * Использует кеширование результатов
     */
    @Cacheable(value = "compatibility", key = "#newComponent.id + '-' + #existingComponents.![id].join(',')")
    public CompatibilityResult checkConfigurationCompatibility(Product newComponent, List<Product> existingComponents) {
        CompatibilityResult result = new CompatibilityResult();
        
        for (Product existing : existingComponents) {
            CompatibilityResult pairResult = checkComponentsCompatibility(newComponent, existing);
            if (!pairResult.isCompatible()) {
                result.setCompatible(false);
                result.getErrors().addAll(pairResult.getErrors());
                return result;
            }
            result.getWarnings().addAll(pairResult.getWarnings());
        }

        return result;
    }

    /**
     * Проверяет пограничные случаи совместимости
     */
    private void checkBorderlineCases(Product source, Product target, CompatibilityResult result) {
        try {
            Map<String, Object> sourceSpecs = objectMapper.readValue(source.getSpecs(), Map.class);
            Map<String, Object> targetSpecs = objectMapper.readValue(target.getSpecs(), Map.class);

            // Проверка мощности БП
            if (source.getComponentType() == ComponentType.PSU) {
                int psuWattage = (int) sourceSpecs.get("wattage");
                int requiredWattage = calculateRequiredWattage(target);
                
                if (psuWattage < requiredWattage * 1.2) {
                    result.addWarning("PSU wattage is close to required minimum");
                }
            }

            // Другие пограничные случаи...
        } catch (Exception e) {
            result.addWarning("Could not check borderline cases");
        }
    }

    /**
     * Рассчитывает требуемую мощность для компонента
     */
    private int calculateRequiredWattage(Product component) {
        try {
            Map<String, Object> specs = objectMapper.readValue(component.getSpecs(), Map.class);
            
            switch (component.getComponentType()) {
                case CPU:
                    return (int) specs.get("tdp");
                case GPU:
                    return (int) specs.get("powerConsumption");
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Получает список совместимых компонентов для указанного типа
     * Учитывает как технические характеристики, так и физическую совместимость
     */
    public List<Product> getCompatibleComponents(Product source, ComponentType targetType) {
        return productRepository.findByComponentType(targetType).stream()
                .filter(target -> 
                    checkComponentsCompatibility(source, target) &&
                    checkPhysicalCompatibility(source, target)
                )
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

    public ProductCompatibility getById(Long id) {
        return compatibilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductCompatibility not found"));
    }

    public boolean checkPhysicalCompatibility(Product component1, Product component2) {
        try {
            Map<String, Object> specs1 = objectMapper.readValue(component1.getPhysicalSpecs(), Map.class);
            Map<String, Object> specs2 = objectMapper.readValue(component2.getPhysicalSpecs(), Map.class);
            
            // Проверка размеров корпуса и материнской платы
            if (component1.getComponentType() == ComponentType.CASE && 
                component2.getComponentType() == ComponentType.MOTHERBOARD) {
                String caseFormFactor = (String) specs1.get("formFactor");
                String mbFormFactor = (String) specs2.get("formFactor");
                return caseFormFactor.equals(mbFormFactor);
            }
            
            // Проверка слотов M.2
            if (component1.getComponentType() == ComponentType.MOTHERBOARD && 
                component2.getComponentType() == ComponentType.STORAGE) {
                int m2Slots = (int) specs1.get("m2Slots");
                boolean isM2 = (boolean) specs2.get("isM2");
                return !isM2 || m2Slots > 0;
            }
            
            // Проверка длины видеокарты
            if (component1.getComponentType() == ComponentType.CASE && 
                component2.getComponentType() == ComponentType.GPU) {
                int maxGpuLength = (int) specs1.get("maxGpuLength");
                int gpuLength = (int) specs2.get("length");
                return gpuLength <= maxGpuLength;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Product> getRecommendedComponents(List<Product> selectedComponents, ComponentType targetType) {
        return productRepository.findByComponentType(targetType).stream()
                .filter(component -> selectedComponents.stream()
                        .allMatch(selected -> checkPhysicalCompatibility(selected, component)))
                .sorted((c1, c2) -> {
                    // Сортировка по рейтингу и цене
                    int ratingCompare = Double.compare(c2.getRating(), c1.getRating());
                    if (ratingCompare != 0) return ratingCompare;
                    return c1.getPrice().compareTo(c2.getPrice());
                })
                .collect(Collectors.toList());
    }

    public Map<ComponentType, List<Product>> getCompatibleComponentsByType(Product baseComponent) {
        Map<ComponentType, List<Product>> result = new HashMap<>();
        for (ComponentType type : ComponentType.values()) {
            if (type != baseComponent.getComponentType()) {
                result.put(type, getCompatibleComponents(baseComponent, type));
            }
        }
        return result;
    }
} 