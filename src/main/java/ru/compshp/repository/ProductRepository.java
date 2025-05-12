package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Product;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.model.enums.ProductCategory;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByManufacturerId(Long manufacturerId);
    List<Product> findByComponentType(ComponentType componentType);
    List<Product> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive);
    List<Product> findByManufacturerIdAndIsActive(Long manufacturerId, Boolean isActive);
    List<Product> findByComponentTypeAndIsActive(ComponentType componentType, Boolean isActive);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isActive = true")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stock <= ?1 AND p.isActive = true")
    List<Product> findLowStockProducts(Integer threshold);
    
    @Query("SELECT p FROM Product p WHERE p.averageRating >= ?1 AND p.isActive = true")
    List<Product> findHighlyRatedProducts(Double minRating);
} 