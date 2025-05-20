package ru.compshp.service;

import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConfiguratorService {
    PCConfiguration createConfiguration(Long userId, String name, String description);
    PCConfiguration updateConfiguration(Long configId, String name, String description);
    void deleteConfiguration(Long configId);
    Optional<PCConfiguration> getConfiguration(Long configId);
    List<PCConfiguration> getUserConfigurations(Long userId);
    PCConfiguration addComponent(Long configId, Long productId);
    PCConfiguration removeComponent(Long configId, Long productId);
    Map<String, Object> getCompatibilityInfo(Long configId);
    List<Product> getCompatibleComponents(Long configId, ComponentType type);
    int calculatePowerRequirement(Long configId);
    double calculatePerformanceScore(Long configId);
    Map<String, Object> getConfigurationSpecs(Long configId);
    Map<ComponentType, List<Product>> getRecommendedComponents(Long configId);
    List<PCConfiguration> getRecommendedConfigurations(String purpose, double budget);
    String exportConfiguration(Long configId);
    PCConfiguration importConfiguration(Long userId, String jsonConfig);
    PCConfiguration cloneConfiguration(Long configId, Long userId);
    boolean checkComponentsAvailability(Long configId);
    List<Product> getMissingComponents(Long configId);
    List<PCConfiguration> getSavedConfigurations();
    void addConfigurationToCart(Long configId);
    List<PCConfiguration> getPopularConfigurations();
    Map<String, Object> getConfiguratorStatistics();
} 