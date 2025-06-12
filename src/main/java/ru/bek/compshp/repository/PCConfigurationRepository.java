package ru.bek.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.PCConfiguration;
import ru.bek.compshp.model.User;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.enums.ComponentType;
import java.math.BigDecimal;
import java.util.List;

// TODO: Репозиторий для конфигураций ПК
@Repository
public interface PCConfigurationRepository extends JpaRepository<PCConfiguration, Long> {
    // Поиск по пользователю
    List<PCConfiguration> findByUser(User user);
    Page<PCConfiguration> findByUser(User user, Pageable pageable);
    List<PCConfiguration> findByUserOrderByCreatedAtDesc(User user);
    
    // Поиск по совместимости
    List<PCConfiguration> findByUserAndIsCompatibleTrue(User user);
    
    // Поиск по цене
    List<PCConfiguration> findByTotalPriceBetween(Double minPrice, Double maxPrice);
    
    // Поиск последних конфигураций
    List<PCConfiguration> findByIsCompatibleTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Подсчет конфигураций пользователя
    long countByUser(User user);
    
    // Поиск по бюджету
    List<PCConfiguration> findByIsCompatibleTrueAndTotalPriceLessThanEqual(Double budget);
    
    // Поиск по бюджету с сортировкой по производительности
    List<PCConfiguration> findByIsCompatibleTrueAndTotalPriceLessThanEqualOrderByTotalPerformanceDesc(Double budget, Pageable pageable);

    List<PCConfiguration> findByUserId(Long userId);
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<PCConfiguration> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
    
    @Query("SELECT pc FROM PCConfiguration pc WHERE pc.totalPerformance >= :minPerformance")
    List<PCConfiguration> findByMinPerformance(@Param("minPerformance") Double minPerformance);
    
    @Query("""
        SELECT p FROM Product p 
        WHERE p.componentType = :type 
        AND p.isActive = true
        AND NOT EXISTS (
            SELECT cr FROM CompatibilityRule cr 
            WHERE (cr.sourceType = p.componentType AND cr.targetType IN 
                  (SELECT cc.product.componentType FROM ConfigComponent cc WHERE cc.configuration.id = :configId))
            OR (cr.targetType = p.componentType AND cr.sourceType IN 
                (SELECT cc.product.componentType FROM ConfigComponent cc WHERE cc.configuration.id = :configId))
            AND cr.isActive = false
        )
    """)
    List<Product> findCompatibleComponents(
        @Param("type") ComponentType type,
        @Param("configId") Long configId
    );
    
    @Query("""
        SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END 
        FROM CompatibilityRule cr 
        WHERE ((cr.sourceType = (SELECT p1.componentType FROM Product p1 WHERE p1.id = :product1Id) 
               AND cr.targetType = (SELECT p2.componentType FROM Product p2 WHERE p2.id = :product2Id))
              OR
              (cr.sourceType = (SELECT p2.componentType FROM Product p2 WHERE p2.id = :product2Id) 
               AND cr.targetType = (SELECT p1.componentType FROM Product p1 WHERE p1.id = :product1Id)))
        AND cr.isActive = true
    """)
    boolean checkCompatibility(
        @Param("product1Id") Long product1Id,
        @Param("product2Id") Long product2Id
    );
    
    @Query("""
        SELECT pc FROM PCConfiguration pc 
        WHERE pc.id IN (
            SELECT c.configuration.id 
            FROM ConfigComponent c 
            GROUP BY c.configuration.id 
            HAVING COUNT(c) = :componentCount
        )
    """)
    List<PCConfiguration> findCompleteConfigurations(@Param("componentCount") int componentCount);
    
    @Query("""
        SELECT pc FROM PCConfiguration pc 
        WHERE pc.id IN (
            SELECT c.configuration.id 
            FROM ConfigComponent c 
            GROUP BY c.configuration.id 
            HAVING COUNT(c) < :componentCount
        )
    """)
    List<PCConfiguration> findIncompleteConfigurations(@Param("componentCount") int componentCount);

    // Поиск публичных конфигураций
    List<PCConfiguration> findByIsPublicTrue();
} 