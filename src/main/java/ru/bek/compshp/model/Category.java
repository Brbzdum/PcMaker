package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель категории товаров
 */
@Entity
@Table(name = "categories")
@Data
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
    private Category parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Builder.Default
    @Column(name = "is_pc_component")
    private Boolean isPcComponent = false;

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

    // Явно добавляем геттер и сеттер для JPA
    public Boolean getIsPcComponent() {
        return this.isPcComponent;
    }
    
    public void setIsPcComponent(Boolean isPcComponent) {
        this.isPcComponent = isPcComponent;
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
} 