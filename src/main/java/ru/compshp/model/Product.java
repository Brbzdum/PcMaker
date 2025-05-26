package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.compshp.model.enums.ComponentType;
import ru.compshp.model.enums.ProductCategory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    @ElementCollection
    @CollectionTable(name = "product_specs", 
        joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private Map<String, String> specs = new HashMap<>();

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Column(name = "ratings_count")
    private Integer ratingsCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product")
    private Set<Review> reviews = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 0;
        if (ratingsCount == null) ratingsCount = 0;
        if (averageRating == null) averageRating = BigDecimal.ZERO;
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getSpec(String key) {
        return specs.getOrDefault(key, "");
    }

    public void setSpec(String key, String value) {
        specs.put(key, value);
    }
} 