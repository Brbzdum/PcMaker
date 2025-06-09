package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bek.compshp.model.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO для корзины пользователя без циклических зависимостей
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private Long id;
    private Long userId;
    private String userName;
    private List<CartItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Преобразует модель Cart в CartDto
     * @param cart модель корзины
     * @return DTO корзины
     */
    public static CartDto fromEntity(Cart cart) {
        if (cart == null) {
            return null;
        }
        
        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .userName(cart.getUser() != null ? cart.getUser().getUsername() : null)
                .items(cart.getItems().stream()
                        .map(CartItemDto::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
} 