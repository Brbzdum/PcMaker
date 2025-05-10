package ru.compshp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Review;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import java.util.List;

// TODO: Репозиторий для отзывов
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // TODO: Методы поиска по товару и пользователю
    List<Review> findByProduct(Product product);
    List<Review> findByUser(User user);
    List<Review> findByProductAndApprovedTrue(Product product);
    List<Review> findByApprovedFalse();
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    List<Review> findByProductOrderByRatingDesc(Product product);
    Review findByUserAndProduct(User user, Product product);
} 