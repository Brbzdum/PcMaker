package ru.bek.compshp.mapper;

import org.springframework.stereotype.Component;
import ru.bek.compshp.dto.OrderCreateRequestDto;
import ru.bek.compshp.dto.OrderStatusUpdateRequest;
import ru.bek.compshp.dto.OrderStatusUpdateRequestDto;
import ru.bek.compshp.model.enums.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Маппер для преобразования DTO запросов в доменные объекты
 */
@Component
public class OrderDtoMapper {

    /**
     * Преобразует DTO запроса на создание заказа в Map для контроллера
     */
    public Map<String, Object> toOrderCreateMap(OrderCreateRequestDto dto) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("address", dto.getAddress());
        orderMap.put("phone", dto.getPhone());
        orderMap.put("fullName", dto.getFullName());
        orderMap.put("paymentMethod", dto.getPaymentMethod());
        orderMap.put("comment", dto.getComment());
        
        List<Map<String, Object>> items = new ArrayList<>();
        if (dto.getItems() != null) {
            dto.getItems().forEach(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productId", item.getProductId());
                itemMap.put("quantity", item.getQuantity());
                items.add(itemMap);
            });
        }
        orderMap.put("items", items);
        
        return orderMap;
    }
    
    /**
     * Преобразует DTO запроса на обновление статуса заказа в доменный объект
     */
    public OrderStatusUpdateRequest toOrderStatusUpdateRequest(OrderStatusUpdateRequestDto dto) {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        try {
            request.setStatus(OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимый статус заказа: " + dto.getStatus());
        }
        request.setComment(dto.getComment());
        return request;
    }
} 