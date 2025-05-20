package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Cart;
import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);

    @Query("SELECT c FROM Cart c JOIN c.items i WHERE i IS NOT NULL")
    List<Cart> findNonEmptyCarts();

    @Query("SELECT c FROM Cart c JOIN c.items i GROUP BY c HAVING COUNT(i) > ?1")
    List<Cart> findCartsWithMoreItemsThan(Integer itemCount);

    @Query("SELECT c FROM Cart c JOIN c.items i WHERE c.user.id = ?1 AND i IS NOT NULL")
    Cart findUserNonEmptyCart(Long userId);
} 