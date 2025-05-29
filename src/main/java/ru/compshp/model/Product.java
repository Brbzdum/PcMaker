package ru.compshp.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.compshp.model.enums.ComponentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Модель товара
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "image_path")
    private String imagePath;

    @Column(nullable = false)
    private Integer stock;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type")
    private ComponentType componentType;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @Type(JsonBinaryType.class)
    @Column(name = "specs", columnDefinition = "jsonb")
    private Map<String, String> specs = new HashMap<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @OneToMany(mappedBy = "product")
    private Set<Review> reviews = new HashSet<>();
    
    /**
     * Получает значение спецификации по указанному ключу
     * @param key ключ спецификации
     * @return значение спецификации или пустая строка, если спецификация не найдена
     */
    public String getSpec(String key) {
        if (specs == null) {
            return "";
        }
        return specs.getOrDefault(key, "");
    }

    /**
     * Получает количество товара на складе
     * @return количество товара на складе
     */
    public Integer getStockQuantity() {
        return this.stock;
    }
    
    /**
     * Получает название продукта (используется для совместимости)
     * @return название продукта
     */
    public String getName() {
        return this.title;
    }
    
    /**
     * Получает все спецификации продукта
     * @return спецификации продукта
     */
    public Map<String, String> getSpecifications() {
        return this.specs;
    }
    
    /**
     * Рассчитывает и возвращает средний рейтинг продукта на основе отзывов
     * @return средний рейтинг или 0, если отзывов нет
     */
    public BigDecimal getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        return BigDecimal.valueOf(averageRating);
    }
} 