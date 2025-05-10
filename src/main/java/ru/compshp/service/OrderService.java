package ru.compshp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Order;
import ru.compshp.model.OrderStatusHistory;
import ru.compshp.model.User;
import ru.compshp.model.enums.OrderStatus;
import ru.compshp.repository.OrderRepository;
import ru.compshp.repository.OrderStatusHistoryRepository;
import ru.compshp.service.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, 
                       OrderStatusHistoryRepository statusHistoryRepository,
                       ProductService productService) {
        this.orderRepository = orderRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.productService = productService;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getByUserAndStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order changeStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // Save status change history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setComment("Changed from " + oldStatus + " to " + newStatus);
        statusHistoryRepository.save(history);

        // If order is cancelled, return items to stock
        if (newStatus == OrderStatus.CANCELLED) {
            order.getOrderItems().forEach(item -> 
                productService.updateProductStock(item.getProduct(), item.getQuantity()));
        }

        return order;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!canBeCancelled(order)) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        return changeStatus(orderId, OrderStatus.CANCELLED);
    }

    public boolean canBeCancelled(Order order) {
        return order.getStatus() == OrderStatus.PENDING || 
               order.getStatus() == OrderStatus.PROCESSING;
    }

    public double calculateTotalPrice(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getOrderItems().stream()
                        .mapToDouble(item -> item.getPrice().doubleValue() * item.getQuantity())
                        .sum())
                .orElse(0.0);
    }

    public List<OrderStatus> getOrderHistory(Long orderId) {
        return statusHistoryRepository.findByOrderId(orderId).stream()
                .map(OrderStatusHistory::getStatus)
                .collect(Collectors.toList());
    }

    public List<Order> getRecentOrders(User user, int limit) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Order> getActiveOrders(User user) {
        return orderRepository.findByUserAndStatusIn(user, 
                List.of(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPED));
    }

    public List<Order> getCompletedOrders(User user) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.COMPLETED);
    }

    public List<Order> getCancelledOrders(User user) {
        return orderRepository.findByUserAndStatus(user, OrderStatus.CANCELLED);
    }
} 