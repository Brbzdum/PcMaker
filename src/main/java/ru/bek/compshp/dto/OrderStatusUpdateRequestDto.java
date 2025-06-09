package ru.bek.compshp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на обновление статуса заказа с фронтенда
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDto {
    @NotNull(message = "Статус не может быть пустым")
    private String status;
    
    private String comment;
} 