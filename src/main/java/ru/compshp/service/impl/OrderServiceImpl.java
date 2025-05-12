package ru.compshp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.exception.*;
import ru.compshp.model.*;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.repository.*;
import ru.compshp.service.CartService;
import ru.compshp.service.OrderService;
import ru.compshp.service.PricingService;
import ru.compshp.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final PricingService pricingService;
    private final OrderStatusHistoryRepository statusHistoryRepository;

    @Override
    @Transactional
    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("EMPTY_CART", "Cannot create order from empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Переносим товары из корзины в заказ
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(pricingService.calculatePrice(cartItem.getProduct()));
            order.getItems().add(orderItem);
        }

        order = orderRepository.save(order);
        createStatusHistory(order, OrderStatus.PENDING, "Order created");
        cartService.clearCart(userId);

        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(Long orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isOrderEditable(orderId)) {
            throw new BusinessException("ORDER_NOT_EDITABLE", "Order cannot be edited in current status");
        }

        order.setShippingAddress(orderDetails.getShippingAddress());
        order.setPaymentMethod(orderDetails.getPaymentMethod());
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isOrderCancellable(orderId)) {
            throw new BusinessException("ORDER_NOT_CANCELLABLE", "Order cannot be cancelled in current status");
        }

        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        return orderRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String comment) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new InvalidOrderStatusException(order.getStatus(), newStatus);
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        createStatusHistory(order, newStatus, comment);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatusAndDateRange(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate);
    }

    @Override
    @Transactional
    public Order addOrderItem(Long orderId, Long productId, int quantity) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isOrderEditable(orderId)) {
            throw new BusinessException("ORDER_NOT_EDITABLE", "Order cannot be edited in current status");
        }

        Product product = productService.getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(pricingService.calculatePrice(product));
        order.getItems().add(orderItem);

        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order removeOrderItem(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isOrderEditable(orderId)) {
            throw new BusinessException("ORDER_NOT_EDITABLE", "Order cannot be edited in current status");
        }

        order.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderItemQuantity(Long orderId, Long productId, int quantity) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isOrderEditable(orderId)) {
            throw new BusinessException("ORDER_NOT_EDITABLE", "Order cannot be edited in current status");
        }

        OrderItem item = order.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ProductNotFoundException(productId));

        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }

        item.setQuantity(quantity);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        return order.getItems();
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateOrderTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order.getItems().stream()
            .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
            .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateOrderTotalWithDiscount(Long orderId, double discountPercentage) {
        double total = calculateOrderTotal(orderId);
        return total * (1 - discountPercentage / 100);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderCancellable(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        return order.getStatus() == OrderStatus.PENDING;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderEditable(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        return order.getStatus() == OrderStatus.PENDING;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOrderPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        return order.getStatus() == OrderStatus.PAID;
    }

    private void createStatusHistory(Order order, OrderStatus status, String comment) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setComment(comment);
        history.setCreatedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Определяем допустимые переходы статусов
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.RETURNED;
            case DELIVERED -> newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.RETURNED;
            case CANCELLED, COMPLETED, RETURNED -> false;
        };
    }
} 