package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Order;

// TODO: Репозиторий для заказов
public interface OrderRepository extends JpaRepository<Order, Long> {
    // TODO: Методы поиска по пользователю и статусу
} 