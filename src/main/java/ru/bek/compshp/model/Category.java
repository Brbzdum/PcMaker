package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель категории товаров
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"parent", "children", "products"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(unique = true)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    private List<Category> children = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @Column(name = "is_pc_component")
    private Boolean isPcComponent = false;
    
    @Builder.Default
    @Column(name = "is_peripheral")
    private Boolean isPeripheral = false;

    /**
     * Получает признак, является ли категория категорией компонентов ПК
     * @return true, если категория является категорией компонентов ПК
     */
    public Boolean getPcComponent() {
        return this.isPcComponent != null ? this.isPcComponent : false;
    }

    /**
     * Устанавливает признак, является ли категория категорией компонентов ПК
     * @param pcComponent признак категории компонентов ПК
     */
    public void setPcComponent(Boolean pcComponent) {
        this.isPcComponent = pcComponent != null ? pcComponent : false;
    }
    
    /**
     * Получает признак, является ли категория категорией периферии
     * @return true, если категория является категорией периферии
     */
    public Boolean getPeripheral() {
        return this.isPeripheral != null ? this.isPeripheral : false;
    }

    /**
     * Устанавливает признак, является ли категория категорией периферии
     * @param peripheral признак категории периферии
     */
    public void setPeripheral(Boolean peripheral) {
        this.isPeripheral = peripheral != null ? peripheral : false;
    }

    // Явно добавляем геттер и сеттер для JPA
    public Boolean getIsPcComponent() {
        return this.isPcComponent;
    }
    
    public void setIsPcComponent(Boolean isPcComponent) {
        this.isPcComponent = isPcComponent;
    }
    
    // Явно добавляем геттер и сеттер для JPA
    public Boolean getIsPeripheral() {
        return this.isPeripheral;
    }
    
    public void setIsPeripheral(Boolean isPeripheral) {
        this.isPeripheral = isPeripheral;
    }

    /**
     * Метод для совместимости с существующим кодом
     * @param mapper функция преобразования
     * @param <T> тип результата
     * @return преобразованный объект
     */
    public <T> T map(java.util.function.Function<Category, T> mapper) {
        return mapper.apply(this);
    }

    /**
     * Проверяет, содержится ли категория с указанным ID в дереве дочерних категорий
     * @param categoryId ID категории
     * @return true, если категория найдена
     */
    public boolean isPresent(Long categoryId) {
        if (this.id.equals(categoryId)) {
            return true;
        }
        
        for (Category child : children) {
            if (child.getId().equals(categoryId)) {
                return true;
            }
            // Опционально проверяем вложенные категории, но без рекурсии
            for (Category grandchild : child.getChildren()) {
                if (grandchild.getId().equals(categoryId)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return "Category{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", slug='" + slug + '\'' +
            ", isPcComponent=" + isPcComponent +
            ", isPeripheral=" + isPeripheral +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
} 