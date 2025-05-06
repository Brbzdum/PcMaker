package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Sale;
import ru.compshp.model.Order;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Репозиторий для продаж
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
    Sale findByOrder(Order order);
} 