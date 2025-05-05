package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.ProductCompatibility;

// TODO: Репозиторий для совместимости продуктов
public interface ProductCompatibilityRepository extends JpaRepository<ProductCompatibility, Long> {
    // TODO: Методы поиска по продуктам и правилам
} 