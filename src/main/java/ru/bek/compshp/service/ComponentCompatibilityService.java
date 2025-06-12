package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.exception.ResourceNotFoundException;
import ru.bek.compshp.model.CompatibilityRule;
import ru.bek.compshp.model.ConfigComponent;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.repository.CompatibilityRuleRepository;
import ru.bek.compshp.repository.ProductRepository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Сервис для проверки совместимости компонентов ПК
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentCompatibilityService {
    
    // Константы операторов сравнения больше не нужны - используем enum
    
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
        // Если один из компонентов - периферийное устройство (component_type == null),
        // считаем, что оно совместимо со всеми компонентами
        if (source.getComponentType() == null || target.getComponentType() == null) {
            return true;
        }
        
        ComponentType sourceType = source.getComponentType();
        ComponentType targetType = target.getComponentType();
        
        // Если компоненты одного типа, проверяем, поддерживают ли они множественные экземпляры
        if (sourceType == targetType) {
            return sourceType.allowsMultiple();
        }
        
        // Ищем правила совместимости для этих типов компонентов
        List<CompatibilityRule> rules = compatibilityRuleRepository.findBySourceTypeAndTargetType(sourceType, targetType);
        
        // Если правил нет, проверяем в обратном порядке (target -> source)
        if (rules.isEmpty()) {
            rules = compatibilityRuleRepository.findBySourceTypeAndTargetType(targetType, sourceType);
        }
        
        // Если правил совместимости нет вообще, считаем компоненты совместимыми
        if (rules.isEmpty()) {
            return true;
        }
        
        // Проверяем каждое правило
        for (CompatibilityRule rule : rules) {
            // Определяем, какой компонент является источником, а какой целью
            Product sourceProduct = (rule.getSourceType() == sourceType) ? source : target;
            Product targetProduct = (rule.getSourceType() == sourceType) ? target : source;
            
            // Проверяем совместимость по правилу
            if (!isCompatibleByRule(sourceProduct, targetProduct, rule)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Проверяет совместимость компонентов по конкретному правилу
     * @param sourceProduct исходный компонент
     * @param targetProduct целевой компонент
     * @param rule правило совместимости
     * @return true, если компоненты совместимы по данному правилу
     */
    private boolean isCompatibleByRule(Product sourceProduct, Product targetProduct, CompatibilityRule rule) {
        String sourceProperty = rule.getSourceProperty();
        String targetProperty = rule.getTargetProperty();
        
        String sourceValue = sourceProduct.getSpec(sourceProperty);
        String targetValue = targetProduct.getSpec(targetProperty);
        
        // Если у одного из компонентов нет нужной характеристики, пропускаем правило
        if (sourceValue.isEmpty() || targetValue.isEmpty()) {
            return true; // Считаем совместимыми, если нет данных для проверки
        }
        
        // Проверяем соответствие правилу
        CompatibilityRule.Operator operator = CompatibilityRule.Operator.fromString(rule.getComparisonOperator());
        switch (operator) {
            case EQUALS:
                return sourceValue.equals(targetValue);
            case NOT_EQUALS:
                return !sourceValue.equals(targetValue);
            case GREATER_THAN:
                try {
                    double sourceNum = Double.parseDouble(sourceValue);
                    double targetNum = Double.parseDouble(targetValue);
                    return sourceNum > targetNum;
                } catch (NumberFormatException e) {
                    return true; // Если не числа, пропускаем правило
                }
            case LESS_THAN:
                try {
                    double sourceNum = Double.parseDouble(sourceValue);
                    double targetNum = Double.parseDouble(targetValue);
                    return sourceNum < targetNum;
                } catch (NumberFormatException e) {
                    return true; // Если не числа, пропускаем правило
                }
            case LESS_THAN_EQUALS:
                try {
                    double sourceNum = Double.parseDouble(sourceValue);
                    double targetNum = Double.parseDouble(targetValue);
                    return sourceNum <= targetNum;
                } catch (NumberFormatException e) {
                    return true; // Если не числа, пропускаем правило
                }
            case GREATER_THAN_EQUALS:
                try {
                    double sourceNum = Double.parseDouble(sourceValue);
                    double targetNum = Double.parseDouble(targetValue);
                    return sourceNum >= targetNum;
                } catch (NumberFormatException e) {
                    return true; // Если не числа, пропускаем правило
                }
            case CONTAINS:
                return sourceValue.contains(targetValue);
            case BALANCED:
                if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                    return evaluateBalancedCondition(rule.getValueModifier(), sourceProduct, targetProduct);
                }
                return true;
            case CONDITION:
                if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                    return evaluateCondition(rule.getValueModifier(), sourceProduct, targetProduct);
                }
                return true;
            default:
                return true;
        }
    }
    
    /**
     * Оценивает условие баланса между компонентами
     * @param expression выражение для оценки
     * @param source первый компонент
     * @param target второй компонент
     * @return true, если условие баланса выполнено
     */
    private boolean evaluateBalancedCondition(String expression, Product source, Product target) {
        try {
            // Пример выражения: "CPU.performance * 0.8 <= GPU.performance AND CPU.performance * 1.2 >= GPU.performance"
            // Разбиваем на части по AND/OR
            String[] parts = expression.split("\\s+AND\\s+|\\s+OR\\s+");
            boolean isAnd = expression.contains(" AND ");
            
            boolean result = isAnd; // Для AND начальное значение true, для OR - false
            
            for (String part : parts) {
                boolean partResult = evaluateSimpleExpression(part, source, target);
                if (isAnd) {
                    result = result && partResult;
                } else {
                    result = result || partResult;
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Ошибка при оценке условия баланса: {}", expression, e);
            return true; // По умолчанию считаем, что условие выполнено
        }
    }
    
    /**
     * Оценивает условное выражение
     * @param expression выражение для оценки
     * @param source первый компонент
     * @param target второй компонент
     * @return true, если условие выполнено
     */
    private boolean evaluateCondition(String expression, Product source, Product target) {
        try {
            // Пример выражения: "IF CPU.workload_type = \"ML\" THEN GPU.memory >= 12"
            if (expression.startsWith("IF ")) {
                // Извлекаем условие и следствие
                Pattern pattern = Pattern.compile("IF\\s+(.+?)\\s+THEN\\s+(.+)");
                Matcher matcher = pattern.matcher(expression);
                
                if (matcher.find()) {
                    String condition = matcher.group(1);
                    String consequence = matcher.group(2);
                    
                    // Оцениваем условие
                    boolean conditionMet = evaluateSimpleExpression(condition, source, target);
                    
                    // Если условие выполнено, проверяем следствие
                    if (conditionMet) {
                        return evaluateSimpleExpression(consequence, source, target);
                    } else {
                        return true; // Если условие не выполнено, правило не применяется
                    }
                }
            }
            
            // Если формат не соответствует IF-THEN, оцениваем как простое выражение
            return evaluateSimpleExpression(expression, source, target);
        } catch (Exception e) {
            log.error("Ошибка при оценке условного выражения: {}", expression, e);
            return true; // По умолчанию считаем, что условие выполнено
        }
    }
    
    /**
     * Оценивает простое выражение сравнения
     * @param expression выражение для оценки
     * @param source первый компонент
     * @param target второй компонент
     * @return true, если выражение истинно
     */
    private boolean evaluateSimpleExpression(String expression, Product source, Product target) {
        try {
            // Поддерживаемые операторы: =, !=, >, <, >=, <=, IN, CONTAINS
            Pattern pattern = Pattern.compile("(.+?)\\s*(=|!=|>|<|>=|<=|IN|CONTAINS)\\s*(.+)");
            Matcher matcher = pattern.matcher(expression);
            
            if (matcher.find()) {
                String leftPart = matcher.group(1).trim();
                String operator = matcher.group(2).trim();
                String rightPart = matcher.group(3).trim();
                
                // Получаем значения из компонентов
                Object leftValue = extractValue(leftPart, source, target);
                Object rightValue = extractValue(rightPart, source, target);
                
                // Если не удалось извлечь значения, считаем условие выполненным
                if (leftValue == null || rightValue == null) {
                    return true;
                }
                
                // Оцениваем выражение в зависимости от оператора
                return compareValues(leftValue, operator, rightValue);
            }
            
            return true; // Если не удалось разобрать выражение, считаем условие выполненным
        } catch (Exception e) {
            log.error("Ошибка при оценке простого выражения: {}", expression, e);
            return true; // По умолчанию считаем, что условие выполнено
        }
    }
    
    /**
     * Извлекает значение из компонента по указанному пути
     * @param path путь к значению (например, "CPU.cores" или "12")
     * @param source первый компонент
     * @param target второй компонент
     * @return извлеченное значение или null, если не удалось извлечь
     */
    private Object extractValue(String path, Product source, Product target) {
        try {
            // Проверяем, является ли значение числом
            if (path.matches("-?\\d+(\\.\\d+)?")) {
                return Double.parseDouble(path);
            }
            
            // Проверяем, является ли значение строкой в кавычках
            if (path.startsWith("\"") && path.endsWith("\"")) {
                return path.substring(1, path.length() - 1);
            }
            
            // Проверяем, является ли значение списком
            if (path.startsWith("[") && path.endsWith("]")) {
                String[] items = path.substring(1, path.length() - 1).split(",\\s*");
                List<String> list = new ArrayList<>();
                for (String item : items) {
                    if (item.startsWith("\"") && item.endsWith("\"")) {
                        list.add(item.substring(1, item.length() - 1));
                    } else {
                        list.add(item);
                    }
                }
                return list;
            }
            
            // Извлекаем значение из компонента
            if (path.contains(".")) {
                String[] parts = path.split("\\.");
                String componentType = parts[0];
                String property = parts[1];
                
                Product component = null;
                if (componentType.equalsIgnoreCase(source.getComponentType().toString())) {
                    component = source;
                } else if (componentType.equalsIgnoreCase(target.getComponentType().toString())) {
                    component = target;
                }
                
                if (component != null) {
                    String value = component.getSpec(property);
                    if (value.isEmpty()) {
                        return null;
                    }
                    
                    // Пытаемся преобразовать в число, если возможно
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        return value;
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Ошибка при извлечении значения из пути: {}", path, e);
            return null;
        }
    }
    
    /**
     * Сравнивает два значения с использованием указанного оператора
     * @param left левое значение
     * @param operator оператор сравнения
     * @param right правое значение
     * @return результат сравнения
     */
    private boolean compareValues(Object left, String operator, Object right) {
        try {
            // Если оба значения числа, выполняем числовое сравнение
            if (left instanceof Number && right instanceof Number) {
                double leftNum = ((Number) left).doubleValue();
                double rightNum = ((Number) right).doubleValue();
                
                switch (operator) {
                    case "=":
                        return leftNum == rightNum;
                    case "!=":
                        return leftNum != rightNum;
                    case ">":
                        return leftNum > rightNum;
                    case "<":
                        return leftNum < rightNum;
                    case ">=":
                        return leftNum >= rightNum;
                    case "<=":
                        return leftNum <= rightNum;
                    default:
                        return false;
                }
            }
            
            // Если оба значения строки, выполняем строковое сравнение
            if (left instanceof String && right instanceof String) {
                String leftStr = (String) left;
                String rightStr = (String) right;
                
                switch (operator) {
                    case "=":
                        return leftStr.equals(rightStr);
                    case "!=":
                        return !leftStr.equals(rightStr);
                    case "CONTAINS":
                        return leftStr.contains(rightStr);
                    case "IN":
                        return rightStr.contains(leftStr);
                    default:
                        return false;
                }
            }
            
            // Если правое значение список, проверяем вхождение левого значения в список
            if (right instanceof List) {
                List<?> rightList = (List<?>) right;
                
                if (operator.equals("IN")) {
                    for (Object item : rightList) {
                        if (item.toString().equals(left.toString())) {
                            return true;
                        }
                    }
                    return false;
                }
            }
            
            // По умолчанию считаем условие невыполненным
            return false;
        } catch (Exception e) {
            log.error("Ошибка при сравнении значений: {} {} {}", left, operator, right, e);
            return false;
        }
    }
    
    /**
     * Получает причину несовместимости двух компонентов
     * @param source первый компонент
     * @param target второй компонент
     * @return строка с объяснением причины несовместимости или null, если компоненты совместимы
     */
    public String getIncompatibilityReason(Product source, Product target) {
        ComponentType sourceType = source.getComponentType();
        ComponentType targetType = target.getComponentType();
        
        // Если компоненты одного типа
        if (sourceType == targetType) {
            return "Компоненты одного типа не могут быть использованы в одной конфигурации";
        }
        
        // Ищем правила совместимости для этих типов компонентов
        List<CompatibilityRule> rules = compatibilityRuleRepository
                .findBySourceTypeAndTargetType(sourceType, targetType);
        
        // Проверяем каждое правило
        for (CompatibilityRule rule : rules) {
            String sourceProperty = rule.getSourceProperty();
            String targetProperty = rule.getTargetProperty();
            
            String sourceValue = source.getSpec(sourceProperty);
            String targetValue = target.getSpec(targetProperty);
            
            // Если у одного из компонентов нет нужной характеристики, пропускаем правило
            if (sourceValue.isEmpty() || targetValue.isEmpty()) {
                continue;
            }
            
            // Проверяем соответствие правилу
            CompatibilityRule.Operator operator = CompatibilityRule.Operator.fromString(rule.getComparisonOperator());
            switch (operator) {
                case EQUALS:
                    if (!sourceValue.equals(targetValue)) {
                        return String.format("%s должен быть равен %s (текущие значения: %s и %s)",
                                sourceProperty, targetProperty, sourceValue, targetValue);
                    }
                    break;
                case NOT_EQUALS:
                    if (sourceValue.equals(targetValue)) {
                        return String.format("%s не должен быть равен %s (оба имеют значение %s)",
                                sourceProperty, targetProperty, sourceValue);
                    }
                    break;
                case GREATER_THAN:
                    try {
                        double sourceNum = Double.parseDouble(sourceValue);
                        double targetNum = Double.parseDouble(targetValue);
                        if (sourceNum <= targetNum) {
                            return String.format("%s должен быть больше %s (текущие значения: %s и %s)",
                                    sourceProperty, targetProperty, sourceValue, targetValue);
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
                            return String.format("%s должен быть меньше %s (текущие значения: %s и %s)",
                                    sourceProperty, targetProperty, sourceValue, targetValue);
                        }
                    } catch (NumberFormatException e) {
                        // Если не числа, пропускаем правило
                    }
                    break;
                case LESS_THAN_EQUALS:
                    try {
                        double sourceNum = Double.parseDouble(sourceValue);
                        double targetNum = Double.parseDouble(targetValue);
                        if (sourceNum > targetNum) {
                            return String.format("%s должен быть меньше или равен %s (текущие значения: %s и %s)",
                                    sourceProperty, targetProperty, sourceValue, targetValue);
                        }
                    } catch (NumberFormatException e) {
                        // Если не числа, пропускаем правило
                    }
                    break;
                case GREATER_THAN_EQUALS:
                    try {
                        double sourceNum = Double.parseDouble(sourceValue);
                        double targetNum = Double.parseDouble(targetValue);
                        if (sourceNum < targetNum) {
                            return String.format("%s должен быть больше или равен %s (текущие значения: %s и %s)",
                                    sourceProperty, targetProperty, sourceValue, targetValue);
                        }
                    } catch (NumberFormatException e) {
                        // Если не числа, пропускаем правило
                    }
                    break;
                case CONTAINS:
                    if (!sourceValue.contains(targetValue)) {
                        return String.format("%s должен содержать %s (текущие значения: %s и %s)",
                                sourceProperty, targetProperty, sourceValue, targetValue);
                    }
                    break;
                case BALANCED:
                    if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                        boolean isBalanced = evaluateBalancedCondition(rule.getValueModifier(), source, target);
                        if (!isBalanced) {
                            return String.format("Компоненты не сбалансированы по производительности: %s", rule.getDescription());
                        }
                    }
                    break;
                case CONDITION:
                    if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                        boolean conditionMet = evaluateCondition(rule.getValueModifier(), source, target);
                        if (!conditionMet) {
                            return String.format("Не выполнено условие: %s", rule.getDescription());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        
        // Также проверяем в обратном направлении
        rules = compatibilityRuleRepository.findBySourceTypeAndTargetType(targetType, sourceType);
        for (CompatibilityRule rule : rules) {
            String sourceProperty = rule.getSourceProperty();
            String targetProperty = rule.getTargetProperty();
            
            String targetValue = target.getSpec(sourceProperty); // Меняем местами source и target
            String sourceValue = source.getSpec(targetProperty);
            
            if (sourceValue.isEmpty() || targetValue.isEmpty()) {
                continue;
            }
            
            // Проверка правил...
            // (для краткости повторный код опущен)
        }
        
        // Если все правила прошли проверку, компоненты совместимы
        return null;
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
        
        // Если это периферийное устройство (component_type == null),
        // считаем, что оно совместимо со всеми компонентами
        if (product.getComponentType() == null) {
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
        
        // Если это периферийное устройство, оно всегда совместимо
        if (product.getComponentType() == null) {
            return result;
        }
        
        for (ConfigComponent component : existingComponents) {
            Product existingProduct = component.getProduct();
            
            // Если существующий компонент - периферия, пропускаем проверку
            if (existingProduct.getComponentType() == null) {
                continue;
            }
            
            // Если компоненты одного типа, проверяем, поддерживает ли тип множественные экземпляры
            if (product.getComponentType() == existingProduct.getComponentType()) {
                if (!product.getComponentType().allowsMultiple()) {
                    result.put(existingProduct, "Компоненты данного типа не могут быть добавлены в конфигурацию более одного раза");
                }
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
                
                String sourceValue = source.getSpec(rule.getSourceProperty());
                String targetValue = target.getSpec(rule.getTargetProperty());
                
                if (sourceValue.isEmpty() || targetValue.isEmpty()) {
                    continue;
                }
                
                boolean isCompatible = true;
                CompatibilityRule.Operator operator = CompatibilityRule.Operator.fromString(rule.getComparisonOperator());
                switch (operator) {
                    case EQUALS: {
                        isCompatible = sourceValue.equals(targetValue);
                        break;
                    }
                    case NOT_EQUALS: {
                        isCompatible = !sourceValue.equals(targetValue);
                        break;
                    }
                    case GREATER_THAN: {
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum > targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    }
                    case LESS_THAN: {
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum < targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    }
                    case LESS_THAN_EQUALS: {
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum <= targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    }
                    case GREATER_THAN_EQUALS: {
                        try {
                            double sourceNum = Double.parseDouble(sourceValue);
                            double targetNum = Double.parseDouble(targetValue);
                            isCompatible = sourceNum >= targetNum;
                        } catch (NumberFormatException e) {
                            // Пропускаем
                        }
                        break;
                    }
                    case CONTAINS: {
                        isCompatible = sourceValue.contains(targetValue);
                        break;
                    }
                    case BALANCED:
                        if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                            boolean isBalanced = evaluateBalancedCondition(rule.getValueModifier(), source, target);
                            if (!isBalanced) {
                                isCompatible = false;
                            }
                        }
                        break;
                    case CONDITION:
                        if (rule.getValueModifier() != null && !rule.getValueModifier().isEmpty()) {
                            boolean conditionMet = evaluateCondition(rule.getValueModifier(), source, target);
                            if (!conditionMet) {
                                isCompatible = false;
                            }
                        }
                        break;
                    default: {
                        break;
                    }
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
            if (existingRule.getSourceProperty().equals(rule.getSourceProperty()) &&
                existingRule.getTargetProperty().equals(rule.getTargetProperty())) {
                // Если оператор тот же или противоположный
                if (isConflictingOperator(existingRule.getComparisonOperator(), rule.getComparisonOperator())) {
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
    private boolean isConflictingOperator(String op1Str, String op2Str) {
        CompatibilityRule.Operator op1 = CompatibilityRule.Operator.fromString(op1Str);
        CompatibilityRule.Operator op2 = CompatibilityRule.Operator.fromString(op2Str);
        
        // Простая проверка на конфликт - если операторы одинаковые или противоположные
        if (op1 == op2) {
            return true;
        }
        
        // Проверка противоположных операторов
        if ((op1 == CompatibilityRule.Operator.EQUALS && op2 == CompatibilityRule.Operator.NOT_EQUALS) ||
            (op1 == CompatibilityRule.Operator.NOT_EQUALS && op2 == CompatibilityRule.Operator.EQUALS) ||
            (op1 == CompatibilityRule.Operator.GREATER_THAN && op2 == CompatibilityRule.Operator.LESS_THAN_EQUALS) ||
            (op1 == CompatibilityRule.Operator.LESS_THAN_EQUALS && op2 == CompatibilityRule.Operator.GREATER_THAN) ||
            (op1 == CompatibilityRule.Operator.LESS_THAN && op2 == CompatibilityRule.Operator.GREATER_THAN_EQUALS) ||
            (op1 == CompatibilityRule.Operator.GREATER_THAN_EQUALS && op2 == CompatibilityRule.Operator.LESS_THAN)) {
            return true;
        }
        
        return false;
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
            rule.setSourceProperty("compatibility");
            rule.setTargetProperty("compatibility");
            rule.setComparisonOperator("EQUALS");
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