package ru.bek.compshp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.ReviewDto;
import ru.bek.compshp.dto.ReviewResponseDto;
import ru.bek.compshp.mapper.ReviewMapper;
import ru.bek.compshp.model.Review;
import ru.bek.compshp.service.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    // Публичные эндпоинты
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponseDto>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        List<ReviewResponseDto> reviewDtos = reviewMapper.toDtoList(reviews);
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Double> getProductRating(@PathVariable Long productId) {
        Double rating = reviewService.getProductRating(productId);
        return ResponseEntity.ok(rating);
    }

    // Эндпоинты для авторизованных пользователей
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDto reviewDto) {
        Review newReview = new Review();
        newReview.setRating(reviewDto.getRating());
        newReview.setComment(reviewDto.getComment());
        
        Review created = reviewService.createReview(newReview, reviewDto.getUserId(), productId);
        ReviewResponseDto responseDto = reviewMapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto reviewDto) {
        Review updateData = new Review();
        updateData.setRating(reviewDto.getRating());
        updateData.setComment(reviewDto.getComment());
        
        Review updated = reviewService.updateReview(reviewId, updateData);
        ReviewResponseDto responseDto = reviewMapper.toDto(updated);
        return ResponseEntity.ok(responseDto);
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
    public ResponseEntity<List<ReviewResponseDto>> getPendingReviews() {
        List<Review> pendingReviews = reviewService.getPendingReviews();
        List<ReviewResponseDto> reviewDtos = reviewMapper.toDtoList(pendingReviews);
        return ResponseEntity.ok(reviewDtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{reviewId}/moderate")
    public ResponseEntity<ReviewResponseDto> moderateReview(
            @PathVariable Long reviewId,
            @RequestParam boolean approved) {
        Review moderatedReview = reviewService.moderateReview(reviewId, approved);
        ReviewResponseDto responseDto = reviewMapper.toDto(moderatedReview);
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reported")
    public ResponseEntity<List<ReviewResponseDto>> getReportedReviews() {
        // Получаем отзывы с количеством жалоб > 0
        List<Review> reportedReviews = reviewService.findByModeratedAndApproved(false, false);
        List<ReviewResponseDto> reviewDtos = reviewMapper.toDtoList(reportedReviews);
        return ResponseEntity.ok(reviewDtos);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{reviewId}/report")
    public ResponseEntity<ReviewResponseDto> reportReview(
            @PathVariable Long reviewId,
            @RequestParam String reason) {
        Review reported = reviewService.reportReview(reviewId);
        ReviewResponseDto responseDto = reviewMapper.toDto(reported);
        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping("/analytics/product/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewAnalytics(@PathVariable Long productId) {
        Map<String, Object> analytics = reviewService.getReviewAnalytics(productId);
        return ResponseEntity.ok(analytics);
    }
} 