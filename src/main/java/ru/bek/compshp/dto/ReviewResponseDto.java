package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для ответа с отзывами
 * Используется для предотвращения циклических ссылок при сериализации
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private Long productId;
    
    private String productName;
    
    private Integer rating;
    
    private String comment;
    
    private Boolean isApproved;
    
    private Boolean isVerifiedPurchase;
    
    private Integer reportCount;
    
    private Boolean isModerated;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 