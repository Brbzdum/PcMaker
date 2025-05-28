package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.exception.ResourceNotFoundException;
import ru.compshp.model.CompatibilityRule;
import ru.compshp.model.ConfigComponent;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.CompatibilityRuleRepository;
import ru.compshp.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для проверки совместимости компонентов ПК
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentCompatibilityServiceImpl {
    
    private final ProductRepository productRepository;
    private final CompatibilityRuleRepository compatibilityRuleRepository;
    
    /**
     * Получает продукт по его ID
     * @param id ID продукта
     * @return продукт
     * @throws ResourceNotFoundException если продукт не найден
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
    
    /**
     * Проверяет совместимость двух компонентов
     * @param source первый компонент
     * @param target второй компонент
     * @return true, если компоненты совместимы
     */
    public boolean checkComponentsCompatibility(Product source, Product target) {
        ComponentType sourceType = source.getComponentType();
        ComponentType targetType = target.getComponentType();
        
        // Если компоненты одного типа, они не могут быть совместимы для одной сборки
        if (sourceType == targetType) {
            return false;
        }
        
        // Ищем правила совместимости для этих типов компонентов
        List<CompatibilityRule> rules = compatibilityRuleRepository
                .findBySourceTypeAndTargetType(sourceType, targetType);
        
        // Если правил нет, считаем, что компоненты совместимы
        if (rules.isEmpty()) {
            return true;
        }
        
        // Проверяем каждое правило
        for (CompatibilityRule rule : rules) {
            String sourceSpecKey = rule.getSourceSpecKey();
            String targetSpecKey = rule.getTargetSpecKey();
            
            String sourceValue = source.getSpec(sourceSpecKey);
            String targetValue = target.getSpec(targetSpecKey);
            
            // Если у одного из компонентов нет нужной характеристики, пропускаем правило
            if (sourceValue.isEmpty() || targetValue.isEmpty()) {
                continue;
            }
            
            // Проверяем соответствие правилу
            switch (rule.getOperator()) {
                case EQUALS:
                    if (!sourceValue.equals(targetValue)) {
                        return false;
                    }
                    break;
                case NOT_EQUALS:
                    if (sourceValue.equals(targetValue)) {
                        return false;
                    }
                    break;
                case GREATER_THAN:
                    try {
                        double sourceNum = Double.parseDouble(sourceValue);
                        double targetNum = Double.parseDouble(targetValue);
                        if (sourceNum <= targetNum) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        // Если не числа, пропускаем правило
                    }
                    break;
                case LESS_THAN:
                    try {
                        double sourceNum = Double.parseDouble(sourceValue);
                        double targetNum = Double.parseDouble(targetValue);
                        if (sourceNum >= targetNum) {
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        // Если не числа, пропускаем правило
                    }
                    break;
                case CONTAINS:
                    if (!sourceValue.contains(targetValue)) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        
        // Если все правила прошли проверку, компоненты совместимы
        return true;
    }
    
    /**
     * Проверяет совместимость компонента с существующей конфигурацией
     * @param newComponent новый компонент
     * @param existingComponents список существующих компонентов
     * @return true, если новый компонент совместим со всеми существующими
     */
    public boolean checkConfigurationCompatibility(Product newComponent, List<Product> existingComponents) {
        if (existingComponents == null || existingComponents.isEmpty()) {
            return true;
        }
        
        // Проверяем совместимость с каждым существующим компонентом
        for (Product existingComponent : existingComponents) {
            if (!checkComponentsCompatibility(newComponent, existingComponent) && 
                !checkComponentsCompatibility(existingComponent, newComponent)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Проверяет совместимость компонента с существующей конфигурацией (через ConfigComponent)
     * @param product компонент
     * @param existingComponents список существующих компонентов конфигурации
     * @return true, если компонент совместим со всеми существующими
     */
    public boolean isCompatibleWithConfiguration(Product product, List<ConfigComponent> existingComponents) {
        if (existingComponents == null || existingComponents.isEmpty()) {
            return true;
        }
        
        // Преобразуем ConfigComponent в Product
        List<Product> products = existingComponents.stream()
                .map(ConfigComponent::getProduct)
                .collect(Collectors.toList());
        
        return checkConfigurationCompatibility(product, products);
    }
    
    /**
     * Получает детали несовместимости компонента с конфигурацией
     * @param product компонент
     * @param existingComponents список существующих компонентов конфигурации
     * @return карта с описаниями несовместимости для каждого конфликтующего компонента
     */
    public Map<Product, String> getIncompatibilityDetails(Product product, List<ConfigComponent> existingComponents) {
        Map<Product, String> result = new HashMap<>();
        
        if (existingComponents == null || existingComponents.isEmpty()) {
            return result;
        }
        
        for (ConfigComponent component : existingComponents) {
            Product existingProduct = component.getProduct();
            
            // Если компоненты одного типа
            if (product.getComponentType() == existingProduct.getComponentType()) {
                result.put(existingProduct, "Компоненты одного типа не могут быть добавлены в одну конфигурацию");
                continue;
            }
            
            // Проверяем правила совместимости
            List<CompatibilityRule> rules = compatibilityRuleRepository
                    .findBySourceTypeAndTargetType(product.getComponentType(), existingProduct.getComponentType());
            
            rules.addAll(compatibilityRuleRepository
                    .findBySourceTypeAndTargetType(existingProduct.getComponentType(), product.getComponentType()));
            
            for (CompatibilityRule rule : rules) {
                Product source, target;
                if (rule.getSourceType() == product.getComponentType()) {
                    source = product;
                    target = existingProduct;
                } else {
                    source = existingProduct;
                    target = product;
                }
                
                String sourceValue = source.getSpec(rule.getSourceSpecKey());
                String targetValue = target.getSpec(rule.getTargetSpecKey());
                
                if (sourceValue.isEmpty() || targetValue.isEmpty()) {
                    continue;
                }
                
                boolean isCompatible = true;
                switch (rule.getOperator()) {
                    case EQUALS:
                        isCompatible = sourceValue.equals(targetValue);
                        break;
                    case NOT_EQUALS:
                        isCompatible = !sourceValue.equals(targetValue);
                        break;
                    case GREATER_THAN:
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum > targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    case LESS_THAN:
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum < targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    case CONTAINS:
                        isCompatible = sourceValue.contains(targetValue);
                        break;
                    default:
                        break;
                }
                
                if (!isCompatible) {
                    result.put(existingProduct, rule.getDescription());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Получает список совместимых компонентов определенного типа
     * @param source базовый компонент
     * @param targetType тип искомых компонентов
     * @return список совместимых компонентов
     */
    public List<Product> getCompatibleComponents(Product source, ComponentType targetType) {
        List<Product> allComponentsOfType = productRepository.findByComponentType(targetType);
        
        return allComponentsOfType.stream()
                .filter(product -> checkComponentsCompatibility(source, product) 
                        || checkComponentsCompatibility(product, source))
                .collect(Collectors.toList());
    }
    
    /**
     * Проверяет наличие конфликтов в правиле совместимости
     * @param rule правило для проверки
     * @return список сообщений о конфликтах или пустой список, если конфликтов нет
     */
    public List<String> checkRuleConflicts(CompatibilityRule rule) {
        List<String> conflicts = new ArrayList<>();
        
        // Проверяем, что типы компонентов разные
        if (rule.getSourceType() == rule.getTargetType()) {
            conflicts.add("Правило не может применяться к компонентам одного типа");
        }
        
        // Проверяем на конфликты с существующими правилами
        List<CompatibilityRule> existingRules = compatibilityRuleRepository
                .findBySourceTypeAndTargetType(rule.getSourceType(), rule.getTargetType());
        
        for (CompatibilityRule existingRule : existingRules) {
            // Пропускаем, если это то же самое правило (при обновлении)
            if (rule.getId() != null && rule.getId().equals(existingRule.getId())) {
                continue;
            }
            
            // Проверяем на конфликт правил с одинаковыми полями
            if (existingRule.getSourceSpecKey().equals(rule.getSourceSpecKey()) &&
                existingRule.getTargetSpecKey().equals(rule.getTargetSpecKey())) {
                // Если оператор тот же или противоположный
                if (isConflictingOperator(existingRule.getOperator(), rule.getOperator())) {
                    conflicts.add("Конфликт с существующим правилом: " + existingRule.getDescription());
                }
            }
        }
        
        return conflicts;
    }
    
    /**
     * Проверяет, конфликтуют ли операторы
     * @param op1 первый оператор
     * @param op2 второй оператор
     * @return true, если операторы конфликтуют
     */
    private boolean isConflictingOperator(Enum<?> op1, Enum<?> op2) {
        // Простая проверка на конфликт - если операторы одинаковые или противоположные
        return op1 == op2 || 
               (op1.toString().equals("EQUALS") && op2.toString().equals("NOT_EQUALS")) ||
               (op1.toString().equals("NOT_EQUALS") && op2.toString().equals("EQUALS")) ||
               (op1.toString().equals("GREATER_THAN") && op2.toString().equals("LESS_THAN")) ||
               (op1.toString().equals("LESS_THAN") && op2.toString().equals("GREATER_THAN"));
    }
    
    /**
     * Сохраняет правило совместимости
     * @param rule правило для сохранения
     * @return сохраненное правило
     */
    @Transactional
    public CompatibilityRule saveCompatibilityRule(CompatibilityRule rule) {
        return compatibilityRuleRepository.save(rule);
    }
    
    /**
     * Получает список правил совместимости для определенного типа компонентов
     * @param componentType тип компонентов
     * @return список правил
     */
    public List<CompatibilityRule> getRulesForComponentType(ComponentType componentType) {
        List<CompatibilityRule> sourceRules = compatibilityRuleRepository.findBySourceType(componentType);
        List<CompatibilityRule> targetRules = compatibilityRuleRepository.findByTargetType(componentType);
        
        // Объединяем списки, избегая дубликатов
        Set<CompatibilityRule> allRules = new HashSet<>(sourceRules);
        allRules.addAll(targetRules);
        
        return new ArrayList<>(allRules);
    }
    
    /**
     * Обновляет информацию о совместимости между двумя продуктами
     * @param source первый продукт
     * @param target второй продукт
     */
    @Transactional
    public void updateProductCompatibility(Product source, Product target) {
        // Здесь можно реализовать обновление информации о совместимости
        // Например, создать запись в таблице совместимости или обновить характеристики
        
        // Пример реализации - создаем или обновляем правила на основе характеристик продуктов
        ComponentType sourceType = source.getComponentType();
        ComponentType targetType = target.getComponentType();
        
        // Предполагаем, что у продуктов есть характеристика "compatibility"
        String sourceCompat = source.getSpec("compatibility");
        String targetCompat = target.getSpec("compatibility");
        
        if (!sourceCompat.isEmpty() && !targetCompat.isEmpty()) {
            // Создаем правило совместимости
            CompatibilityRule rule = new CompatibilityRule();
            rule.setSourceType(sourceType);
            rule.setTargetType(targetType);
            rule.setSourceSpecKey("compatibility");
            rule.setTargetSpecKey("compatibility");
            rule.setOperator(Enum.valueOf(CompatibilityRule.Operator.class, "EQUALS"));
            rule.setDescription("Совместимость между " + source.getTitle() + " и " + target.getTitle());
            
            // Проверяем наличие конфликтов
            List<String> conflicts = checkRuleConflicts(rule);
            if (conflicts.isEmpty()) {
                saveCompatibilityRule(rule);
            } else {
                log.warn("Невозможно создать правило совместимости: {}", conflicts);
            }
        }
    }
} 