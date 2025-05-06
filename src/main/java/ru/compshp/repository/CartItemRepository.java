package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.CartItem;
import ru.compshp.model.Cart;
import ru.compshp.model.Product;
import java.util.List;

// TODO: Репозиторий для позиций корзины
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByProduct(Product product);
} 