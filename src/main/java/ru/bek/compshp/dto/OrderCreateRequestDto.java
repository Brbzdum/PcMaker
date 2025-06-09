package ru.bek.compshp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для создания заказа из фронтенда
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {
    private String address;
    private String phone;
    private String fullName;
    private String paymentMethod;
    private String comment;
    private List<OrderItemRequestDto> items;
}