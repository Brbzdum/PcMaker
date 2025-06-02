package ru.bek.compshp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания заказа
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;
    
    private Long configurationId;
    
    private String deliveryAddress;
    
    private String comment;
} 