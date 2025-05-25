package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<Product> findByComponentTypeAndIsActiveTrueAndStockGreaterThanAndPowerConsumptionLessThanEqualOrderByPerformanceScoreDesc(
        ComponentType type, Integer stock, Integer maxPower);
    
    List<Product> findByComponentTypeAndIsActiveTrueAndStockGreaterThanAndPriceLessThanEqualOrderByPerformanceScoreDesc(
        ComponentType type, BigDecimal maxPrice);
    
    List<Product> findByComponentTypeAndIsActiveTrueAndStockGreaterThanAndPerformanceScoreGreaterThanEqualOrderByPriceAsc(
        ComponentType type, Double minPerformance);

    // Методы для магазина
    List<Product> findByStockGreaterThanAndIsActiveTrue(Integer stock);
    List<Product> findByStockLessThanEqualAndIsActiveTrue(Integer threshold);
    List<Product> findByRatingGreaterThanEqualAndIsActiveTrue(Double minRating);
    List<Product> findByPriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByDiscountGreaterThanAndIsActiveTrue(Integer discount);
    List<Product> findByPowerConsumptionLessThanEqualAndIsActiveTrue(Integer maxPower);

    // Методы для готовых сборок
    List<Product> findByCategoryIdAndIsActiveTrueAndStockGreaterThan(Long categoryId, Integer stock);

    // Методы для работы со спецификациями
    List<Product> findBySpecsContainingAndIsActiveTrue(String spec);
    List<Product> findByCompatibilitySpecsContainingAndIsActiveTrue(String compatibilitySpec);
} 