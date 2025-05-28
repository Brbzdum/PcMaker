package ru.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO для корзины покупок
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private int itemCount;
    private BigDecimal totalPrice;
} 