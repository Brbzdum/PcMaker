package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PCConfigurationService {
    private final PCConfigurationRepository configurationRepository;
    private final ConfigComponentRepository componentRepository;
    private final ProductRepository productRepository;
    private final CompatibilityRuleRepository compatibilityRuleRepository;
    private final ComponentCompatibilityService compatibilityService;
    private final ProductService productService;

    public PCConfigurationService(
            PCConfigurationRepository configurationRepository,
            ConfigComponentRepository componentRepository,
            ProductRepository productRepository,
            CompatibilityRuleRepository compatibilityRuleRepository,
            ComponentCompatibilityService compatibilityService,
            ProductService productService
    ) {
        this.configurationRepository = configurationRepository;
        this.componentRepository = componentRepository;
        this.productRepository = productRepository;
        this.compatibilityRuleRepository = compatibilityRuleRepository;
        this.compatibilityService = compatibilityService;
        this.productService = productService;
    }

    /**
     * Создает новую конфигурацию
     */
    @Transactional
    public PCConfiguration createConfiguration(String name, String description) {
        PCConfiguration config = new PCConfiguration();
        config.setName(name);
        config.setDescription(description);
        return configurationRepository.save(config);
    }

    /**
     * Добавляет компонент в конфигурацию
     */
    @Transactional
    public PCConfiguration addComponent(Long configId, Long productId) {
        PCConfiguration config = configurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));
        
        Product product = productService.getById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Проверяем совместимость с существующими компонентами
        List<Product> existingComponents = config.getComponents().stream()
                .map(ConfigComponent::getProduct)
                .toList();

        if (!compatibilityService.checkConfigurationCompatibility(product, existingComponents)) {
            throw new RuntimeException("Component is not compatible with the configuration");
        }

        // Проверяем, нет ли уже компонента такого типа
        config.getComponents().stream()
                .filter(c -> c.getProduct().getComponentType() == product.getComponentType())
                .findFirst()
                .ifPresent(c -> {
                    throw new RuntimeException("Component of type " + product.getComponentType() + " already exists");
                });

        ConfigComponent component = new ConfigComponent();
        component.setConfiguration(config);
        component.setProduct(product);
        componentRepository.save(component);
        config.getComponents().add(component);

        return configurationRepository.save(config);
    }

    /**
     * Удаляет компонент из конфигурации
     */
    @Transactional
    public PCConfiguration removeComponent(Long configId, Long componentId) {
        PCConfiguration config = configurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

        ConfigComponent component = config.getComponents().stream()
                .filter(c -> c.getId().equals(componentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Component not found"));

        config.getComponents().remove(component);
        componentRepository.delete(component);
        return configurationRepository.save(config);
    }

    /**
     * Заменяет компонент в конфигурации
     */
    @Transactional
    public PCConfiguration replaceComponent(Long configId, Long oldComponentId, Long newProductId) {
        PCConfiguration config = removeComponent(configId, oldComponentId);
        return addComponent(configId, newProductId);
    }

    /**
     * Получает все готовые конфигурации
     */
    public List<PCConfiguration> getAllConfigurations() {
        return configurationRepository.findAll();
    }

    /**
     * Получает конфигурацию по ID
     */
    public Optional<PCConfiguration> getConfiguration(Long id) {
        return configurationRepository.findById(id);
    }

    /**
     * Клонирует конфигурацию
     */
    @Transactional
    public PCConfiguration cloneConfiguration(Long id, String newName) {
        PCConfiguration original = configurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

        PCConfiguration clone = new PCConfiguration();
        clone.setName(newName);
        clone.setDescription(original.getDescription());
        clone = configurationRepository.save(clone);

        for (ConfigComponent component : original.getComponents()) {
            ConfigComponent newComponent = new ConfigComponent();
            newComponent.setConfiguration(clone);
            newComponent.setProduct(component.getProduct());
            componentRepository.save(newComponent);
            clone.getComponents().add(newComponent);
        }

        return configurationRepository.save(clone);
    }

    /**
     * Рассчитывает общую стоимость конфигурации
     */
    public BigDecimal calculateTotalPrice(Long configId) {
        return configurationRepository.findById(configId)
                .map(config -> config.getComponents().stream()
                        .map(component -> component.getProduct().getPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Проверяет наличие всех компонентов
     */
    public boolean checkComponentsAvailability(Long configId) {
        return configurationRepository.findById(configId)
                .map(config -> config.getComponents().stream()
                        .allMatch(component -> 
                            productService.isInStock(component.getProduct().getId(), 1)))
                .orElse(false);
    }

    /**
     * Получает рекомендуемые компоненты для конфигурации
     */
    public List<Product> getRecommendedComponents(Long configId, ComponentType type) {
        return configurationRepository.findById(configId)
                .map(config -> {
                    List<Product> existingComponents = config.getComponents().stream()
                            .map(ConfigComponent::getProduct)
                            .toList();
                    
                    return productService.getByComponentType(type).stream()
                            .filter(product -> compatibilityService.checkConfigurationCompatibility(product, existingComponents))
                            .toList();
                })
                .orElse(List.of());
    }

    /**
     * Рассчитывает энергопотребление конфигурации
     */
    public int calculatePowerConsumption(Long configId) {
        return configurationRepository.findById(configId)
                .map(config -> config.getComponents().stream()
                        .mapToInt(component -> {
                            try {
                                Map<String, Object> specs = objectMapper.readValue(component.getProduct().getSpecs(), Map.class);
                                return specs.containsKey("tdp") ? (int) specs.get("tdp") : 0;
                            } catch (Exception e) {
                                return 0;
                            }
                        })
                        .sum())
                .orElse(0);
    }

    /**
     * Экспортирует конфигурацию в JSON
     */
    public String exportConfiguration(Long configId) {
        return configurationRepository.findById(configId)
                .map(config -> {
                    try {
                        Map<String, Object> export = new HashMap<>();
                        export.put("name", config.getName());
                        export.put("description", config.getDescription());
                        export.put("components", config.getComponents().stream()
                                .map(component -> {
                                    Map<String, Object> comp = new HashMap<>();
                                    comp.put("type", component.getProduct().getComponentType());
                                    comp.put("name", component.getProduct().getTitle());
                                    comp.put("price", component.getProduct().getPrice());
                                    return comp;
                                })
                                .toList());
                        export.put("totalPrice", calculateTotalPrice(configId));
                        export.put("powerConsumption", calculatePowerConsumption(configId));
                        return objectMapper.writeValueAsString(export);
                    } catch (Exception e) {
                        throw new RuntimeException("Error exporting configuration", e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Configuration not found"));
    }

    public List<PCConfiguration> getByUser(User user) {
        return configurationRepository.findByUser(user);
    }

    public boolean checkCompatibility(Long configId) {
        // TODO: Проверить совместимость всех компонентов конфигурации
        return true;
    }

    public boolean checkAllRequiredComponentsPresent(Long configId) {
        // TODO: Проверить, что все обязательные компоненты присутствуют
        return true;
    }

    public void exportToPdf(Long configId) {
        // TODO: Экспортировать конфигурацию в PDF
    }

    // TODO: Методы для публикации конфигурации, получения истории изменений и т.д.
} 