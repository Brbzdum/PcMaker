package ru.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.compshp.dto.OrderCreateRequest;
import ru.compshp.dto.OrderResponse;
import ru.compshp.dto.OrderStatusUpdateRequest;
import ru.compshp.model.Order;
import ru.compshp.model.OrderStatusHistory;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы", description = "API для управления заказами")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Получить все заказы пользователя")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@RequestParam Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> response = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(mapToOrderResponse(order));
    }

    @PostMapping("/from-cart")
    @Operation(summary = "Создать заказ из корзины")
    public ResponseEntity<OrderResponse> createOrderFromCart(@RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrderFromCart(request.getUserId());
        return new ResponseEntity<>(mapToOrderResponse(order), HttpStatus.CREATED);
    }

    @PostMapping("/from-configuration")
    @Operation(summary = "Создать заказ из конфигурации ПК")
    public ResponseEntity<OrderResponse> createOrderFromConfiguration(
            @RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrderFromConfiguration(
                request.getUserId(), request.getConfigurationId());
        return new ResponseEntity<>(mapToOrderResponse(order), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Обновить статус заказа")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        Order order = orderService.updateOrderStatus(
                orderId, request.getStatus(), request.getComment());
        return ResponseEntity.ok(mapToOrderResponse(order));
    }

    @GetMapping("/{orderId}/history")
    @Operation(summary = "Получить историю статусов заказа")
    public ResponseEntity<List<Map<String, Object>>> getOrderStatusHistory(@PathVariable Long orderId) {
        List<OrderStatusHistory> history = orderService.getOrderStatusHistory(orderId);
        List<Map<String, Object>> response = history.stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-status")
    @Operation(summary = "Получить заказы по статусу")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@RequestParam OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderResponse> response = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Получить заказы по диапазону дат")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<OrderResponse> response = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Получить статистику по заказам")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        return ResponseEntity.ok(orderService.getOrderStatistics());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<Map<String, Object>> itemsList = new ArrayList<>();
        order.getItems().forEach(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProduct().getId());
            itemMap.put("productName", item.getProduct().getTitle());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("price", item.getPrice());
            itemsList.add(itemMap);
        });
        
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .deliveryAddress(order.getDeliveryAddress().toString())
                .items(itemsList)
                .build();
    }

    private Map<String, Object> mapToHistoryResponse(OrderStatusHistory history) {
        Map<String, Object> historyMap = new HashMap<>();
        historyMap.put("id", history.getId());
        historyMap.put("status", history.getStatus());
        historyMap.put("comment", history.getComment() != null ? history.getComment() : "");
        historyMap.put("changedAt", history.getChangedAt());
        return historyMap;
    }
} 