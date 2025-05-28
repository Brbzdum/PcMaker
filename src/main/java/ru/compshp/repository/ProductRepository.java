package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Product;
import ru.compshp.model.Manufacturer;
import ru.compshp.model.enums.ComponentType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для работы с продуктами
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Находит продукты по типу компонента
     * @param componentType тип компонента
     * @return список продуктов
     */
    List<Product> findByComponentType(ComponentType componentType);
    
    /**
     * Находит продукты по типу компонента с пагинацией
     * @param componentType тип компонента
     * @param pageable параметры пагинации
     * @return страница продуктов
     */
    Page<Product> findByComponentType(ComponentType componentType, Pageable pageable);
    
    /**
     * Находит активные продукты
     * @return список продуктов
     */
    List<Product> findByIsActiveTrue();
    
    /**
     * Находит активные продукты с пагинацией
     * @param pageable параметры пагинации
     * @return страница продуктов
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Находит продукты в заданном ценовом диапазоне
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список продуктов
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Находит продукты по имени, содержащему подстроку (без учета регистра)
     * @param name подстрока для поиска
     * @return список продуктов
     */
    List<Product> findByTitleContainingIgnoreCase(String name);
    
    /**
     * Находит продукты с количеством на складе меньше заданного
     * @param stock количество
     * @return список продуктов
     */
    List<Product> findByStockLessThan(Integer stock);
    
    /**
     * Находит продукты по идентификатору производителя
     * @param manufacturerId идентификатор производителя
     * @return список продуктов
     */
    List<Product> findByManufacturerId(Long manufacturerId);
    
    /**
     * Находит продукты по категории
     * @param categoryId идентификатор категории
     * @return список продуктов
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Методы для магазина
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.isActive = true")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
    
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating AND p.isActive = true")
    List<Product> findByMinRating(@Param("minRating") Double minRating);
    
    @Query("SELECT p FROM Product p JOIN p.specs s WHERE KEY(s) = 'discount' AND CAST(VALUE(s) AS int) > :minDiscount AND p.isActive = true")
    List<Product> findByMinDiscount(@Param("minDiscount") Integer minDiscount);
    
    // Методы для готовых сборок
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.componentType IS NULL AND p.isActive = true")
    List<Product> findReadyPCs(@Param("categoryId") Long categoryId);
    
    // Методы для работы со спецификациями
    @Query("SELECT p FROM Product p JOIN p.specs s WHERE VALUE(s) LIKE %:spec% AND p.isActive = true")
    List<Product> findBySpecsContaining(@Param("spec") String spec);
    
    // Методы для поиска компонентов для конфигуратора
    @Query("SELECT p FROM Product p WHERE p.componentType = :type AND p.price <= :maxPrice AND p.isActive = true AND p.stock > 0")
    List<Product> findComponentsInBudget(
        @Param("type") ComponentType type,
        @Param("maxPrice") BigDecimal maxPrice
    );
    
    @Query("SELECT p FROM Product p JOIN p.specs s WHERE p.componentType = :type AND KEY(s) = 'performance' AND CAST(VALUE(s) AS double) >= :minPerformance AND p.isActive = true AND p.stock > 0")
    List<Product> findComponentsByMinPerformance(
        @Param("type") ComponentType type,
        @Param("minPerformance") Double minPerformance
    );
    
    // Методы для поиска по спецификациям
    @Query("SELECT DISTINCT p FROM Product p JOIN p.specs s WHERE p.isActive = true AND (KEY(s) IN :keys)")
    List<Product> findBySpecs(@Param("keys") Map<String, String> specs);
    
    // Методы для работы с производителями
    @Query("SELECT m FROM Manufacturer m WHERE m.id = :id")
    Optional<Manufacturer> findManufacturerById(@Param("id") Long id);
    
    @Query("SELECT p FROM Product p WHERE p.manufacturer = :manufacturer")
    List<Product> findByManufacturer(@Param("manufacturer") Manufacturer manufacturer);
    
    default Manufacturer saveManufacturer(Manufacturer manufacturer) {
        throw new UnsupportedOperationException("This method should be implemented in ManufacturerRepository");
    }
} 