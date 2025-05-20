package ru.compshp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "pc_configurations")
@NoArgsConstructor
@AllArgsConstructor
public class PCConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "power_requirement")
    private Integer powerRequirement;

    @Column(name = "performance_score")
    private Double performanceScore;

    @Column(name = "is_compatible")
    private Boolean isCompatible;

    @Column(name = "compatibility_notes", columnDefinition = "jsonb")
    private String compatibilityNotes;

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConfigComponent> components = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        isPublic = false;
        viewCount = 0L;
        likeCount = 0L;
        isCompatible = true;
        performanceScore = 0.0;
        powerRequirement = 0;
        totalPrice = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addComponent(Product product) {
        ConfigComponent component = new ConfigComponent();
        component.setConfiguration(this);
        component.setProduct(product);
        components.add(component);
        updateTotalPrice();
    }

    public void removeComponent(Product product) {
        components.removeIf(c -> c.getProduct().equals(product));
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPrice = components.stream()
            .map(c -> c.getProduct().getPrice().multiply(BigDecimal.valueOf(c.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isComplete() {
        return components.size() == ComponentType.values().length;
    }

    public List<Product> getMissingComponentTypes() {
        List<Product> missing = new ArrayList<>();
        Set<ComponentType> existingTypes = components.stream()
            .map(c -> c.getProduct().getType())
            .collect(Collectors.toSet());

        for (ComponentType type : ComponentType.values()) {
            if (!existingTypes.contains(type)) {
                missing.add(null); // Placeholder for missing component type
            }
        }
        return missing;
    }

    public void incrementViewCount() {
        viewCount++;
    }

    public void incrementLikeCount() {
        likeCount++;
    }

    public void decrementLikeCount() {
        if (likeCount > 0) {
            likeCount--;
        }
    }

    // TODO: Добавить метод для расчета общей стоимости конфигурации
    // TODO: Добавить метод для проверки совместимости всех компонентов
    // TODO: Добавить метод для добавления/удаления компонентов
    // TODO: Добавить метод для клонирования конфигурации
    // TODO: Добавить метод для экспорта конфигурации в PDF
    // TODO: Добавить метод для проверки наличия всех необходимых компонентов
    // TODO: Добавить метод для расчета энергопотребления
} 