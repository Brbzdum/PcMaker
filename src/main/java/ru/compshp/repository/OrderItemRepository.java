package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.OrderItem;
import ru.compshp.model.OrderItemId;
import ru.compshp.model.Order;
import ru.compshp.model.Product;
import java.util.List;

// Репозиторий для позиций заказа
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
} 