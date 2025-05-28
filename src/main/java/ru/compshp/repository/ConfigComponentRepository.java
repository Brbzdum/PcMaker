package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.ConfigComponent;
import ru.compshp.model.ConfigComponentId;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;

import java.util.List;

/**
 * Репозиторий для работы с компонентами конфигурации ПК
 */
@Repository
public interface ConfigComponentRepository extends JpaRepository<ConfigComponent, ConfigComponentId> {
    /**
     * Находит компоненты по идентификатору конфигурации
     * @param configId идентификатор конфигурации
     * @return список компонентов
     */
    List<ConfigComponent> findByConfigId(Long configId);
    
    /**
     * Находит компоненты по конфигурации
     * @param configuration конфигурация
     * @return список компонентов
     */
    List<ConfigComponent> findByConfiguration(PCConfiguration configuration);
    
    /**
     * Находит компоненты по продукту
     * @param product продукт
     * @return список компонентов
     */
    List<ConfigComponent> findByProduct(Product product);
    
    /**
     * Проверяет, существует ли компонент с указанным типом в конфигурации
     * @param configId идентификатор конфигурации
     * @param componentType тип компонента
     * @return true, если компонент существует
     */
    boolean existsByConfigIdAndProduct_ComponentType(Long configId, ComponentType componentType);
    
    /**
     * Находит компоненты по идентификатору конфигурации и идентификатору продукта
     * @param configId идентификатор конфигурации
     * @param productId идентификатор продукта
     * @return компонент
     */
    ConfigComponent findByConfigIdAndProductId(Long configId, Long productId);
} 