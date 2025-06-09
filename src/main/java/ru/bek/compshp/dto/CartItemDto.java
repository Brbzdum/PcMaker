package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.model.CartItem;

import java.math.BigDecimal;

/**
 * DTO для элемента корзины без циклических зависимостей
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    
    /**
     * Преобразует модель CartItem в CartItemDto
     * @param cartItem модель элемента корзины
     * @return DTO элемента корзины
     */
    public static CartItemDto fromEntity(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) {
            return null;
        }
        
        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        
        return CartItemDto.builder()
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getTitle())
                .productDescription(cartItem.getProduct().getDescription())
                .productImageUrl(cartItem.getProduct().getImagePath())
                .productPrice(price)
                .quantity(cartItem.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
} 