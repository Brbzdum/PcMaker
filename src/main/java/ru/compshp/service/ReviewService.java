package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Product;
import ru.compshp.model.Review;
import ru.compshp.model.User;
import ru.compshp.repository.ReviewRepository;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления отзывами
 * Основные функции:
 * - Управление отзывами (CRUD)
 * - Модерация отзывов
 * - Расчет рейтингов
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }

    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    public List<Review> getApprovedReviews(Product product) {
        return reviewRepository.findByProductAndApprovedTrue(product);
    }

    @Transactional
    public Review createReview(Review review) {
        // Проверяем, не оставил ли пользователь уже отзыв на этот продукт
        if (reviewRepository.existsByUserAndProduct(review.getUser(), review.getProduct())) {
            throw new RuntimeException("User has already reviewed this product");
        }

        review.setApproved(false); // По умолчанию отзыв не одобрен
        Review savedReview = reviewRepository.save(review);
        
        // Обновляем рейтинг продукта
        productService.updateProductRating(review.getProduct().getId());
        
        return savedReview;
    }

    @Transactional
    public Review updateReview(Long id, Review review) {
        return reviewRepository.findById(id)
            .map(existingReview -> {
                existingReview.setRating(review.getRating());
                existingReview.setComment(review.getComment());
                existingReview.setApproved(false); // При обновлении отзыв нужно снова проверить
                Review savedReview = reviewRepository.save(existingReview);
                
                // Обновляем рейтинг продукта
                productService.updateProductRating(review.getProduct().getId());
                
                return savedReview;
            })
            .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.findById(id).ifPresent(review -> {
            Long productId = review.getProduct().getId();
            reviewRepository.deleteById(id);
            productService.updateProductRating(productId);
        });
    }

    @Transactional
    public Review approveReview(Long id) {
        return reviewRepository.findById(id)
            .map(review -> {
                review.setApproved(true);
                return reviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Transactional
    public Review rejectReview(Long id, String reason) {
        return reviewRepository.findById(id)
            .map(review -> {
                review.setApproved(false);
                review.setRejectionReason(reason);
                return reviewRepository.save(review);
            })
            .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    public double calculateAverageRating(Product product) {
        List<Review> approvedReviews = reviewRepository.findByProductAndApprovedTrue(product);
        if (approvedReviews.isEmpty()) {
            return 0.0;
        }
        return approvedReviews.stream()
            .mapToDouble(Review::getRating)
            .average()
            .orElse(0.0);
    }

    public boolean hasUserReviewedProduct(User user, Product product) {
        return reviewRepository.existsByUserAndProduct(user, product);
    }
} 