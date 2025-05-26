package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Модель для товаров в корзине
 */
@Data
@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @EmbeddedId
    private CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at")
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

    /**
     * Рассчитывает общую стоимость позиции в корзине
     */
    public BigDecimal calculateTotal() {
        if (product == null || product.getPrice() == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public boolean isInStock() {
        return product.getStock() >= quantity;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        this.quantity = newQuantity;
    }

    public boolean isMaxQuantityReached() {
        return quantity >= product.getStock();
    }

    /**
     * Инициализирует составной ключ
     */
    @PrePersist
    @PreUpdate
    public void onSave() {
        if (id == null) {
            id = new CartItemId();
        }
        if (cart != null) {
            id.setCartId(cart.getId());
        }
        if (product != null) {
            id.setProductId(product.getId());
        }
    }
}

