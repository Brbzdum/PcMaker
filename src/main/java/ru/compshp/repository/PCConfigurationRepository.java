package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.PCConfiguration;
import ru.compshp.model.User;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
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
    List<PCConfiguration> findByIsCompatibleTrueAndTotalPriceLessThanEqualOrderByPerformanceScoreDesc(Double budget, Pageable pageable);

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
        AND p.id NOT IN (
            SELECT pc.productId FROM ProductCompatibility pc 
            WHERE pc.productIdSource IN (
                SELECT cc.product.id FROM ConfigComponent cc 
                WHERE cc.configuration.id = :configId
            )
            AND pc.valid = false
        )
        AND p.isActive = true
    """)
    List<Product> findCompatibleComponents(
        @Param("type") ComponentType type,
        @Param("configId") Long configId
    );
    
    @Query("""
        SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END 
        FROM ProductCompatibility pc 
        WHERE pc.productIdSource = :product1Id 
        AND pc.productIdTarget = :product2Id 
        AND pc.valid = true
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
} 