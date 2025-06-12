package ru.bek.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bek.compshp.model.*;
import ru.bek.compshp.repository.ConfigComponentRepository;
import ru.bek.compshp.repository.PCConfigurationRepository;
import ru.bek.compshp.repository.ProductRepository;
import ru.bek.compshp.repository.UserRepository;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с конфигуратором ПК
 * Предоставляет методы для создания, редактирования и анализа конфигураций компьютеров
 */
@Service
@RequiredArgsConstructor
public class ConfiguratorService {
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ConfigComponentRepository configComponentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ComponentCompatibilityService compatibilityService;

    // Базовые операции с конфигурациями
    /**
     * Получает конфигурацию по ID в виде Optional
     * @param configId ID конфигурации
     * @return Optional с конфигурацией или пустой Optional
     */
    public Optional<PCConfiguration> getOptionalConfiguration(Long configId) {
        return pcConfigurationRepository.findById(configId);
    }
    
    /**
     * Получает конфигурацию по ID с загруженными компонентами
     * @param configId ID конфигурации
     * @return конфигурация с компонентами
     * @throws ResourceNotFoundException если конфигурация не найдена
     */
    @Transactional(readOnly = true)
    public PCConfiguration getConfigurationWithComponents(Long configId) {
        return pcConfigurationRepository.findByIdWithComponents(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
    }

    /**
     * Создает новую пустую конфигурацию для пользователя
     * @param userId ID пользователя
     * @return созданная конфигурация
     */
    @Transactional
    public PCConfiguration createConfiguration(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        config.setIsCompatible(true);
        return pcConfigurationRepository.save(config);
    }

    /**
     * Создает новую конфигурацию с названием и описанием
     * @param userId ID пользователя
     * @param name название конфигурации
     * @param description описание конфигурации
     * @return созданная конфигурация
     */
    @Transactional
    public PCConfiguration createConfiguration(Long userId, String name, String description) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(name);
        config.setDescription(description);
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        config.setIsCompatible(true);
        
        return pcConfigurationRepository.save(config);
    }

    /**
     * Обновляет конфигурацию
     * @param config конфигурация для обновления
     * @return обновленная конфигурация
     */
    @Transactional
    public PCConfiguration updateConfiguration(PCConfiguration config) {
        return pcConfigurationRepository.save(config);
    }
    
    /**
     * Обновляет название и описание конфигурации
     * @param configId ID конфигурации
     * @param name новое название
     * @param description новое описание
     * @return обновленная конфигурация
     */
    @Transactional
    public PCConfiguration updateConfiguration(Long configId, String name, String description) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        config.setName(name);
        config.setDescription(description);
        
