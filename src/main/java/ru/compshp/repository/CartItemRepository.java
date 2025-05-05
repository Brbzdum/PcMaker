package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.CartItem;

// TODO: Репозиторий для позиций корзины
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // TODO: Методы поиска по корзине и товару
} 