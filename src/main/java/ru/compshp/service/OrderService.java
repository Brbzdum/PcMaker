package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.*;
import ru.compshp.repository.*;
import ru.compshp.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления заказами
 * Основные функции:
 * - Создание и управление заказами
 * - Обработка заказов
 * - Управление статусами заказов
 * - Расчет стоимости заказов
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final PCConfigurationRepository pcConfigurationRepository;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrder(Long userId, String deliveryAddress, String deliveryMethod) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryMethod(deliveryMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cartService.calculateTotal(userId));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            OrderItemId orderItemId = new OrderItemId();
            orderItemId.setOrderId(order.getId());
            orderItemId.setProductId(cartItem.getProduct().getId());
            orderItem.setId(orderItemId);
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            // Update product stock
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartService.clearCart(userId);
        return order;
    }

    @Transactional
    public Order createOrderFromConfiguration(Long userId, Long configId, String deliveryAddress, String deliveryMethod) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        PCConfiguration config = pcConfigurationRepository.findById(configId)
            .orElseThrow(() -> new RuntimeException("Configuration not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryMethod(deliveryMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(config.getTotalPrice());
        order.setPcConfiguration(config);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);

        for (ConfigComponent component : config.getComponents()) {
            OrderItem orderItem = new OrderItem();
            OrderItemId orderItemId = new OrderItemId();
            orderItemId.setOrderId(order.getId());
            orderItemId.setProductId(component.getProduct().getId());
            orderItem.setId(orderItemId);
            orderItem.setOrder(order);
            orderItem.setProduct(component.getProduct());
            orderItem.setQuantity(component.getQuantity());
            orderItem.setPriceAtTime(component.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            // Update product stock
            Product product = component.getProduct();
            product.setStock(product.getStock() - component.getQuantity());
            productRepository.save(product);
        }

        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }
} 