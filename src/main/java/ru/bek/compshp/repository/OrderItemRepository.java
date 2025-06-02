package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bek.compshp.model.OrderItem;
import ru.bek.compshp.model.OrderItemId;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.Product;
import java.util.List;

// Репозиторий для позиций заказа
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);
} 