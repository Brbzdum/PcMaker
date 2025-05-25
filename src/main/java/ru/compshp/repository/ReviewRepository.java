package ru.compshp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.compshp.model.Review;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import java.util.List;

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
} 