package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Order;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

// TODO: Репозиторий для заказов
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Поиск по пользователю
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUser(User user);
    List<Order> findByUser_Id(Long userId);
    
    // Поиск по статусу
    List<Order> findByStatus(OrderStatus status);
    
    // Комбинированный поиск по пользователю и статусу
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    List<Order> findByUserAndStatusIn(User user, List<OrderStatus> statuses);
    List<Order> findByUser_IdAndStatus(Long userId, OrderStatus status);
    
    // Поиск по датам
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Комбинированный поиск по пользователю и датам
    List<Order> findByUser_IdAndCreatedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Комбинированный поиск по статусу и датам
    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);
} 