package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.config.DateUtils;
import ru.bek.compshp.dto.OrderDto;
import ru.bek.compshp.dto.OrderItemDto;
import ru.bek.compshp.dto.OrderResponse;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования между сущностями Order и DTO
 */
@Component
public class OrderMapper {

    /**
     * Преобразует сущность Order в OrderDTO
     */
    public OrderDto toDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        List<OrderItemDto> itemDTOs = order.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
        
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .address(order.getDeliveryAddress() != null ? 
                        order.getDeliveryAddress().get("fullAddress").toString() : null)
                .phone(order.getPhone())
                .fullName(order.getFullName())
                .paymentMethod(order.getPaymentMethod())
                .comment(order.getComment())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDTOs)
                .build();
    }
    
    /**
     * Преобразует сущность OrderItem в OrderItemDTO
     */
    public OrderItemDto toItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        
        BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return OrderItemDto.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getTitle())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
    
    /**
     * Преобразует OrderDTO в OrderResponse
     */
    public OrderResponse toResponse(OrderDto dto) {
        if (dto == null) {
            return null;
        }
        
        List<Map<String, Object>> itemsList = dto.getItems().stream()
                .map(this::itemToMap)
                .collect(Collectors.toList());
        
        // Форматируем даты в ISO формат
        LocalDateTime createdAt = dto.getCreatedAt();
        LocalDateTime updatedAt = dto.getUpdatedAt();
        
        return OrderResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .status(dto.getStatus())
                .total(dto.getTotalPrice())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .fullName(dto.getFullName())
                .paymentMethod(dto.getPaymentMethod())
                .comment(dto.getComment())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .items(itemsList)
                .build();
    }
    
    /**
     * Преобразует OrderItemDTO в Map для ответа API
     */
    private Map<String, Object> itemToMap(OrderItemDto item) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("productId", item.getProductId());
        itemMap.put("productName", item.getProductName());
        itemMap.put("productPrice", item.getPrice());
        itemMap.put("quantity", item.getQuantity());
        itemMap.put("totalPrice", item.getTotalPrice());
        return itemMap;
    }
    
    /**
     * Преобразует сущность Order напрямую в OrderResponse
     */
    public OrderResponse entityToResponse(Order order) {
        return toResponse(toDTO(order));
    }
} 