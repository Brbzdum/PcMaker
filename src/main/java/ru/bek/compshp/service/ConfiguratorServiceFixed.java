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
 * Исправленная версия сервиса для работы с конфигуратором ПК
 */
@Service
@RequiredArgsConstructor
public class ConfiguratorServiceFixed {
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ConfigComponentRepository configComponentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ComponentCompatibilityService compatibilityService;
    private static final Logger log = LoggerFactory.getLogger(ConfiguratorServiceFixed.class);

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
        
        // Создаем конфигурацию
        PCConfiguration config = new PCConfiguration();
        config.setUser(user);
        config.setName(name);
        config.setDescription(description);
        config.setCategory(category);
        config.setTotalPrice(BigDecimal.ZERO);
        config.setTotalPerformance(0.0);
        config.setIsCompatible(true);
        config.setComponents(new HashSet<>());
        
        // Сохраняем конфигурацию для получения ID
        PCConfiguration savedConfig = pcConfigurationRepository.save(config);
        final Long configId = savedConfig.getId();
        
        // Собираем информацию о компонентах и считаем общую цену
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalPerformance = 0.0;
        
        // Получаем продукты заранее
        Map<Long, Product> productsMap = new HashMap<>();
        for (Long productId : componentIds) {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            productsMap.put(productId, product);
            
            // Суммируем цену
            if (product.getPrice() != null) {
                totalPrice = totalPrice.add(product.getPrice());
            }
            
            // Суммируем производительность
            try {
                String performanceStr = product.getSpec("performance");
                if (performanceStr != null && !performanceStr.isEmpty()) {
                    double productPerformance = Double.parseDouble(performanceStr);
                    totalPerformance += productPerformance;
                }
            } catch (Exception e) {
                // Если характеристики нет или она не числовая, игнорируем
            }
        }
        
        // Обновляем общую цену и производительность
        savedConfig.setTotalPrice(totalPrice);
        savedConfig.setTotalPerformance(totalPerformance);
        
        // Сохраняем обновленную конфигурацию
        pcConfigurationRepository.save(savedConfig);
        
        // Добавляем компоненты по одному, используя метод addComponent
        for (Long productId : componentIds) {
            try {
                Product product = productsMap.get(productId);
                
                ConfigComponentId componentId = new ConfigComponentId(configId, productId);
                ConfigComponent component = new ConfigComponent();
                component.setId(componentId);
                component.setProduct(product);
                
                // Используем метод addComponent для установки двусторонней связи
                savedConfig.addComponent(component);
                
                // Сохраняем компонент
                configComponentRepository.save(component);
            } catch (Exception e) {
                log.warn("Не удалось добавить компонент {} в конфигурацию {}: {}", 
                    productId, configId, e.getMessage(), e);
            }
        }
        
        // Сохраняем конфигурацию с компонентами
        PCConfiguration updatedConfig = pcConfigurationRepository.save(savedConfig);
        
        // Загружаем конфигурацию заново с компонентами
        return pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));
    }
} 