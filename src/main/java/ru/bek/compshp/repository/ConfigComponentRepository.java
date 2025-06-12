package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.ConfigComponent;
import ru.bek.compshp.model.ConfigComponentId;
import ru.bek.compshp.model.PCConfiguration;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;

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
    @Query("SELECT c FROM ConfigComponent c WHERE c.id.configId = :configId")
    List<ConfigComponent> findByConfigId(@Param("configId") Long configId);
    
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
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ConfigComponent c WHERE c.id.configId = :configId AND c.product.componentType = :componentType")
    boolean existsByIdConfigIdAndProduct_ComponentType(@Param("configId") Long configId, @Param("componentType") ComponentType componentType);
    
    /**
     * Находит компоненты по идентификатору конфигурации и идентификатору продукта
     * @param configId идентификатор конфигурации
     * @param productId идентификатор продукта
     * @return компонент
     */
    @Query("SELECT c FROM ConfigComponent c WHERE c.id.configId = :configId AND c.id.productId = :productId")
    ConfigComponent findByConfigIdAndProductId(@Param("configId") Long configId, @Param("productId") Long productId);

    /**
     * Удаляет все компоненты конфигурации по ID конфигурации
     * @param configId ID конфигурации
     */
    void deleteByIdConfigId(Long configId);
} 