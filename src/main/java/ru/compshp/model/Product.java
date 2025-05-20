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

    @OneToMany(mappedBy = "product")
    private Set<Review> reviews = new HashSet<>();

    @Column
    private BigDecimal discount;

    @Column
    private Double rating;

    @Column(name = "power_consumption")
    private Integer powerConsumption;

    @Column(name = "performance_score")
    private Double performanceScore;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 0;
        if (ratingsCount == null) ratingsCount = 0;
        if (averageRating == null) averageRating = BigDecimal.ZERO;
        if (isActive == null) isActive = true;
        if (discount == null) discount = BigDecimal.ZERO;
        if (rating == null) rating = 0.0;
        if (powerConsumption == null) powerConsumption = 0;
        if (performanceScore == null) performanceScore = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void increaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        stock += amount;
    }

    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (stock < amount) {
            throw new IllegalStateException("Insufficient stock");
        }
        stock -= amount;
    }

    public void updateRating(Double newRating) {
        if (newRating < 0 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        double totalRating = (rating * ratingsCount) + newRating;
        ratingsCount++;
        rating = totalRating / ratingsCount;
    }

    public BigDecimal getDiscountedPrice() {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return price;
        }
        return price.multiply(BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100"))));
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean isCompatibleWith(Product other) {
        if (componentType == null || other.getComponentType() == null) {
            return true; // Non-component products are always compatible
        }
        // TODO: Implement actual compatibility check based on specs
        return true;
    }

    public void validateComponentType() {
        if (componentType == null) {
            return; // Not a component
        }
        if (category == null) {
            throw new IllegalStateException("Category must be set for components");
        }
        // TODO: Implement validation logic based on category
    }

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