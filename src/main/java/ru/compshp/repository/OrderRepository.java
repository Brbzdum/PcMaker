package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Order;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Репозиторий для заказов
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUserAndStatusIn(User user, List<OrderStatus> statuses);
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN ?1 AND ?2")
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.userId = ?1 AND o.createdAt BETWEEN ?2 AND ?3")
    List<Order> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status = ?1 AND o.createdAt BETWEEN ?2 AND ?3")
    List<Order> findByStatusAndDateRange(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);
} 