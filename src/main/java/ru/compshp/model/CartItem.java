package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // TODO: Добавить метод для расчета стоимости позиции
    // TODO: Добавить метод для проверки наличия товара
    // TODO: Добавить метод для обновления количества
    // TODO: Добавить метод для проверки максимального количества
}

@Embeddable
@Data
class CartItemId implements Serializable {
    private Long cartId;
    private Long productId;
} 