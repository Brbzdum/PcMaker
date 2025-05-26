package ru.compshp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Manufacturer;
import java.util.List;
import java.util.Optional;

// TODO: Репозиторий для производителей
@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    // TODO: Методы поиска по имени
    Optional<Manufacturer> findByName(String name);
    
    @Query("SELECT m FROM Manufacturer m JOIN m.products p GROUP BY m ORDER BY COUNT(p) DESC")
    List<Manufacturer> findTopByProductCount(Pageable pageable);
} 