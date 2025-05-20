package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "config_components")
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComponent {
    @EmbeddedId
    private ConfigComponentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("configId")
    @JoinColumn(name = "config_id")
    private PCConfiguration configuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

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

    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public int getTotalPowerConsumption() {
        return product.getPowerConsumption() * quantity;
    }

    public double getTotalPerformanceScore() {
        return product.getPerformanceScore() * quantity;
    }

    public boolean isInStock() {
        return product.getStock() >= quantity;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (newQuantity > product.getStock()) {
            throw new InsufficientStockException(product.getId(), newQuantity, product.getStock());
        }
        quantity = newQuantity;
    }

    public boolean isCompatibleWith(ConfigComponent other) {
        return product.isCompatibleWith(other.getProduct());
    }

    // TODO: Добавить метод для проверки совместимости с другими компонентами
    // TODO: Добавить метод для расчета стоимости компонента с учетом количества
    // TODO: Добавить метод для проверки наличия компонента на складе
    // TODO: Добавить метод для обновления количества компонента
} 