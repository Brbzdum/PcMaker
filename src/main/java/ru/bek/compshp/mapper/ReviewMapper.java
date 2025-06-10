package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.dto.ReviewResponseDto;
import ru.bek.compshp.model.Review;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между Review и ReviewResponseDto
 * Предотвращает циклические ссылки при сериализации
 */
@Component
public class ReviewMapper {
    
    /**
     * Преобразует Review в ReviewResponseDto
     * @param review отзыв
     * @return DTO отзыва
     */
    public ReviewResponseDto toDto(Review review) {
        if (review == null) {
            return null;
        }
        
        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .username(review.getUser() != null ? review.getUser().getUsername() : null)
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getTitle() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .isApproved(review.getIsApproved())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .reportCount(review.getReportCount())
                .isModerated(review.getIsModerated())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
    
    /**
     * Преобразует список Review в список ReviewResponseDto
     * @param reviews список отзывов
     * @return список DTO отзывов
     */
    public List<ReviewResponseDto> toDtoList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }
        
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
} 