package ru.bek.compshp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.Manufacturer;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с производителями
 */
@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    
    /**
     * Находит производителя по имени
     * @param name имя производителя
     * @return производитель
     */
    Optional<Manufacturer> findByName(String name);
    
    /**
     * Проверяет существование производителя с заданным именем
     * @param name имя производителя
     * @return true, если производитель существует
     */
    boolean existsByName(String name);
    
    /**
     * Находит производителей по имени, содержащему подстроку
     * @param name подстрока для поиска
     * @return список производителей
     */
    List<Manufacturer> findByNameContainingIgnoreCase(String name);
    
    /**
     * Считает количество продуктов у производителя
     * @param manufacturerId ID производителя
     * @return количество продуктов
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.manufacturer.id = :manufacturerId")
    long countProductsByManufacturerId(@Param("manufacturerId") Long manufacturerId);

    @Query("SELECT m FROM Manufacturer m JOIN m.products p GROUP BY m ORDER BY COUNT(p) DESC")
    List<Manufacturer> findTopByProductCount(Pageable pageable);
} 