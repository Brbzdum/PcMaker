package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    Page<Review> findByProductIdAndApprovedTrue(Long productId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1 AND r.approved = true")
    Double calculateAverageRating(Long productId);

    Page<Review> findByModeratedFalse(Pageable pageable);

    Page<Review> findByReportCountGreaterThan(Integer count, Pageable pageable);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = ?1 AND i.product.id = ?2")
    boolean hasUserPurchasedProduct(Long userId, Long productId);

    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    List<Review> findByProductIdAndIsApproved(Long productId, Boolean isApproved);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= ?1 AND r.isApproved = true")
    List<Review> findHighlyRatedReviews(Integer minRating);
    
    @Query("SELECT r FROM Review r WHERE r.isVerifiedPurchase = true AND r.isApproved = true")
    List<Review> findVerifiedReviews();
    
    @Query("SELECT r FROM Review r WHERE r.reportCount > ?1 AND r.isModerated = false")
    List<Review> findReportedReviews(Integer threshold);
} 