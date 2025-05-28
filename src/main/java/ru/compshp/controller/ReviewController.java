package ru.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ReviewDto;
import ru.compshp.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Публичные эндпоинты
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reviewService.getProductReviews(productId, page, size);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<?> getProductRating(@PathVariable Long productId) {
        return reviewService.getProductRating(productId);
    }

    // Эндпоинты для авторизованных пользователей
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/product/{productId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.createReview(productId, reviewDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.updateReview(reviewId, reviewDto);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    // Эндпоинты для администраторов
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reviewService.getPendingReviews(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{reviewId}/moderate")
    public ResponseEntity<?> moderateReview(
            @PathVariable Long reviewId,
            @RequestParam boolean approved) {
        return reviewService.moderateReview(reviewId, approved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reported")
    public ResponseEntity<?> getReportedReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reviewService.getReportedReviews(page, size);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<?> reportReview(
            @PathVariable Long reviewId,
            @RequestParam String reason) {
        return reviewService.reportReview(reviewId, reason);
    }
} 