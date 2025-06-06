package ru.bek.compshp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для позиции в корзине покупок
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    
    @NotNull
    private Long productId;
    
    private String productName;
    private String productImageUrl;
    
    @NotNull
    @Min(1)
    private Integer quantity;
    
    private BigDecimal price;
    private BigDecimal totalPrice;
} 