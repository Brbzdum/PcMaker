package ru.compshp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.compshp.model.Order;
import ru.compshp.model.OrderItem;
import ru.compshp.model.Product;
import ru.compshp.model.User;
import ru.compshp.repository.OrderRepository;
import ru.compshp.repository.ProductRepository;
import ru.compshp.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrder(User user, List<OrderItem> items) {
        Order order = new Order();
        order.setUser(user);
        order.setItems(items);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        
        // Рассчитываем общую стоимость
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            totalAmount = totalAmount.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(totalAmount);

        // Обновляем количество товаров
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
            .map(order -> {
                order.setStatus(status);
                if ("COMPLETED".equals(status)) {
                    order.setCompletedAt(LocalDateTime.now());
                }
                return orderRepository.save(order);
            })
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            if ("PENDING".equals(order.getStatus())) {
                // Возвращаем товары на склад
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
                order.setStatus("CANCELLED");
                orderRepository.save(order);
            }
        });
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public BigDecimal calculateOrderTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean validateOrderItems(List<OrderItem> items) {
        return items.stream().allMatch(item -> 
            productService.isInStock(item.getProduct().getId(), item.getQuantity())
        );
    }
} 