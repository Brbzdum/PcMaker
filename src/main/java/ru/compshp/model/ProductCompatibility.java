package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product_compatibilities")
public class ProductCompatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_product_id", nullable = false)
    private Product sourceProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_product_id", nullable = false)
    private Product targetProduct;

    @Column(nullable = false)
    private Boolean valid;

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

    // TODO: Добавить метод для проверки совместимости продуктов
    // TODO: Добавить метод для применения правил совместимости
    // TODO: Добавить метод для обновления статуса совместимости
    // TODO: Добавить метод для получения всех совместимых продуктов
    // TODO: Добавить метод для валидации правил совместимости
} 