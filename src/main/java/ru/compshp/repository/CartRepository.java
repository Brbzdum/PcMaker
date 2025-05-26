package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Cart;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Поиск по пользователю
    Cart findByUser_Id(Long userId);
    
    // Поиск непустых корзин
    List<Cart> findByItemsIsNotEmpty();
    
    // Поиск корзин с количеством товаров больше указанного
    List<Cart> findByItems_SizeGreaterThan(Integer itemCount);
    
    // Поиск непустой корзины пользователя
    Cart findByUser_IdAndItemsIsNotEmpty(Long userId);

    Optional<Cart> findByUserId(Long userId);
} 