package ru.bek.compshp.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.bek.compshp.model.enums.ComponentType;

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
@Getter
@Setter
@EqualsAndHashCode(exclude = {"manufacturer", "category", "reviews"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о товаре в магазине компьютерных комплектующих")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор товара", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Название товара", example = "NVIDIA GeForce RTX 3080")
    private String title;

    @Column(length = 2000, nullable = false)
    @Schema(description = "Подробное описание товара", example = "Видеокарта NVIDIA GeForce RTX 3080 с 10 ГБ памяти GDDR6X...")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Цена товара", example = "69999.99")
    private BigDecimal price;

    @Column(name = "image_path")
    @Schema(description = "Путь к изображению товара", example = "/images/products/rtx3080.jpg")
    private String imagePath;

    @Column(nullable = false)
    @Schema(description = "Количество товара на складе", example = "10")
    private Integer stock;

    @Builder.Default
    @Column(name = "is_active")
    @Schema(description = "Флаг активности товара (доступен ли для продажи)", example = "true")
    private Boolean isActive = true;

    @Column(name = "component_type")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Тип компонента компьютера", example = "GPU")
    private ComponentType componentType;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", nullable = false)
    @ToString.Exclude
    @Schema(description = "Производитель товара")
    private Manufacturer manufacturer;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @Schema(description = "Категория товара")
    private Category category;

    @Builder.Default
    @Type(JsonBinaryType.class)
    @Column(name = "specs", columnDefinition = "jsonb")
    @Schema(description = "Технические характеристики товара", example = "{\"cores\": \"8\", \"frequency\": \"3.8 GHz\"}")
    private Map<String, String> specs = new HashMap<>();

    @Column(name = "created_at")
    @Schema(description = "Дата и время создания записи о товаре", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Дата и время последнего обновления записи о товаре", example = "2023-01-02T15:30:00")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    @Schema(description = "Отзывы о товаре")
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
    
    /**
     * Проверяет, является ли продукт периферийным устройством
     * @return true, если продукт является периферийным устройством
     */
    public boolean isPeripheral() {
        return category != null && category.getIsPeripheral();
    }
    
    /**
     * Получает тип продукта (компонент или периферия) в виде строки
     * @return строковое представление типа продукта
     */
    public String getProductType() {
        if (componentType != null) {
            return componentType.name();
        } else if (category != null && category.getIsPeripheral()) {
            // Для периферии используем slug категории в нижнем регистре
            if (category.getSlug() != null) {
                String slug = category.getSlug().toLowerCase();
                // Маппинг известных slug'ов к типам периферии
                switch (slug) {
                    case "monitors": return "monitor";
                    case "keyboards": return "keyboard";
                    case "mice": return "mouse";
                    case "headsets": case "headphones": return "headset";
                    case "speakers": return "speakers";
                    case "mousepads": return "mousepad";
                    case "microphones": return "microphone";
                    default: return slug; // Используем slug как тип, если нет специального маппинга
                }
            }
            // Если slug отсутствует, возвращаем имя категории в нижнем регистре
            if (category.getName() != null) {
                return category.getName().toLowerCase();
            }
        }
        
        return "unknown";
    }
    
    @Override
    public String toString() {
        return "Product{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", price=" + price +
            ", stock=" + stock +
            ", isActive=" + isActive +
            ", componentType=" + componentType +
            ", category=" + (category != null ? category.getName() : "null") +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
} 