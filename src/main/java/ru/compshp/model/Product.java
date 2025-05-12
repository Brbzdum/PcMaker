package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.model.enums.ProductCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type")
    private ComponentType componentType;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String specs;

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Column(name = "ratings_count")
    private Integer ratingsCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "physical_specs", columnDefinition = "jsonb")
    private String physicalSpecs;

    @Column(name = "compatibility_specs", columnDefinition = "jsonb")
    private String compatibilitySpecs;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    @Column
    private BigDecimal discount;

    @Column
    private Double rating;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // TODO: Реализовать валидацию типа компонента в зависимости от категории товара
    // TODO: Добавить методы для управления складом (увеличение/уменьшение количества)
    // TODO: Добавить методы для расчета рейтинга товара
    // TODO: Добавить методы для расчета цен и скидок
    // TODO: Реализовать проверку совместимости компонентов

    public enum ProductType {
        CPU,
        MOTHERBOARD,
        GPU,
        RAM,
        STORAGE,
        PSU,
        CASE,
        COOLER,
        PERIPHERAL,
        READY_PC
    }
} 