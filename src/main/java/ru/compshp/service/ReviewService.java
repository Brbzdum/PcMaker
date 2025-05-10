package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Review;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.repository.ReviewRepository;
import ru.compshp.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

    public List<Review> getByProduct(Product product) {
        return reviewRepository.findByProduct(product);
    }

    public List<Review> getByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    @Transactional
    public Review save(Review review) {
        Review savedReview = reviewRepository.save(review);
        productService.updateProductRating(review.getProduct());
        return savedReview;
    }

    @Transactional
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Product product = review.getProduct();
        reviewRepository.deleteById(id);
        productService.updateProductRating(product);
    }

    public double calculateAverageRating(Product product) {
        return reviewRepository.findByProduct(product).stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Review> getPopularReviews(Product product) {
        return reviewRepository.findByProduct(product).stream()
                .filter(review -> review.getLikes() != null)
                .sorted((r1, r2) -> Integer.compare(r2.getLikes(), r1.getLikes()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional
    public void moderateReview(Long reviewId, boolean approved) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setApproved(approved);
        reviewRepository.save(review);
        
        if (approved) {
            productService.updateProductRating(review.getProduct());
        }
    }

    public List<Review> getApprovedReviews(Product product) {
        return reviewRepository.findByProductAndApprovedTrue(product);
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    @Transactional
    public void likeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setLikes(review.getLikes() + 1);
        reviewRepository.save(review);
    }

    @Transactional
    public void dislikeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setDislikes(review.getDislikes() + 1);
        reviewRepository.save(review);
    }

    public List<Review> getRecentReviews(Product product, int limit) {
        return reviewRepository.findByProductOrderByCreatedAtDesc(product)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Review> getTopRatedReviews(Product product, int limit) {
        return reviewRepository.findByProductOrderByRatingDesc(product)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean hasUserReviewedProduct(User user, Product product) {
        return !reviewRepository.findByUserAndProduct(user, product).isEmpty();
    }
} 