        return pcConfigurationRepository.save(config);
    }

    /**
     * Добавляет компонент в конфигурацию
     * @param configId ID конфигурации
     * @param productId ID продукта (компонента)
     * @return обновленная конфигурация
     * @throws IllegalStateException если компонент такого типа уже существует или не совместим
     */
    @Transactional
    public PCConfiguration addComponent(Long configId, Long productId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Проверяем, является ли продукт периферийным устройством (component_type == null)
        boolean isPeripheral = product.getComponentType() == null;
        
        // Если это компонент ПК (не периферия), проверяем, можно ли добавить несколько таких компонентов
        if (!isPeripheral) {
            // Проверяем, нет ли уже компонента такого типа, который не поддерживает множественные экземпляры
            if (!product.getComponentType().allowsMultiple() && 
                configComponentRepository.existsByIdConfigIdAndProduct_ComponentType(configId, product.getComponentType())) {
                throw new IllegalStateException("Компонент такого типа уже существует в конфигурации");
            }
        }
        
        // Проверяем совместимость с существующими компонентами
        List<ConfigComponent> existingComponents = configComponentRepository.findByConfigId(configId);
        Map<Product, String> incompatibilities = compatibilityService.getIncompatibilityDetails(product, existingComponents);
        
        if (!incompatibilities.isEmpty()) {
            String incompatibilityDetails = incompatibilities.entrySet().stream()
                .map(entry -> "Несовместим с " + entry.getKey().getTitle() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
            throw new IllegalStateException("Компонент несовместим с текущей конфигурацией: " + incompatibilityDetails);
        }
        
        // Проверяем, есть ли уже такой же продукт в конфигурации
        ConfigComponentId componentId = new ConfigComponentId(configId, productId);
        Optional<ConfigComponent> existingComponent = configComponentRepository.findById(componentId);
        
        if (existingComponent.isPresent()) {
            // Если компонент уже есть, увеличиваем его количество
            ConfigComponent component = existingComponent.get();
            component.setQuantity(component.getQuantity() + 1);
            configComponentRepository.save(component);
        } else {
            // Иначе создаем новый компонент
            ConfigComponent component = new ConfigComponent();
            component.setId(componentId);
            component.setConfiguration(config);
            component.setProduct(product);
            component.setQuantity(1);
            configComponentRepository.save(component);
        }
        
        // Обновляем общую стоимость конфигурации и другие показатели
        updateTotals(config);
        
        return pcConfigurationRepository.save(config);
    }

    /**
     * Удаляет компонент из конфигурации
     * @param configId ID конфигурации
     * @param productId ID продукта (компонента)
     * @return обновленная конфигурация
     */
    @Transactional
    public PCConfiguration removeComponent(Long configId, Long productId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        ConfigComponentId componentId = new ConfigComponentId(configId, productId);
        ConfigComponent component = configComponentRepository.findById(componentId)
            .orElseThrow(() -> new ResourceNotFoundException("Component", "id", componentId));

        // Если количество больше 1, уменьшаем количество
        if (component.getQuantity() > 1) {
            component.setQuantity(component.getQuantity() - 1);
            configComponentRepository.save(component);
            
            // Обновляем общую стоимость
            config.setTotalPrice(config.getTotalPrice().subtract(component.getProduct().getPrice()));
            
            // Проверяем совместимость оставшейся конфигурации
            config.setIsCompatible(validateConfiguration(config));
            
            return pcConfigurationRepository.save(config);
        }

        // Если количество 1, удаляем компонент
        // Обновляем общую стоимость и производительность
        config.setTotalPrice(config.getTotalPrice().subtract(component.getProduct().getPrice()));
        
        // Обновляем производительность если у продукта есть такая характеристика
        try {
            Double currentPerformance = config.getTotalPerformance();
            String performanceStr = component.getProduct().getSpec("performance");
            if (performanceStr != null && !performanceStr.isEmpty()) {
                double productPerformance = Double.parseDouble(performanceStr);
                config.setTotalPerformance(currentPerformance - productPerformance);
            }
        } catch (Exception e) {
            // Если характеристики нет или она не числовая, игнорируем
        }
        
        configComponentRepository.delete(component);
        
        // Проверяем совместимость оставшейся конфигурации
        config.setIsCompatible(validateConfiguration(config));
        
        return pcConfigurationRepository.save(config);
    }

    // Проверка совместимости
    /**
     * Проверяет совместимость компонентов в конфигурации
     * @param configId ID конфигурации
     * @return true если все компоненты совместимы, иначе false
     */
    public boolean checkCompatibility(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        if (components.size() <= 1) {
            return true; // Один компонент всегда совместим сам с собой
        }
        
        // Проверяем совместимость каждого компонента с каждым
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (!compatibilityService.checkComponentsCompatibility(
                        components.get(i).getProduct(), components.get(j).getProduct())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Получает список проблем совместимости в конфигурации
     * @param configId ID конфигурации
     * @return список проблем совместимости
     */
    public List<String> getCompatibilityIssues(Long configId) {
        List<String> issues = new ArrayList<>();
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        
        if (components.size() <= 1) {
            return issues; // Пустой список - нет проблем
        }

        // Группируем компоненты по типу для проверки на дублирование
        Map<ComponentType, List<ConfigComponent>> componentsByType = components.stream()
            .collect(Collectors.groupingBy(c -> c.getProduct().getComponentType()));
            
        // Проверяем, что компоненты, не поддерживающие множественные экземпляры, 
        // представлены только в одном экземпляре
        for (Map.Entry<ComponentType, List<ConfigComponent>> entry : componentsByType.entrySet()) {
            ComponentType type = entry.getKey();
            List<ConfigComponent> componentsOfType = entry.getValue();
            
            if (!type.allowsMultiple() && componentsOfType.size() > 1) {
                issues.add(String.format("Компонент типа %s не может быть добавлен в нескольких экземплярах", 
                    type.getDisplayName()));
            }
        }

        // Проверяем каждую пару компонентов
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Product product1 = components.get(i).getProduct();
                Product product2 = components.get(j).getProduct();
                
                // Если компоненты одного типа и тип поддерживает множественные экземпляры,
                // пропускаем проверку совместимости между ними
                if (product1.getComponentType() == product2.getComponentType() && 
                    product1.getComponentType().allowsMultiple()) {
                    continue;
                }
                
                String reason = compatibilityService.getIncompatibilityReason(product1, product2);
                if (reason != null) {
                    issues.add(String.format("Несовместимость: %s (%s) и %s (%s) - %s", 
                        product1.getTitle(), product1.getComponentType().getDisplayName(),
                        product2.getTitle(), product2.getComponentType().getDisplayName(),
                        reason));
                }
            }
        }
        
        // Проверяем, что все обязательные компоненты присутствуют
        for (ComponentType requiredType : ComponentType.values()) {
            if (requiredType.isRequired() && 
                components.stream().noneMatch(c -> c.getProduct().getComponentType() == requiredType)) {
                issues.add("Отсутствует обязательный компонент: " + requiredType.getDisplayName());
            }
        }
        
        return issues;
    }

    /**
     * Получает список совместимых компонентов заданного типа для конфигурации
     * @param configId ID конфигурации
     * @param type тип компонента
     * @return список совместимых продуктов
     */
    public List<Product> getCompatibleComponents(Long configId, ComponentType type) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        List<ConfigComponent> existingComponents = configComponentRepository.findByConfigId(configId);
        List<Product> allComponentsOfType = productRepository.findByComponentType(type);
        
        return allComponentsOfType.stream()
            .filter(product -> compatibilityService.isCompatibleWithConfiguration(product, existingComponents))
            .toList();
    }
    
    /**
     * Получает все конфигурации пользователя
     * @param userId ID пользователя
     * @return список конфигураций
     */
    public List<PCConfiguration> getUserConfigurations(Long userId) {
        return pcConfigurationRepository.findByUserId(userId);
    }

    /**
     * Получает конфигурации в заданном диапазоне цен
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список конфигураций
     */
    public List<PCConfiguration> getConfigurationsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return pcConfigurationRepository.findByPriceRange(minPrice, maxPrice);
    }

    /**
     * Получает конфигурации с минимальной производительностью
     * @param minPerformance минимальная производительность
     * @return список конфигураций
     */
    public List<PCConfiguration> getConfigurationsByPerformance(Double minPerformance) {
        return pcConfigurationRepository.findByMinPerformance(minPerformance);
    }

    /**
     * Получает полные конфигурации (содержащие все необходимые компоненты)
     * @return список полных конфигураций
     */
    public List<PCConfiguration> getCompleteConfigurations() {
        return pcConfigurationRepository.findCompleteConfigurations(ComponentType.values().length);
    }

    /**
     * Получает неполные конфигурации
     * @return список неполных конфигураций
     */
    public List<PCConfiguration> getIncompleteConfigurations() {
        return pcConfigurationRepository.findIncompleteConfigurations(ComponentType.values().length);
    }

    /**
     * Удаляет конфигурацию
     * @param configId ID конфигурации
     */
    @Transactional
    public void deleteConfiguration(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        pcConfigurationRepository.delete(config);
    }

    // Расчеты и статистика
    /**
     * Рассчитывает потребляемую мощность конфигурации
     * @param configId ID конфигурации
     * @return потребляемая мощность в ваттах
     */
    public Integer calculatePowerRequirement(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        int totalPower = 0;
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            String powerStr = "";
            
            // В зависимости от типа компонента, получаем мощность из разных полей
            switch (product.getComponentType()) {
                case CPU:
                    powerStr = product.getSpec("tdp");
                    break;
                case GPU:
                    powerStr = product.getSpec("power");
                    break;
                case RAM:
                    powerStr = product.getSpec("power_consumption");
                    break;
                case STORAGE:
                    powerStr = product.getSpec("power_consumption");
                    break;
                default:
                    powerStr = product.getSpec("power_consumption");
                    break;
            }
            
            if (powerStr != null && !powerStr.isEmpty()) {
                try {
                    int power = Integer.parseInt(powerStr);
                    // Умножаем мощность на количество компонентов
                    totalPower += power * component.getQuantity();
                } catch (NumberFormatException e) {
                    // Если не удалось преобразовать, игнорируем
                }
            }
        }
        
        return totalPower;
    }
    
    /**
     * Рассчитывает оценку производительности конфигурации
     * @param configId ID конфигурации
     * @return оценка производительности
     */
    public Double calculatePerformanceScore(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        double totalScore = 0.0;
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            String performanceStr = product.getSpec("performance");
            
            if (performanceStr != null && !performanceStr.isEmpty()) {
                try {
                    double performance = Double.parseDouble(performanceStr);
                    // Для некоторых компонентов, таких как RAM и STORAGE, производительность может зависеть от количества
                    if (product.getComponentType() == ComponentType.RAM || 
                        product.getComponentType() == ComponentType.STORAGE) {
                        totalScore += performance * component.getQuantity();
                    } else {
                        // Для других компонентов берем максимальную производительность
                        totalScore += performance;
                    }
                } catch (NumberFormatException e) {
                    // Если не удалось преобразовать, игнорируем
                }
            }
        }
        
        return totalScore;
    }
    
    /**
     * Получает спецификации конфигурации
     * @param configId ID конфигурации
     * @return карта с характеристиками конфигурации
     */
    public Map<String, Object> getConfigurationSpecs(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
            
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        Map<String, Object> specs = new HashMap<>();
        
        // Общие характеристики
        specs.put("name", config.getName());
        specs.put("description", config.getDescription());
        specs.put("totalPrice", config.getTotalPrice());
        specs.put("isCompatible", config.getIsCompatible());
        specs.put("totalPerformance", calculatePerformanceScore(configId));
        specs.put("powerRequirement", calculatePowerRequirement(configId));
        
        // Группируем компоненты по типу
        Map<ComponentType, List<Map<String, Object>>> componentsByType = new HashMap<>();
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            Map<String, Object> componentData = new HashMap<>();
            
            componentData.put("id", product.getId());
            componentData.put("name", product.getTitle());
            componentData.put("price", product.getPrice());
            componentData.put("specs", product.getSpecs());
            componentData.put("quantity", component.getQuantity());
            
            ComponentType type = product.getComponentType();
            if (!componentsByType.containsKey(type)) {
                componentsByType.put(type, new ArrayList<>());
            }
            
            componentsByType.get(type).add(componentData);
        }
        
        specs.put("components", componentsByType);
        
        // Добавляем специфичные характеристики в зависимости от наличия компонентов
        if (componentsByType.containsKey(ComponentType.CPU)) {
            Map<String, Object> cpu = componentsByType.get(ComponentType.CPU).get(0);
            specs.put("cpu", cpu.get("name"));
            specs.put("cpuCores", getSpecValue(cpu, "cores"));
            specs.put("cpuFrequency", getSpecValue(cpu, "frequency"));
        }
        
        if (componentsByType.containsKey(ComponentType.GPU)) {
            Map<String, Object> gpu = componentsByType.get(ComponentType.GPU).get(0);
            specs.put("gpu", gpu.get("name"));
            specs.put("gpuMemory", getSpecValue(gpu, "memory"));
        }
        
        if (componentsByType.containsKey(ComponentType.RAM)) {
            // Если RAM несколько, суммируем объем и берем минимальную частоту
            List<Map<String, Object>> ramList = componentsByType.get(ComponentType.RAM);
            int totalCapacity = 0;
            int minFrequency = Integer.MAX_VALUE;
            
            for (Map<String, Object> ram : ramList) {
                int quantity = (int) ram.get("quantity");
                int capacity = parseIntSpec(getSpecValue(ram, "capacity"));
                int frequency = parseIntSpec(getSpecValue(ram, "frequency"));
                
                totalCapacity += capacity * quantity;
                if (frequency < minFrequency) {
                    minFrequency = frequency;
                }
            }
            
            specs.put("ramCapacity", totalCapacity);
            specs.put("ramFrequency", minFrequency == Integer.MAX_VALUE ? null : minFrequency);
        }
        
        if (componentsByType.containsKey(ComponentType.STORAGE)) {
            // Если накопителей несколько, суммируем объем
            List<Map<String, Object>> storageList = componentsByType.get(ComponentType.STORAGE);
            int totalCapacity = 0;
            
            for (Map<String, Object> storage : storageList) {
                int quantity = (int) storage.get("quantity");
                int capacity = parseIntSpec(getSpecValue(storage, "capacity"));
                totalCapacity += capacity * quantity;
            }
            
            specs.put("storageCapacity", totalCapacity);
        }
        
        return specs;
    }
    
    /**
     * Получает значение спецификации из карты компонента
     * @param component карта с данными компонента
     * @param specName имя спецификации
     * @return значение спецификации или null
     */
    @SuppressWarnings("unchecked")
    private String getSpecValue(Map<String, Object> component, String specName) {
        Map<String, Object> specs = (Map<String, Object>) component.get("specs");
        return specs != null ? (String) specs.get(specName) : null;
    }
    
    /**
     * Преобразует строковое значение спецификации в целое число
     * @param value строковое значение
     * @return целое число или 0, если преобразование невозможно
     */
    private int parseIntSpec(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        
        try {
            // Удаляем все нецифровые символы (например, "16 ГБ" -> "16")
            String numericValue = value.replaceAll("[^0-9]", "");
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    // Рекомендации
    /**
     * Получает рекомендуемые компоненты для добавления в конфигурацию
     * @param configId ID конфигурации
     * @return карта с рекомендуемыми компонентами по типам
     */
    public Map<ComponentType, List<Product>> getRecommendedComponents(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
            
        Map<ComponentType, List<Product>> recommendations = new HashMap<>();
        List<ConfigComponent> existingComponents = configComponentRepository.findByConfigId(configId);
        
        // Получаем список типов компонентов, которых нет в конфигурации
        Set<ComponentType> presentTypes = existingComponents.stream()
            .map(c -> c.getProduct().getComponentType())
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
            
        for (ComponentType type : ComponentType.values()) {
            if (!presentTypes.contains(type)) {
                List<Product> compatibleProducts = getCompatibleComponents(configId, type);
                recommendations.put(type, compatibleProducts);
            }
        }
        
        return recommendations;
    }
    
    /**
     * Получает рекомендуемые конфигурации по назначению и бюджету
     * @param purpose назначение (игры, работа и т.д.)
     * @param budget бюджет
     * @return список рекомендуемых конфигураций
     */
    public List<PCConfiguration> getRecommendedConfigurations(String purpose, double budget) {
        // Заглушка для демонстрации
        // В реальном приложении здесь должна быть логика рекомендаций
        return pcConfigurationRepository.findByPriceRange(
            BigDecimal.ZERO, 
            BigDecimal.valueOf(budget)
        );
    }
    
    // Импорт/экспорт
    /**
     * Экспортирует конфигурацию в строковый формат
     * @param configId ID конфигурации
     * @return строковое представление конфигурации
     */
    public String exportConfiguration(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
            
        Map<String, Object> configData = getConfigurationSpecs(configId);
        
        // В реальном приложении здесь должно быть преобразование в JSON
        return configData.toString();
    }
    
    /**
     * Импортирует конфигурацию из строкового формата
     * @param userId ID пользователя
     * @param jsonConfig строковое представление конфигурации
     * @return импортированная конфигурация
     */
    @Transactional
    public PCConfiguration importConfiguration(Long userId, String jsonConfig) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            
        // В реальном приложении здесь должен быть парсинг JSON и создание конфигурации
        
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName("Imported Configuration");
        config.setDescription("Imported from external source");
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        
        return pcConfigurationRepository.save(config);
    }
    
    // Клонирование
    /**
     * Клонирует существующую конфигурацию для другого пользователя
     * @param configId ID исходной конфигурации
     * @param userId ID пользователя
     * @return клонированная конфигурация
     */
    @Transactional
    public PCConfiguration cloneConfiguration(Long configId, Long userId) {
        PCConfiguration source = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            
        // Создаем новую конфигурацию
        PCConfiguration clone = new PCConfiguration();
        clone.setUser(user);
        clone.setName(source.getName() + " (Clone)");
        clone.setDescription(source.getDescription());
        clone.setTotalPrice(source.getTotalPrice());
        clone.setTotalPerformance(source.getTotalPerformance());
        clone.setIsCompatible(source.getIsCompatible());
        
        PCConfiguration savedClone = pcConfigurationRepository.save(clone);
        
        // Копируем компоненты
        List<ConfigComponent> sourceComponents = configComponentRepository.findByConfigId(configId);
        
        for (ConfigComponent sourceComponent : sourceComponents) {
            ConfigComponent cloneComponent = new ConfigComponent();
            ConfigComponentId componentId = new ConfigComponentId(savedClone.getId(), sourceComponent.getProduct().getId());
            cloneComponent.setId(componentId);
            cloneComponent.setConfiguration(savedClone);
            cloneComponent.setProduct(sourceComponent.getProduct());
            
            configComponentRepository.save(cloneComponent);
        }
        
        return savedClone;
    }
    
    // Наличие компонентов
    /**
     * Проверяет наличие всех компонентов конфигурации на складе
     * @param configId ID конфигурации
     * @return true если все компоненты в наличии, иначе false
     */
    public boolean checkComponentsAvailability(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        
        for (ConfigComponent component : components) {
            if (component.getProduct().getStockQuantity() <= 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Получает список отсутствующих на складе компонентов
     * @param configId ID конфигурации
     * @return список отсутствующих продуктов
     */
    public List<Product> getMissingComponents(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        List<Product> missingProducts = new ArrayList<>();
        
        for (ConfigComponent component : components) {
            if (component.getProduct().getStockQuantity() <= 0) {
                missingProducts.add(component.getProduct());
            }
        }
        
        return missingProducts;
    }

    /**
     * Проверяет валидность конфигурации
     * @param config конфигурация для проверки
     * @return true если конфигурация валидна, иначе false
     */
    private boolean validateConfiguration(PCConfiguration config) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(config.getId());
        
        // Если компонентов нет, считаем, что конфигурация валидна
        if (components.isEmpty()) {
            return true;
        }
        
        // Отделяем компоненты ПК от периферии
        List<ConfigComponent> pcComponents = new ArrayList<>();
        List<ConfigComponent> peripheralComponents = new ArrayList<>();
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            if (product.getComponentType() == null) {
                peripheralComponents.add(component);
            } else {
                pcComponents.add(component);
            }
        }
        
        // Если нет компонентов ПК, конфигурация не валидна
        if (pcComponents.isEmpty()) {
            return false;
        }
        
        // Проверяем наличие всех необходимых компонентов
        Set<ComponentType> requiredTypes = Arrays.stream(ComponentType.values())
            .filter(ComponentType::isRequired)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
            
        Set<ComponentType> presentTypes = pcComponents.stream()
            .map(c -> c.getProduct().getComponentType())
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
        
        if (!presentTypes.containsAll(requiredTypes)) {
            return false;
        }

        // Группируем компоненты по типу для проверки совместимости
        Map<ComponentType, List<ConfigComponent>> componentsByType = pcComponents.stream()
            .collect(Collectors.groupingBy(c -> c.getProduct().getComponentType()));
            
        // Проверяем, что компоненты, не поддерживающие множественные экземпляры, 
        // представлены только в одном экземпляре
        for (Map.Entry<ComponentType, List<ConfigComponent>> entry : componentsByType.entrySet()) {
            ComponentType type = entry.getKey();
            List<ConfigComponent> componentsOfType = entry.getValue();
            
            if (!type.allowsMultiple() && componentsOfType.size() > 1) {
                return false;
            }
        }

        // Проверяем совместимость между всеми компонентами ПК
        for (int i = 0; i < pcComponents.size(); i++) {
            for (int j = i + 1; j < pcComponents.size(); j++) {
                Product product1 = pcComponents.get(i).getProduct();
                Product product2 = pcComponents.get(j).getProduct();
                
                // Если компоненты одного типа и тип поддерживает множественные экземпляры,
                // пропускаем проверку совместимости между ними
                if (product1.getComponentType() == product2.getComponentType() && 
                    product1.getComponentType().allowsMultiple()) {
                    continue;
                }
                
                if (!compatibilityService.checkComponentsCompatibility(product1, product2)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Получает все публичные конфигурации с загруженными компонентами
     * @return список публичных конфигураций с компонентами
     */
    public List<PCConfiguration> getPublicConfigurationsWithComponents() {
        return pcConfigurationRepository.findAllWithComponents().stream()
            .limit(20)
            .toList();
    }

    /**
     * Получает компоненты конфигурации по ID
     * @param configId ID конфигурации
     * @return список компонентов
     */
    public List<ConfigComponent> getConfigComponents(Long configId) {
        return configComponentRepository.findByConfigId(configId);
    }

    /**
     * Получает все конфигурации пользователя с загруженными компонентами
     * @param userId ID пользователя
     * @return список конфигураций с компонентами
     */
    @Transactional(readOnly = true)
    public List<PCConfiguration> getUserConfigurationsWithComponents(Long userId) {
        return pcConfigurationRepository.findByUserIdWithComponents(userId);
    }

    /**
     * Получает конфигурацию по ID
     * @param configId ID конфигурации
     * @return конфигурация
     * @throws ResourceNotFoundException если конфигурация не найдена
     */
    public PCConfiguration getConfiguration(Long configId) {
        return pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
    }

    /**
     * Обновляет общую стоимость и производительность конфигурации
     * @param config конфигурация для обновления
     */
    private void updateTotals(PCConfiguration config) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(config.getId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalPerformance = 0.0;
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            // Учитываем количество компонентов при расчете стоимости
            BigDecimal componentPrice = product.getPrice().multiply(BigDecimal.valueOf(component.getQuantity()));
            totalPrice = totalPrice.add(componentPrice);
            
            // Учитываем производительность только для компонентов ПК, не для периферии
            if (product.getComponentType() != null) {
                try {
                    String performanceStr = product.getSpec("performance");
                    if (performanceStr != null && !performanceStr.isEmpty()) {
                        double productPerformance = Double.parseDouble(performanceStr);
                        // Для RAM и STORAGE учитываем количество
                        if (product.getComponentType() != null && 
                            (product.getComponentType() == ComponentType.RAM || 
                             product.getComponentType() == ComponentType.STORAGE)) {
                            productPerformance *= component.getQuantity();
                        }
                        totalPerformance += productPerformance;
                    }
                } catch (Exception e) {
                    // Если характеристики нет или она не числовая, игнорируем
                }
            }
        }
        
        config.setTotalPrice(totalPrice);
        config.setTotalPerformance(totalPerformance);
        config.setIsCompatible(validateConfiguration(config));
    }
} 