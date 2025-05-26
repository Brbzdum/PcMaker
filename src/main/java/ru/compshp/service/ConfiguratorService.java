package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConfiguratorService {
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ConfigComponentRepository configComponentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public PCConfiguration updateConfiguration(PCConfiguration config) {
        return pcConfigurationRepository.save(config);
    }

    @Transactional
    public PCConfiguration addComponent(Long configId, Long productId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Проверяем, нет ли уже компонента такого типа
        if (configComponentRepository.existsByConfigurationIdAndProduct_ComponentType(
            configId, product.getComponentType())) {
            throw new IllegalStateException("Component of this type already exists in configuration");
        }

        // Проверяем совместимость с существующими компонентами
        List<ConfigComponent> existingComponents = configComponentRepository.findByConfigurationId(configId);
        for (ConfigComponent existing : existingComponents) {
            if (!pcConfigurationRepository.checkCompatibility(existing.getProduct().getId(), productId)) {
                throw new IllegalStateException("Component is not compatible with existing configuration");
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
        config.setTotalPerformance(config.getTotalPerformance() + 
            Double.parseDouble(product.getSpec("performance")));
        
        // Проверяем совместимость всей конфигурации
        config.setIsCompatible(validateConfiguration(config));
        
        return pcConfigurationRepository.save(config);
    }

    @Transactional
    public PCConfiguration removeComponent(Long configId, Long productId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        
        ConfigComponentId componentId = new ConfigComponentId(configId, productId);
        ConfigComponent component = configComponentRepository.findById(componentId)
            .orElseThrow(() -> new ResourceNotFoundException("Component", "id", componentId));

        // Обновляем общую стоимость и производительность
        config.setTotalPrice(config.getTotalPrice().subtract(component.getProduct().getPrice()));
        config.setTotalPerformance(config.getTotalPerformance() - 
            Double.parseDouble(component.getProduct().getSpec("performance")));
        
        configComponentRepository.delete(component);
        
        // Проверяем совместимость оставшейся конфигурации
        config.setIsCompatible(validateConfiguration(config));
        
        return pcConfigurationRepository.save(config);
    }

    public List<Product> getCompatibleComponents(Long configId, ComponentType type) {
        return pcConfigurationRepository.findCompatibleComponents(type, configId);
    }

    public PCConfiguration getConfiguration(Long configId) {
        return pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
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

    public List<PCConfiguration> getCompleteConfigurations() {
        return pcConfigurationRepository.findCompleteConfigurations(ComponentType.values().length);
    }

    public List<PCConfiguration> getIncompleteConfigurations() {
        return pcConfigurationRepository.findIncompleteConfigurations(ComponentType.values().length);
    }

    @Transactional
    public void deleteConfiguration(Long configId) {
        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
        pcConfigurationRepository.delete(config);
    }

    private boolean validateConfiguration(PCConfiguration config) {
        List<ConfigComponent> components = configComponentRepository.findByConfigurationId(config.getId());
        
        // Проверяем наличие всех необходимых компонентов
        Set<ComponentType> requiredTypes = new HashSet<>(Arrays.asList(ComponentType.values()));
        Set<ComponentType> presentTypes = components.stream()
            .map(c -> c.getProduct().getComponentType())
            .collect(HashSet::new, HashSet::add, HashSet::addAll);
        
        if (!presentTypes.containsAll(requiredTypes)) {
            return false;
        }

        // Проверяем совместимость между всеми компонентами
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (!pcConfigurationRepository.checkCompatibility(
                    components.get(i).getProduct().getId(),
                    components.get(j).getProduct().getId())) {
                    return false;
                }
            }
        }

        return true;
    }
} 