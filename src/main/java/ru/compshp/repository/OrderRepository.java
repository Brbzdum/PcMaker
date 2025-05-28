package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Order;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с заказами
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Находит все заказы пользователя
     * @param userId ID пользователя
     * @return список заказов
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Находит все заказы пользователя с пагинацией
     * @param userId ID пользователя
     * @param pageable параметры пагинации
     * @return страница заказов
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Находит все заказы со статусом (строковое представление)
     * @param status статус заказа (строка)
     * @return список заказов
     */
    List<Order> findByStatus(String status);
    
    /**
     * Находит все заказы со статусом (перечисление)
     * @param status статус заказа (enum)
     * @return список заказов
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Находит все заказы созданные после указанной даты
     * @param date дата
     * @return список заказов
     */
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Считает количество заказов созданных после указанной даты
     * @param date дата
     * @return количество заказов
     */
    Long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Находит 5 последних заказов
     * @return список заказов
     */
    List<Order> findTop5ByOrderByCreatedAtDesc();
    
    /**
     * Считает общую сумму всех заказов
     * @return общая сумма
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal calculateTotalRevenue();
    
    /**
     * Считает общую сумму заказов после указанной даты
     * @param date дата
     * @return общая сумма
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt > :date")
    BigDecimal calculateRevenueAfterDate(@Param("date") LocalDateTime date);

    // Поиск по пользователю
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByUser(User user);
    List<Order> findByUser_Id(Long userId);
    
    // Поиск по статусу
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