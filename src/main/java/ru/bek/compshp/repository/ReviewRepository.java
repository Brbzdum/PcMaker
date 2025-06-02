package ru.bek.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bek.compshp.model.Review;
import ru.bek.compshp.model.Product;
import ru.bek.compshp.model.User;
import java.util.List;
import java.util.Optional;

// TODO: Репозиторий для отзывов
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Поиск по продукту
    List<Review> findByProduct(Product product);
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    List<Review> findByProductOrderByRatingDesc(Product product);
    List<Review> findByProductId(Long productId);
    
    // Поиск по пользователю
    List<Review> findByUser(User user);
    List<Review> findByUserId(Long userId);
    
    // Поиск по статусу модерации
    List<Review> findByProductAndIsApprovedTrue(Product product);
    List<Review> findByIsApprovedFalse();
    List<Review> findByProductIdAndIsApproved(Long productId, Boolean isApproved);
    List<Review> findByProductIdAndIsApprovedTrue(Long productId);
    List<Review> findByIsModeratedFalse();
    List<Review> findByIsModeratedAndIsApproved(Boolean isModerated, Boolean isApproved);
    
    // Поиск по флагу проверенной покупки
    List<Review> findByProductIdAndIsVerifiedPurchase(Long productId, Boolean isVerified);
    
    // Поиск по рейтингу
    List<Review> findByRatingGreaterThanEqualAndIsApprovedTrue(Integer minRating);
    
    // Поиск проверенных отзывов
    List<Review> findByIsVerifiedPurchaseTrueAndIsApprovedTrue();
    
    // Поиск по жалобам
    List<Review> findByReportCountGreaterThanAndIsModeratedFalse(Integer threshold);
    
    // Поиск по модерации
    Page<Review> findByIsModeratedFalse(Pageable pageable);
    Page<Review> findByReportCountGreaterThan(Integer count, Pageable pageable);
    
    // Поиск по продукту с пагинацией
    Page<Review> findByProductIdAndIsApprovedTrue(Long productId, Pageable pageable);
    
    // Поиск по пользователю и продукту
    Review findByUserAndProduct(User user, Product product);
    
    // Проверка существования отзыва
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Счетчики
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.isVerifiedPurchase = true")
    Long countByProductIdAndVerifiedPurchaseTrue(@Param("productId") Long productId);
    
    // Расчет средней оценки продукта
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId AND r.isApproved = true")
    Double calculateAverageRating(@Param("productId") Long productId);
    
    // Дополнительные методы
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.rating = :rating")
    Long countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> countByProductIdGroupByRating(@Param("productId") Long productId);
    
    /**
     * Найти отзыв по ID
     * @param id ID отзыва
     * @return отзыв
     */
    Optional<Review> findReviewById(Long id);
} 