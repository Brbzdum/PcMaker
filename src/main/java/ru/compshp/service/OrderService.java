package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Сервис для управления заказами
 * Основные функции:
 * - Создание и управление заказами
 * - Обработка заказов
 * - Управление статусами заказов
 * - Расчет стоимости заказов
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PCConfigurationRepository pcConfigurationRepository;
    private final ProductService productService;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_Id(userId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        final Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(calculateCartTotal(cart));
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // Создаем элементы заказа из корзины
        final Order finalOrder = order;
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            OrderItemId orderItemId = new OrderItemId(finalOrder.getId(), cartItem.getProduct().getId());
            orderItem.setId(orderItemId);
            orderItem.setOrder(finalOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            // Уменьшаем количество товара на складе
            productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        });

        // Очищаем корзину
        cartService.clearCart(userId);

        // Создаем запись в истории статусов
        createStatusHistory(order, OrderStatus.PENDING, "Заказ создан");

        return order;
    }

    @Transactional
    public Order createOrderFromConfiguration(Long userId, Long configId) {
        final PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new ResourceNotFoundException("Configuration", "id", configId));

        if (!config.getIsCompatible()) {
            throw new IllegalStateException("Конфигурация несовместима");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(config.getTotalPrice());
        order.setCreatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        // Создаем элементы заказа из конфигурации
        final Order finalOrder = order;
        config.getComponents().forEach(component -> {
            OrderItem orderItem = new OrderItem();
            OrderItemId orderItemId = new OrderItemId(finalOrder.getId(), component.getProduct().getId());
            orderItem.setId(orderItemId);
            orderItem.setOrder(finalOrder);
            orderItem.setProduct(component.getProduct());
            orderItem.setQuantity(1);
            orderItem.setPrice(component.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            // Уменьшаем количество товара на складе
            productService.updateStock(component.getProduct().getId(), 1);
        });

        // Создаем запись в истории статусов
        createStatusHistory(order, OrderStatus.PENDING, "Заказ создан из конфигурации");

        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String comment) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(newStatus);
        order = orderRepository.save(order);

        createStatusHistory(order, newStatus, comment);
        return order;
    }

    private void createStatusHistory(Order order, OrderStatus status, String comment) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setComment(comment);
        history.setChangedAt(LocalDateTime.now());
        orderStatusHistoryRepository.save(history);
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUser_Id(userId);
    }

    public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    private BigDecimal calculateCartTotal(Cart cart) {
        return cart.getItems().stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateOrderTotal(Order order) {
        return order.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
        stats.put("completedOrders", orderRepository.countByStatus(OrderStatus.DELIVERED));
        stats.put("cancelledOrders", orderRepository.countByStatus(OrderStatus.CANCELLED));
        return stats;
    }
} 