package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.OrderStatusHistory;
import ru.bek.compshp.model.enums.OrderStatus;
import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);
    List<OrderStatusHistory> findByOrderIdAndStatus(Long orderId, OrderStatus status);
    List<OrderStatusHistory> findByStatus(OrderStatus status);
} 