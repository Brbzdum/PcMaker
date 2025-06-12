package ru.bek.compshp.model.future;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.bek.compshp.model.PCConfiguration;
import ru.bek.compshp.model.Product;

import java.time.LocalDateTime;

/**
 * Модель компонента конфигурации с поддержкой дубликатов.
 * Этот класс заменит существующий ConfigComponent после миграции базы данных.
 */
@Data
@Entity
@Table(name = "config_components")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigComponentWithId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PCConfiguration configuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 