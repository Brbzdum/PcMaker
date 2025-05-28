package ru.compshp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.compshp.model.enums.OrderStatus;

/**
 * DTO для обновления статуса заказа
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "Статус не может быть пустым")
    private OrderStatus status;
    
    private String comment;
} 