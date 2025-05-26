package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.compshp.model.ConfigComponent;
import ru.compshp.model.ConfigComponentId;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import java.util.List;

// TODO: Репозиторий для компонентов конфигурации
public interface ConfigComponentRepository extends JpaRepository<ConfigComponent, ConfigComponentId> {
    List<ConfigComponent> findByConfiguration(PCConfiguration configuration);
    List<ConfigComponent> findByProduct(Product product);
    
    @Query("SELECT cc FROM ConfigComponent cc WHERE cc.configuration.id = :configId")
    List<ConfigComponent> findByConfigurationId(@Param("configId") Long configId);
    
    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ConfigComponent cc " +
           "WHERE cc.configuration.id = :configId AND cc.product.componentType = :componentType")
    boolean existsByConfigurationIdAndProduct_ComponentType(
        @Param("configId") Long configId,
        @Param("componentType") ComponentType componentType
    );

    @Query("SELECT cc FROM ConfigComponent cc WHERE cc.configuration.id = :configId AND cc.product.componentType = :componentType")
    List<ConfigComponent> findByConfigurationIdAndComponentType(
        @Param("configId") Long configId,
        @Param("componentType") ComponentType componentType
    );
} 