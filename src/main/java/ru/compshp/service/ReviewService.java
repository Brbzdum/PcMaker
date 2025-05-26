package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.exception.ResourceNotFoundException;
import ru.compshp.model.Review;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.repository.ReviewRepository;
import ru.compshp.repository.OrderRepository;
import ru.compshp.repository.UserRepository;
import ru.compshp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Сервис для управления отзывами
 * Основные функции:
 * - Создание и модерация отзывов
 * - Получение отзывов по продукту
 * - Аналитика отзывов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * Получить отзывы на продукт
     */
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    /**
     * Получить рейтинг продукта
     */
    public Double getProductRating(Long productId) {
        return reviewRepository.calculateAverageRating(productId);
    }

    /**
     * Создать отзыв на продукт
     */
    @Transactional
    public Review createReview(Review review, Long userId, Long productId) {
        // Проверяем существование пользователя и продукта
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        // Проверяем, купил ли пользователь товар
        boolean hasPurchased = orderRepository.existsByUserIdAndProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
        
        if (hasPurchased) {
            review.setIsVerifiedPurchase(true);
        } else {
            review.setIsVerifiedPurchase(false);
        }

        // Проверяем, не оставлял ли пользователь уже отзыв
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("Вы уже оставили отзыв на этот товар");
        }

        // Устанавливаем пользователя и продукт
        review.setUser(user);
        review.setProduct(productRepository.getById(productId));
        
        // По умолчанию отзыв не одобрен и не модерирован
        review.setIsApproved(false);
        review.setIsModerated(false);
        review.setReportCount(0);
        
        return reviewRepository.save(review);
    }

    /**
     * Обновить отзыв
     */
    @Transactional
    public Review updateReview(Long reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());
        // После обновления отзыв нужно заново модерировать
        review.setIsModerated(false);
        review.setIsApproved(false);
        
        return reviewRepository.save(review);
    }

    /**
     * Удалить отзыв
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Модерировать отзыв
     */
    @Transactional
    public Review moderateReview(Long reviewId, boolean approved) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setIsModerated(true);
        review.setIsApproved(approved);
        return reviewRepository.save(review);
    }

    /**
     * Устанавливает флаг "проверенная покупка"
     */
    @Transactional
    public Review setVerifiedPurchase(Long reviewId, boolean isVerified) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setIsVerifiedPurchase(isVerified);
        return reviewRepository.save(review);
    }

    /**
     * Получить ожидающие модерации отзывы
     */
    public List<Review> getPendingReviews() {
        return reviewRepository.findByModeratedFalse();
    }

    /**
     * Получить одобренные отзывы на продукт
     */
    public List<Review> getApprovedReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId);
    }

    /**
     * Получить аналитику по отзывам на продукт
     */
    public Map<String, Object> getReviewAnalytics(Long productId) {
        Double averageRating = reviewRepository.calculateAverageRating(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);
        Long verifiedReviews = reviewRepository.countByProductIdAndVerifiedPurchaseTrue(productId);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("averageRating", averageRating);
        analytics.put("totalReviews", totalReviews);
        analytics.put("verifiedReviews", verifiedReviews);
        
        // Добавляем распределение по рейтингам
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, reviewRepository.countByProductIdAndRating(productId, i));
        }
        analytics.put("ratingDistribution", ratingDistribution);
        
        return analytics;
    }
    
    /**
     * Найти отзывы пользователя
     */
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    /**
     * Найти отзывы по ID продукта и флагу "проверенная покупка"
     */
    public List<Review> findByProductIdAndVerifiedPurchase(Long productId, boolean isVerified) {
        return reviewRepository.findByProductIdAndIsVerifiedPurchase(productId, isVerified);
    }
    
    /**
     * Найти отзывы с флагами модерации
     */
    public List<Review> findByModeratedAndApproved(boolean isModerated, boolean isApproved) {
        return reviewRepository.findByIsModeratedAndIsApproved(isModerated, isApproved);
    }
    
    /**
     * Пометить отзыв как содержащий нарушение
     */
    @Transactional
    public Review reportReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        review.setReportCount(review.getReportCount() + 1);
        return reviewRepository.save(review);
    }
} 