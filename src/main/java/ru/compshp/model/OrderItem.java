package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    // TODO: Добавить метод для расчета стоимости позиции
    // TODO: Добавить метод для проверки наличия товара
    // TODO: Добавить метод для обновления количества
    // TODO: Добавить метод для расчета скидки
}

@Embeddable
@Data
class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;
} 