package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
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