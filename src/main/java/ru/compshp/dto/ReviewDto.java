package ru.compshp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных отзыва
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    
    private Long id;
    
    private Long userId;
    
    private Long productId;
    
    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    private Integer rating;
    
    @Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
    private String comment;
    
    private Boolean isVerifiedPurchase;
    
    private LocalDateTime createdAt;
    
    private String username;
    
    private String productName;
} 