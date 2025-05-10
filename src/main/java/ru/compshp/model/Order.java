package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.compshp.model.enums.OrderStatus;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "delivery_method")
    private String deliveryMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "return_status")
    private String returnStatus;

    @Column(name = "return_reason")
    private String returnReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // TODO: Добавить метод для расчета общей стоимости заказа
    // TODO: Добавить метод для изменения статуса заказа
    // TODO: Добавить метод для добавления товаров в заказ
    // TODO: Добавить метод для отмены заказа
    // TODO: Добавить метод для проверки возможности отмены
    // TODO: Добавить метод для получения истории статусов
    // TODO: Добавить метод для расчета скидок
} 