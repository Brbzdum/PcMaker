package ru.bek.compshp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.bek.compshp.dto.*;
import ru.bek.compshp.mapper.OrderDtoMapper;
import ru.bek.compshp.mapper.OrderMapper;
import ru.bek.compshp.model.Order;
import ru.bek.compshp.model.OrderStatusHistory;
import ru.bek.compshp.model.enums.OrderStatus;
import ru.bek.compshp.security.CustomUserDetails;
import ru.bek.compshp.service.OrderService;

import java.time.LocalDateTime;
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
    private final OrderMapper orderMapper;
    private final OrderDtoMapper orderDtoMapper;

    @GetMapping
    @Operation(summary = "Получить все заказы пользователя")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        List<Order> orders = orderService.getUserOrders(userPrincipal.getId());
        List<OrderResponse> response = orders.stream()
                .map(orderMapper::entityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(orderMapper.entityToResponse(order));
    }

    @PostMapping
    @Operation(summary = "Создать заказ")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequestDto requestDTO,
                                                    @AuthenticationPrincipal CustomUserDetails userPrincipal) {
        Map<String, Object> orderData = orderDtoMapper.toOrderCreateMap(requestDTO);
        
        // Создаем заказ
        Order order = orderService.createOrder(
            userPrincipal.getId(), 
            (String) orderData.get("address"), 
            (String) orderData.get("phone"), 
            (String) orderData.get("fullName"), 
            (String) orderData.get("paymentMethod"), 
            (String) orderData.get("comment"), 
            (List<Map<String, Object>>) orderData.get("items")
        );
        
        return new ResponseEntity<>(orderMapper.entityToResponse(order), HttpStatus.CREATED);
    }

    @PostMapping("/from-cart")
    @Operation(summary = "Создать заказ из корзины")
    public ResponseEntity<OrderResponse> createOrderFromCart(@RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrderFromCart(
                request.getUserId(),
                request.getAddress(),
                request.getPhone(),
                request.getFullName(),
                request.getPaymentMethod(),
                request.getComment()
        );
        return new ResponseEntity<>(orderMapper.entityToResponse(order), HttpStatus.CREATED);
    }

    @PostMapping("/from-configuration")
    @Operation(summary = "Создать заказ из конфигурации ПК")
    public ResponseEntity<OrderResponse> createOrderFromConfiguration(
            @RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrderFromConfiguration(
                request.getUserId(),
                request.getConfigurationId(),
                request.getAddress(),
                request.getPhone(),
                request.getFullName(),
                request.getPaymentMethod(),
                request.getComment()
        );
        return new ResponseEntity<>(orderMapper.entityToResponse(order), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Обновить статус заказа")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequestDto requestDTO) {
        OrderStatusUpdateRequest request = orderDtoMapper.toOrderStatusUpdateRequest(requestDTO);
        Order order = orderService.updateOrderStatus(
                orderId, request.getStatus(), request.getComment());
        return ResponseEntity.ok(orderMapper.entityToResponse(order));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Отменить заказ")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED, "Заказ отменен пользователем");
        return ResponseEntity.ok(orderMapper.entityToResponse(order));
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
                .map(orderMapper::entityToResponse)
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
                .map(orderMapper::entityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Получить статистику по заказам")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        return ResponseEntity.ok(orderService.getOrderStatistics());
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