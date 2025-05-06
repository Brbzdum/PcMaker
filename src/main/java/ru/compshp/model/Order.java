package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import ru.compshp.model.enums.OrderStatus;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivery_address", columnDefinition = "jsonb")
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items;

    // TODO: Добавить метод для расчета общей стоимости заказа
    // TODO: Добавить метод для изменения статуса заказа
    // TODO: Добавить метод для добавления товаров в заказ
    // TODO: Добавить метод для отмены заказа
    // TODO: Добавить метод для проверки возможности отмены
    // TODO: Добавить метод для получения истории статусов
    // TODO: Добавить метод для расчета скидок
} 