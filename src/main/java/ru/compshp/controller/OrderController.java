package ru.compshp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.compshp.model.Order;
import ru.compshp.model.OrderItem;
import ru.compshp.model.OrderStatus;
import ru.compshp.service.OrderService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestParam @NotNull Long userId) {
        return ResponseEntity.ok(orderService.createOrder(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable @NotNull Long orderId) {
        return orderService.getOrderById(orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable @NotNull OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable @NotNull Long orderId,
            @RequestBody @Validated Order order) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable @NotNull Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable @NotNull Long orderId,
            @RequestParam @NotNull OrderStatus status,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status, comment));
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable @NotNull Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addOrderItem(
            @PathVariable @NotNull Long orderId,
            @RequestParam @NotNull Long productId,
            @RequestParam @NotNull int quantity) {
        return ResponseEntity.ok(orderService.addOrderItem(orderId, productId, quantity));
    }

    @DeleteMapping("/{orderId}/items/{productId}")
    public ResponseEntity<Order> removeOrderItem(
            @PathVariable @NotNull Long orderId,
            @PathVariable @NotNull Long productId) {
        return ResponseEntity.ok(orderService.removeOrderItem(orderId, productId));
    }

    @PutMapping("/{orderId}/items/{productId}")
    public ResponseEntity<Order> updateOrderItemQuantity(
            @PathVariable @NotNull Long orderId,
            @PathVariable @NotNull Long productId,
            @RequestParam @NotNull int quantity) {
        return ResponseEntity.ok(orderService.updateOrderItemQuantity(orderId, productId, quantity));
    }

    @GetMapping("/{orderId}/total")
    public ResponseEntity<Double> getOrderTotal(@PathVariable @NotNull Long orderId) {
        return ResponseEntity.ok(orderService.calculateOrderTotal(orderId));
    }

    @GetMapping("/{orderId}/total-with-discount")
    public ResponseEntity<Double> getOrderTotalWithDiscount(
            @PathVariable @NotNull Long orderId,
            @RequestParam @NotNull double discountPercentage) {
        return ResponseEntity.ok(orderService.calculateOrderTotalWithDiscount(orderId, discountPercentage));
    }
} 