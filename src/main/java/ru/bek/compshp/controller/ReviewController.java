package ru.bek.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.ReviewDto;
import ru.bek.compshp.model.Review;
import ru.bek.compshp.service.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Публичные эндпоинты
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Double> getProductRating(@PathVariable Long productId) {
        Double rating = reviewService.getProductRating(productId);
        return ResponseEntity.ok(rating);
    }

    // Эндпоинты для авторизованных пользователей
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/product/{productId}")
    public ResponseEntity<Review> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDto reviewDto) {
        Review newReview = new Review();
        newReview.setRating(reviewDto.getRating());
        newReview.setComment(reviewDto.getComment());
        
        Review created = reviewService.createReview(newReview, reviewDto.getUserId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto reviewDto) {
        Review updateData = new Review();
        updateData.setRating(reviewDto.getRating());
        updateData.setComment(reviewDto.getComment());
        
        Review updated = reviewService.updateReview(reviewId, updateData);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // Эндпоинты для администраторов
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        List<Review> pendingReviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(pendingReviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{reviewId}/moderate")
    public ResponseEntity<Review> moderateReview(
            @PathVariable Long reviewId,
            @RequestParam boolean approved) {
        Review moderatedReview = reviewService.moderateReview(reviewId, approved);
        return ResponseEntity.ok(moderatedReview);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reported")
    public ResponseEntity<List<Review>> getReportedReviews() {
        // Получаем отзывы с количеством жалоб > 0
        List<Review> reportedReviews = reviewService.findByModeratedAndApproved(false, false);
        return ResponseEntity.ok(reportedReviews);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Review> reportReview(
            @PathVariable Long reviewId,
            @RequestParam String reason) {
        Review reported = reviewService.reportReview(reviewId);
        return ResponseEntity.ok(reported);
    }
    
    @GetMapping("/analytics/product/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewAnalytics(@PathVariable Long productId) {
        Map<String, Object> analytics = reviewService.getReviewAnalytics(productId);
        return ResponseEntity.ok(analytics);
    }
} 