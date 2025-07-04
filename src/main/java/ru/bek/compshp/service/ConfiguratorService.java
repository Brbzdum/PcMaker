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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ConfiguratorService.class);

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
     * @param category категория конфигурации
     * @return созданная конфигурация
     */
    @Transactional
    public PCConfiguration createConfiguration(Long userId, String name, String description, String category) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(name);
        config.setDescription(description);
        config.setCategory(category);
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        config.setIsCompatible(true);
        
        return pcConfigurationRepository.save(config);
    }

    /**
     * Создает новую конфигурацию с компонентами
     * @param userId ID пользователя
     * @param name название конфигурации
     * @param description описание конфигурации
     * @param category категория конфигурации
     * @param componentIds список ID компонентов
     * @return созданная конфигурация
     */
    @Transactional
    public PCConfiguration createConfigurationWithComponents(Long userId, String name, String description, String category, List<Long> componentIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Создаем конфигурацию без компонентов
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(name);
        config.setDescription(description);
        config.setCategory(category);
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        config.setIsCompatible(true);
        config.setComponents(new HashSet<>());
        
        // Сначала сохраняем конфигурацию
        PCConfiguration savedConfig = pcConfigurationRepository.save(config);
        
        // Собираем информацию о компонентах и считаем общую цену
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalPerformance = 0.0;
        
        // Группируем компоненты по ID для учета количества
        Map<Long, Integer> componentQuantities = new HashMap<>();
        for (Long productId : componentIds) {
            componentQuantities.put(productId, componentQuantities.getOrDefault(productId, 0) + 1);
        }
        
        // Добавляем компоненты по одному
        for (Map.Entry<Long, Integer> entry : componentQuantities.entrySet()) {
            Long productId = entry.getKey();
            
            try {
                // Используем существующий метод для добавления компонента
                PCConfiguration updatedConfig = addComponent(savedConfig.getId(), productId);
                savedConfig = updatedConfig;
            } catch (Exception e) {
                log.warn("Не удалось добавить компонент {} в конфигурацию {}: {}", 
                    productId, savedConfig.getId(), e.getMessage());
            }
        }
        
        // Возвращаем обновленную конфигурацию
        return savedConfig;
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
     * @param category новая категория
     * @return обновленная конфигурация
     */
    @Transactional
    public PCConfiguration updateConfiguration(Long configId, String name, String description, String category) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        config.setName(name);
        config.setDescription(description);
        config.setCategory(category);
        
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

        // Проверяем, нет ли уже компонента такого типа
        if (configComponentRepository.existsByIdConfigIdAndProduct_ComponentType(
            configId, product.getComponentType())) {
            throw new IllegalStateException("Component of this type already exists in configuration");
        }

        // Проверяем совместимость с существующими компонентами
        List<ConfigComponent> existingComponents = configComponentRepository.findByConfigId(configId);
        if (!compatibilityService.isCompatibleWithConfiguration(product, existingComponents)) {
            Map<Product, String> incompatibilityDetails = 
                compatibilityService.getIncompatibilityDetails(product, existingComponents);
            
            if (!incompatibilityDetails.isEmpty()) {
                String errorMessage = "Компонент несовместим с существующей конфигурацией: " + 
                    incompatibilityDetails.values().iterator().next();
                throw new IllegalStateException(errorMessage);
            } else {
                throw new IllegalStateException("Компонент несовместим с существующей конфигурацией");
            }
        }

        ConfigComponent component = new ConfigComponent();
        ConfigComponentId componentId = new ConfigComponentId(configId, productId);
        component.setId(componentId);
        component.setConfiguration(config);
        component.setProduct(product);
        configComponentRepository.save(component);

        // Обновляем общую стоимость и производительность
        config.setTotalPrice(config.getTotalPrice().add(product.getPrice()));
        // Обновляем производительность если у продукта есть такая характеристика
        try {
            Double currentPerformance = config.getTotalPerformance();
            String performanceStr = product.getSpec("performance");
            if (performanceStr != null && !performanceStr.isEmpty()) {
                double productPerformance = Double.parseDouble(performanceStr);
                config.setTotalPerformance(currentPerformance + productPerformance);
            }
        } catch (Exception e) {
            // Если характеристики нет или она не числовая, игнорируем
        }
        
        // Проверяем совместимость всей конфигурации
        config.setIsCompatible(validateConfiguration(config));
        
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

        // Проверяем каждую пару компонентов
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Product product1 = components.get(i).getProduct();
                Product product2 = components.get(j).getProduct();
                
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
     * Рассчитывает общее энергопотребление конфигурации
     * @param configId ID конфигурации
     * @return общее энергопотребление в ваттах
     */
    public Integer calculatePowerRequirement(Long configId) {
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        int totalPower = 0;
        
        for (ConfigComponent component : components) {
            try {
                Product product = component.getProduct();
                
                // ИСКЛЮЧАЕМ БП из расчета потребления - БП поставляет энергию, а не потребляет!
                if (product.getComponentType() == ComponentType.PSU) {
                    continue; // Пропускаем БП
                }
                
                String powerSpec = null;
                
                // Для всех компонентов кроме БП ищем энергопотребление
                powerSpec = product.getSpec("power_consumption");
                if (powerSpec == null || powerSpec.isEmpty()) {
                    powerSpec = product.getSpec("tdp");
                }
                if (powerSpec == null || powerSpec.isEmpty()) {
                    powerSpec = product.getSpec("power");
                }
                
                if (powerSpec != null && !powerSpec.isEmpty()) {
                    int componentPower = Integer.parseInt(powerSpec);
                    totalPower += componentPower;
                    log.debug("Компонент {}: {}Вт", product.getTitle(), componentPower);
                } else {
                    // Добавляем базовое потребление для компонентов без указанного TDP
                    int basePower = getBasePowerConsumption(product.getComponentType());
                    totalPower += basePower;
                    log.debug("Компонент {} (базовое потребление): {}Вт", product.getTitle(), basePower);
                }
            } catch (Exception e) {
                log.warn("Ошибка при расчете мощности для компонента: {}", e.getMessage());
            }
        }
        
        log.info("Общее энергопотребление конфигурации {}: {}Вт", configId, totalPower);
        return totalPower;
    }
    
    /**
     * Возвращает базовое энергопотребление для типа компонента
     */
    private int getBasePowerConsumption(ComponentType componentType) {
        switch (componentType) {
            case CPU: return 100; // Базовое потребление процессора
            case GPU: return 200; // Базовое потребление видеокарты  
            case RAM: return 5;   // Потребление модуля памяти
            case MB: return 30;   // Потребление материнской платы
            case STORAGE: return 10; // Потребление накопителя
            case COOLER: return 5;   // Потребление системы охлаждения
            case CASE: return 0;     // Корпус не потребляет энергию
            case PSU: return 0;      // БП не потребляет энергию
            default: return 10;      // Для неизвестных компонентов
        }
    }
    
    /**
     * Рассчитывает общую производительность конфигурации
     * @param configId ID конфигурации
     * @return значение производительности
     */
    public Double calculatePerformanceScore(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        return config.getTotalPerformance();
    }
    
    /**
     * Получает спецификации конфигурации
     * @param configId ID конфигурации
     * @return карта со спецификациями
     */
    public Map<String, Object> getConfigurationSpecs(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
            
        Map<String, Object> specs = new HashMap<>();
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        
        // Общая информация
        specs.put("id", config.getId());
        specs.put("name", config.getName());
        specs.put("description", config.getDescription());
        specs.put("totalPrice", config.getTotalPrice());
        specs.put("performanceScore", config.getTotalPerformance());
        specs.put("isCompatible", config.getIsCompatible());
        
        // Информация по компонентам
        Map<ComponentType, Map<String, Object>> componentsMap = new HashMap<>();
        
        for (ConfigComponent component : components) {
            Product product = component.getProduct();
            Map<String, Object> componentInfo = new HashMap<>();
            
            componentInfo.put("id", product.getId());
            componentInfo.put("name", product.getName());
            componentInfo.put("price", product.getPrice());
            componentInfo.put("manufacturer", product.getManufacturer().getName());
            componentInfo.put("specs", product.getSpecifications());
            
            componentsMap.put(product.getComponentType(), componentInfo);
        }
        
        specs.put("components", componentsMap);
        
        return specs;
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
        
        // Проверяем наличие всех необходимых компонентов
        Set<ComponentType> requiredTypes = Arrays.stream(ComponentType.values())
            .filter(ComponentType::isRequired)
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
            
        Set<ComponentType> presentTypes = components.stream()
            .map(c -> c.getProduct().getComponentType())
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
        
        if (!presentTypes.containsAll(requiredTypes)) {
            return false;
        }

        // Проверяем совместимость между всеми компонентами
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (!compatibilityService.checkComponentsCompatibility(
                    components.get(i).getProduct(),
                    components.get(j).getProduct())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Загружает сохраненную конфигурацию в конфигуратор
     * @param configId ID сохраненной конфигурации
     * @return Загруженная конфигурация с полной информацией о продуктах
     */
    @Transactional
    public PCConfiguration loadSavedConfiguration(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        // Загружаем все компоненты конфигурации с полной информацией о продуктах
        List<ConfigComponent> components = configComponentRepository.findByConfigId(configId);
        
        // Убедимся, что все продукты полностью загружены (решение проблемы с "подсосом продуктов")
        for (ConfigComponent component : components) {
            Product product = productRepository.findById(component.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", component.getProduct().getId()));
            
            // Обновляем ссылку на продукт в компоненте
            component.setProduct(product);
        }
        
        // Обновляем ссылки на компоненты в конфигурации
        config.setComponents(new HashSet<>(components));
        
        return config;
    }

    /**
     * Получает все продукты из сохраненной конфигурации для отображения в конфигураторе
     * @param configId ID конфигурации
     * @return Карта, где ключ - тип компонента, значение - продукт
     */
    public Map<ComponentType, Product> getConfigurationProducts(Long configId) {
        PCConfiguration config = loadSavedConfiguration(configId);
        Map<ComponentType, Product> result = new HashMap<>();
        
        for (ConfigComponent component : config.getComponents()) {
            Product product = component.getProduct();
            result.put(product.getComponentType(), product);
        }
        
        return result;
    }

    /**
     * Получает список публичных конфигураций
     * @return список публичных конфигураций
     */
    public List<PCConfiguration> getPublicConfigurations() {
        return pcConfigurationRepository.findByIsPublicTrue();
    }

    /**
     * Переключает статус публикации конфигурации
     * @param configId ID конфигурации
     * @return обновленная конфигурация
     */
    @Transactional
    public PCConfiguration toggleConfigurationPublication(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        // Инвертируем текущее значение isPublic
        boolean currentPublicStatus = config.getIsPublic() != null ? config.getIsPublic() : false;
        config.setIsPublic(!currentPublicStatus);
        
        log.info("Toggling publication status for configuration {}: {} -> {}", 
            configId, currentPublicStatus, !currentPublicStatus);
            
        return pcConfigurationRepository.save(config);
    }
} 