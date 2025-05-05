package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Sale;

// TODO: Репозиторий для продаж
public interface SaleRepository extends JpaRepository<Sale, Long> {
    // TODO: Методы поиска по периоду, товару, категории
} 