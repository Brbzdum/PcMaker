package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_compatibility")
public class ProductCompatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_source", nullable = false)
    private Product sourceProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id_target", nullable = false)
    private Product targetProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private CompatibilityRule rule;

    @Column(nullable = false)
    private Boolean valid = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // TODO: Добавить метод для проверки совместимости продуктов
    // TODO: Добавить метод для применения правил совместимости
    // TODO: Добавить метод для обновления статуса совместимости
    // TODO: Добавить метод для получения всех совместимых продуктов
    // TODO: Добавить метод для валидации правил совместимости
} 