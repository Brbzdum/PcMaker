package ru.bek.compshp.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import ru.bek.compshp.model.enums.ComponentType;
import ru.bek.compshp.util.PostgreSQLEnumConverter;

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
    @Convert(converter = PostgreSQLEnumConverter.class)
    @Schema(description = "Тип компонента компьютера", example = "GPU")
    private ComponentType componentType;

    @ManyToOne
    @JoinColumn(name = "manufacturer_id", nullable = false)
    @Schema(description = "Производитель товара")
    private Manufacturer manufacturer;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
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
} 