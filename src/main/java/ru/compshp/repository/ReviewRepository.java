package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.compshp.model.Review;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import java.util.List;

// TODO: Репозиторий для отзывов
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // TODO: Методы поиска по товару и пользователю
    List<Review> findByProduct(Product product);
    List<Review> findByUser(User user);
} 