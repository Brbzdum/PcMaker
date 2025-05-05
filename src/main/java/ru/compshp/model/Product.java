package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type")
    private ComponentType componentType;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String specs;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "ratings_count")
    private Integer ratingsCount;

    // TODO: Реализовать валидацию типа компонента в зависимости от категории товара
    // TODO: Добавить методы для управления складом (увеличение/уменьшение количества)
    // TODO: Добавить методы для расчета рейтинга товара
    // TODO: Добавить методы для расчета цен и скидок
    // TODO: Реализовать проверку совместимости компонентов
} 