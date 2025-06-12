package ru.bek.compshp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель конфигурации ПК
 */
@Entity
@Table(name = "pc_configurations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "is_compatible")
    private Boolean isCompatible;

    // Поле для хранения общей производительности
    @Builder.Default
    @Column(name = "total_performance")
    private Double totalPerformance = 0.0;

    @Builder.Default
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ConfigComponent> components = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Геттер и сеттер для совместимости с существующим кодом
    public Double getTotalPerformance() {
        return totalPerformance == null ? 0.0 : totalPerformance;
    }

    public void setTotalPerformance(Double totalPerformance) {
        this.totalPerformance = totalPerformance;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Добавляет компонент в конфигурацию с правильной установкой двусторонней связи
     * @param component компонент для добавления
     */
    public void addComponent(ConfigComponent component) {
        components.add(component);
        component.setConfiguration(this);
    }
} 