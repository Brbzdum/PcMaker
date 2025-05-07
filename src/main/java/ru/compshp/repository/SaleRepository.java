package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Sale;
import ru.compshp.model.Order;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Репозиторий для продаж
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Sale findByOrder(Order order);
} 