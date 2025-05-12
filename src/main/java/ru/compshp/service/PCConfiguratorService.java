package ru.compshp.service;

import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для управления конфигурациями ПК
 * Основные функции:
 * - Создание и управление конфигурациями
 * - Проверка совместимости компонентов
 * - Расчет производительности
 * - Рекомендации по компонентам
 */
public interface PCConfiguratorService {
    /**
     * Создает новую конфигурацию ПК
     */
    PCConfiguration createConfiguration(Long userId, String name, String description);

    /**
     * Обновляет существующую конфигурацию
     */
    PCConfiguration updateConfiguration(Long configId, String name, String description);

    /**
     * Удаляет конфигурацию
     */
    void deleteConfiguration(Long configId);

    /**
     * Получает конфигурацию по ID
     */
    Optional<PCConfiguration> getConfiguration(Long configId);

    /**
     * Получает все конфигурации пользователя
     */
    List<PCConfiguration> getUserConfigurations(Long userId);

    /**
     * Добавляет компонент в конфигурацию
     */
    PCConfiguration addComponent(Long configId, Long productId);

    /**
     * Удаляет компонент из конфигурации
     */
    PCConfiguration removeComponent(Long configId, Long productId);

    /**
     * Проверяет совместимость компонентов в конфигурации
     */
    boolean checkCompatibility(Long configId);

    /**
     * Получает список проблем совместимости
     */
    List<String> getCompatibilityIssues(Long configId);

    /**
     * Получает список совместимых компонентов для указанного типа
     */
    List<Product> getCompatibleComponents(Long configId, ComponentType type);

    /**
     * Рассчитывает требуемую мощность блока питания
     */
    int calculatePowerRequirement(Long configId);

    /**
     * Рассчитывает общую производительность конфигурации
     */
    double calculatePerformanceScore(Long configId);

    /**
     * Получает спецификации конфигурации
     */
    Map<String, Object> getConfigurationSpecs(Long configId);

    /**
     * Получает рекомендуемые компоненты для улучшения конфигурации
     */
    Map<ComponentType, List<Product>> getRecommendedComponents(Long configId);

    /**
     * Получает рекомендуемые готовые конфигурации
     */
    List<PCConfiguration> getRecommendedConfigurations(String purpose, double budget);

    /**
     * Экспортирует конфигурацию в JSON
     */
    String exportConfiguration(Long configId);

    /**
     * Импортирует конфигурацию из JSON
     */
    PCConfiguration importConfiguration(Long userId, String jsonConfig);

    /**
     * Клонирует существующую конфигурацию
     */
    PCConfiguration cloneConfiguration(Long configId, Long userId);

    /**
     * Проверяет наличие компонентов на складе
     */
    boolean checkComponentsAvailability(Long configId);

    /**
     * Получает список отсутствующих компонентов
     */
    List<Product> getMissingComponents(Long configId);
} 