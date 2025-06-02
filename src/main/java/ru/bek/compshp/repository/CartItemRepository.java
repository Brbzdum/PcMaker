package ru.bek.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bek.compshp.model.CartItem;
import ru.bek.compshp.model.CartItemId;
import ru.bek.compshp.model.Cart;
import ru.bek.compshp.model.Product;
import java.util.List;

// TODO: Репозиторий для позиций корзины
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByProduct(Product product);
} 