package ru.compshp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.exception.*;
import ru.compshp.model.*;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.repository.*;
import ru.compshp.service.CompatibilityService;
import ru.compshp.service.PCConfiguratorService;
import ru.compshp.service.PricingService;
import ru.compshp.service.ProductService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PCConfiguratorServiceImpl implements PCConfiguratorService {
    private final PCConfigurationRepository configRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CompatibilityService compatibilityService;
    private final PricingService pricingService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PCConfiguration createConfiguration(Long userId, String name, String description) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(name);
        config.setDescription(description);
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        config.setComponents(new ArrayList<>());

        return configRepository.save(config);
    }

    @Override
    @Transactional
    public PCConfiguration updateConfiguration(Long configId, String name, String description) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        config.setName(name);
        config.setDescription(description);
        config.setUpdatedAt(LocalDateTime.now());

        return configRepository.save(config);
    }

    @Override
    @Transactional
    public void deleteConfiguration(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));
        configRepository.delete(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PCConfiguration> getConfiguration(Long configId) {
        return configRepository.findById(configId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PCConfiguration> getUserConfigurations(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        return configRepository.findByUser(user);
    }

    @Override
    @Transactional
    public PCConfiguration addComponent(Long configId, Long productId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStock() <= 0) {
            throw new InsufficientStockException(productId, 1, 0);
        }

        // Проверяем, нет ли уже компонента такого типа
        config.getComponents().stream()
            .filter(c -> c.getType() == product.getType())
            .findFirst()
            .ifPresent(c -> {
                throw new InvalidConfigurationException(
                    String.format("Configuration already has a %s component", product.getType())
                );
            });

        // Проверяем совместимость с существующими компонентами
        for (Product existingComponent : config.getComponents()) {
            if (!compatibilityService.areComponentsCompatible(existingComponent, product)) {
                throw new InvalidConfigurationException(
                    existingComponent.getName(),
                    product.getName(),
                    compatibilityService.getCompatibilityIssue(existingComponent, product)
                );
            }
        }

        config.getComponents().add(product);
        config.setUpdatedAt(LocalDateTime.now());
        return configRepository.save(config);
    }

    @Override
    @Transactional
    public PCConfiguration removeComponent(Long configId, Long productId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        config.getComponents().removeIf(c -> c.getId().equals(productId));
        config.setUpdatedAt(LocalDateTime.now());
        return configRepository.save(config);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkCompatibility(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        List<Product> components = config.getComponents();
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                if (!compatibilityService.areComponentsCompatible(components.get(i), components.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCompatibilityIssues(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        List<String> issues = new ArrayList<>();
        List<Product> components = config.getComponents();
        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Product comp1 = components.get(i);
                Product comp2 = components.get(j);
                if (!compatibilityService.areComponentsCompatible(comp1, comp2)) {
                    issues.add(String.format("%s and %s: %s",
                        comp1.getName(),
                        comp2.getName(),
                        compatibilityService.getCompatibilityIssue(comp1, comp2)
                    ));
                }
            }
        }
        return issues;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getCompatibleComponents(Long configId, ComponentType type) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        return productService.getProductsByType(type).stream()
            .filter(product -> config.getComponents().stream()
                .allMatch(existing -> compatibilityService.areComponentsCompatible(existing, product)))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int calculatePowerRequirement(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        return config.getComponents().stream()
            .mapToInt(Product::getPowerConsumption)
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public double calculatePerformanceScore(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        return config.getComponents().stream()
            .mapToDouble(Product::getPerformanceScore)
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getConfigurationSpecs(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        Map<String, Object> specs = new HashMap<>();
        specs.put("name", config.getName());
        specs.put("description", config.getDescription());
        specs.put("totalPrice", pricingService.calculateTotalPrice(config.getComponents()));
        specs.put("powerRequirement", calculatePowerRequirement(configId));
        specs.put("performanceScore", calculatePerformanceScore(configId));
        specs.put("components", config.getComponents().stream()
            .collect(Collectors.toMap(
                Product::getType,
                Product::getName
            )));

        return specs;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<ComponentType, List<Product>> getRecommendedComponents(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        Map<ComponentType, List<Product>> recommendations = new HashMap<>();
        for (ComponentType type : ComponentType.values()) {
            if (config.getComponents().stream().noneMatch(c -> c.getType() == type)) {
                recommendations.put(type, getCompatibleComponents(configId, type));
            }
        }
        return recommendations;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PCConfiguration> getRecommendedConfigurations(String purpose, double budget) {
        // TODO: Implement recommendation logic based on purpose and budget
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportConfiguration(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            throw new BusinessException("EXPORT_ERROR", "Failed to export configuration: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PCConfiguration importConfiguration(Long userId, String jsonConfig) {
        try {
            PCConfiguration config = objectMapper.readValue(jsonConfig, PCConfiguration.class);
            config.setId(null);
            config.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
            return configRepository.save(config);
        } catch (Exception e) {
            throw new BusinessException("IMPORT_ERROR", "Failed to import configuration: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PCConfiguration cloneConfiguration(Long configId, Long userId) {
        PCConfiguration original = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        PCConfiguration clone = new PCConfiguration();
        clone.setUser(userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId)));
        clone.setName(original.getName() + " (Copy)");
        clone.setDescription(original.getDescription());
        clone.setComponents(new ArrayList<>(original.getComponents()));
        clone.setCreatedAt(LocalDateTime.now());
        clone.setUpdatedAt(LocalDateTime.now());

        return configRepository.save(clone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkComponentsAvailability(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        return config.getComponents().stream()
            .allMatch(product -> product.getStock() > 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getMissingComponents(Long configId) {
        PCConfiguration config = configRepository.findById(configId)
            .orElseThrow(() -> new ConfigurationNotFoundException(configId));

        return config.getComponents().stream()
            .filter(product -> product.getStock() <= 0)
            .collect(Collectors.toList());
    }
} 