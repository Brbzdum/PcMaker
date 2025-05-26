package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);
    
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
    
    // Проверка на покупку продукта пользователем
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
           "JOIN o.items i WHERE o.user.id = :userId AND i.product.id = :productId AND o.status = :status")
    boolean existsByUserIdAndProductIdAndStatus(
        @Param("userId") Long userId, 
        @Param("productId") Long productId, 
        @Param("status") OrderStatus status
    );

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<Order> findByTotalPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT o FROM Order o WHERE o.deliveryAddress LIKE %:address%")
    List<Order> findByDeliveryAddressContaining(@Param("address") String address);
} 