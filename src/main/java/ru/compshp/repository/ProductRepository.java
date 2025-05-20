package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Базовые методы поиска
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByManufacturerId(Long manufacturerId);
    List<Product> findByComponentType(ComponentType componentType);
    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);
    List<Product> findByManufacturerIdAndIsActive(Long manufacturerId, Boolean isActive);
    List<Product> findByComponentTypeAndIsActive(ComponentType componentType, Boolean isActive);
    
    // Методы для конфигуратора
    @Query("SELECT p FROM Product p WHERE p.componentType = ?1 AND p.isActive = true AND p.stock > 0 " +
           "AND p.powerConsumption <= ?2 ORDER BY p.performanceScore DESC")
    List<Product> findCompatibleComponents(ComponentType type, Integer maxPower);

    @Query("SELECT p FROM Product p WHERE p.componentType = ?1 AND p.isActive = true AND p.stock > 0 " +
           "AND p.price <= ?2 ORDER BY p.performanceScore DESC")
    List<Product> findComponentsInBudget(ComponentType type, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.componentType = ?1 AND p.isActive = true AND p.stock > 0 " +
           "AND p.performanceScore >= ?2 ORDER BY p.price ASC")
    List<Product> findComponentsByMinPerformance(ComponentType type, Double minPerformance);

    // Методы для магазина
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isActive = true")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stock <= ?1 AND p.isActive = true")
    List<Product> findLowStockProducts(Integer threshold);
    
    @Query("SELECT p FROM Product p WHERE p.rating >= ?1 AND p.isActive = true")
    List<Product> findHighlyRatedProducts(Double minRating);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2 AND p.isActive = true")
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.discount > 0 AND p.isActive = true")
    List<Product> findDiscountedProducts();

    @Query("SELECT p FROM Product p WHERE p.powerConsumption <= ?1 AND p.isActive = true")
    List<Product> findByMaxPowerConsumption(Integer maxPower);

    // Методы для готовых сборок
    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 AND p.isActive = true AND p.stock > 0")
    List<Product> findReadyPCs(Long categoryId);

    // Методы для работы со спецификациями
    @Query("SELECT p FROM Product p WHERE p.specs LIKE %?1% AND p.isActive = true")
    List<Product> findBySpecification(String spec);

    @Query("SELECT p FROM Product p WHERE p.compatibilitySpecs LIKE %?1% AND p.isActive = true")
    List<Product> findByCompatibilitySpec(String compatibilitySpec);
} 