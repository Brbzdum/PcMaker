package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Manufacturer;
import java.util.Optional;

// TODO: Репозиторий для производителей
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    // TODO: Методы поиска по имени
    Optional<Manufacturer> findByName(String name);
} 