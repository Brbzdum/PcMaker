package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Order;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import java.util.List;

// TODO: Репозиторий для заказов
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUserAndStatusIn(User user, List<OrderStatus> statuses);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserAndStatus(User user, OrderStatus status);
} 