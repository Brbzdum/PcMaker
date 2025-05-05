package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.OrderItem;

// TODO: Репозиторий для позиций заказа
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // TODO: Методы поиска по заказу и товару
} 