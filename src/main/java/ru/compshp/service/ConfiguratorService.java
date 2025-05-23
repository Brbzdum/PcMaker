package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.ComponentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfiguratorService {
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public PCConfiguration createConfiguration(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        return pcConfigurationRepository.save(config);
    }

    public PCConfiguration getConfiguration(Long configId) {
        return pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new RuntimeException("Configuration not found"));
    }

    public ConfigComponent addComponent(Long configId, Long productId, Integer quantity) {
        PCConfiguration config = getConfiguration(configId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if component is already in configuration
        Optional<ConfigComponent> existingComponent = config.getComponents().stream()
            .filter(c -> c.getProduct().getComponentType() == product.getComponentType())
            .findFirst();

        if (existingComponent.isPresent()) {
            throw new RuntimeException("Component of this type already exists in configuration");
        }

        // Check compatibility with existing components
        for (ConfigComponent component : config.getComponents()) {
            if (!productService.isCompatible(product, component.getProduct())) {
                throw new RuntimeException("Component is not compatible with existing configuration");
            }
        }

        ConfigComponent configComponent = new ConfigComponent();
        configComponent.setConfiguration(config);
        configComponent.setProduct(product);
        configComponent.setQuantity(quantity);

        config.getComponents().add(configComponent);
        pcConfigurationRepository.save(config);

        return configComponent;
    }

    public void removeComponent(Long configId, Long productId) {
        PCConfiguration config = getConfiguration(configId);
        config.getComponents().removeIf(c -> c.getProduct().getId().equals(productId));
        pcConfigurationRepository.save(config);
    }

    public void updateComponentQuantity(Long configId, Long productId, Integer quantity) {
        PCConfiguration config = getConfiguration(configId);
        config.getComponents().stream()
            .filter(c -> c.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(component -> {
                component.setQuantity(quantity);
                pcConfigurationRepository.save(config);
            });
    }

    public List<Product> getCompatibleComponents(Long configId, ComponentType type) {
        PCConfiguration config = getConfiguration(configId);
        return productRepository.findCompatibleComponents(type, config.getTotalPowerConsumption());
    }

    public List<Product> getComponentsInBudget(Long configId, ComponentType type, BigDecimal maxPrice) {
        PCConfiguration config = getConfiguration(configId);
        return productRepository.findComponentsInBudget(type, maxPrice);
    }

    public List<Product> getComponentsByMinPerformance(Long configId, ComponentType type, Double minPerformance) {
        PCConfiguration config = getConfiguration(configId);
        return productRepository.findComponentsByMinPerformance(type, minPerformance);
    }

    public boolean validateConfiguration(Long configId) {
        PCConfiguration config = getConfiguration(configId);
        
        // Check if all required components are present
        for (ComponentType type : ComponentType.values()) {
            if (type.isRequired() && config.getComponents().stream()
                .noneMatch(c -> c.getProduct().getComponentType() == type)) {
                return false;
            }
        }

        // Check compatibility between all components
        for (ConfigComponent component1 : config.getComponents()) {
            for (ConfigComponent component2 : config.getComponents()) {
                if (component1 != component2 && 
                    !productService.isCompatible(component1.getProduct(), component2.getProduct())) {
                    return false;
                }
            }
        }

        // Check power consumption
        if (config.getTotalPowerConsumption() > config.getPowerSupply().getPower()) {
            return false;
        }

        return true;
    }

    public List<PCConfiguration> getUserConfigurations(Long userId) {
        return pcConfigurationRepository.findByUserId(userId);
    }

    public List<PCConfiguration> getConfigurationsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return pcConfigurationRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<PCConfiguration> getConfigurationsByPerformance(Double minPerformance) {
        return pcConfigurationRepository.findByMinPerformance(minPerformance);
    }

    public void deleteConfiguration(Long configId) {
        pcConfigurationRepository.deleteById(configId);
    }

    public Map<String, Object> getCompatibilityInfo(Long configId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<Product> getMissingComponents(Long configId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<PCConfiguration> getSavedConfigurations() {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addConfigurationToCart(Long configId) {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public List<PCConfiguration> getPopularConfigurations() {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Map<String, Object> getConfiguratorStatistics() {
        // Implementation needed
        throw new UnsupportedOperationException("Method not implemented");
    }
} 