package ru.compshp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.ReviewDto;
import ru.compshp.service.ReviewService;
import ru.compshp.service.ProductService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;

    public ReviewController(ReviewService reviewService, ProductService productService) {
        this.reviewService = reviewService;
        this.productService = productService;
    }

    // TODO: Получить отзывы о товаре
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> listProductReviews(@PathVariable Long productId) {
        // TODO: Вернуть список отзывов о товаре
        return ResponseEntity.ok(/* список отзывов */ null);
    }

    // TODO: Создать отзыв
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        // TODO: Создать отзыв
        return ResponseEntity.ok(/* reviewDto */ null);
    }

    // TODO: Получить отзыв для редактирования
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        // TODO: Получить отзыв по ID
        return ResponseEntity.ok(/* reviewDto */ null);
    }

    // TODO: Обновить отзыв
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @RequestBody ReviewDto reviewDto) {
        // TODO: Обновить отзыв
        return ResponseEntity.ok(/* reviewDto */ null);
    }

    // TODO: Удалить отзыв
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        // TODO: Удалить отзыв
        return ResponseEntity.ok().build();
    }

    // TODO: Методы для получения отзывов пользователя, популярных отзывов, модерации и т.д.
} 